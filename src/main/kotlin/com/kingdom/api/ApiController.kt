package com.kingdom.api

import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.RandomizingOptions
import com.kingdom.model.User
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.players.HumanPlayer
import com.kingdom.service.CardManager
import com.kingdom.service.GameMessageService
import com.kingdom.service.GameManager
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LobbyChats
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.KingdomUtil
import com.kingdom.util.USERNAME_COOKIE
import com.kingdom.util.removeSpaces
import com.kingdom.web.GameController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.Date
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api")
class ApiController(
        private val cardManager: CardManager,
        private val gameManager: GameManager,
        private val gameMessageService: GameMessageService,
        private val gameRoomManager: GameRoomManager,
        private val lobbyChats: LobbyChats,
        private val mapper: ApiSnapshotMapper,
        private val gameController: GameController
) {

    @PostMapping("/access")
    fun access(@RequestBody body: AccessRequest, response: HttpServletResponse): ApiMessageDto {
        if (body.password?.trim()?.toLowerCase() != ACCESS_PASSWORD) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid access password")
        }
        val accessCookie = Cookie("kingdomaccess", ACCESS_PASSWORD)
        accessCookie.maxAge = 24 * 60 * 60 * 365
        accessCookie.path = "/"
        response.addCookie(accessCookie)
        return ApiMessageDto("Access granted")
    }

    @PostMapping("/session")
    fun session(@RequestBody body: SessionRequest, request: HttpServletRequest, response: HttpServletResponse): UserDto {
        if (!isAccessAllowed(request)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access password required")
        }
        val username = body.username?.trim()
        if (username.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
        }

        val usernameCookieValue = getUsernameCookie(request)
        val usernameMatchesCookie = username.removeSpaces().toLowerCase() == usernameCookieValue?.toLowerCase()
        val existingUser = LoggedInUsers.getUserByUsername(username)
        if (existingUser?.isExpired == true) {
            LoggedInUsers.userLoggedOut(existingUser)
        }
        if (LoggedInUsers.usernameBeingUsed(username) && !usernameMatchesCookie) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username is already being used")
        }

        val user = usernameCookieValue?.let { existingUser } ?: User()
        user.username = username
        user.isMobile = true
        KingdomUtil.addUsernameCookieToResponse(username, response)
        LoggedInUsers.userLoggedIn(user)
        LoggedInUsers.refreshLobbyPlayers()

        val session = request.getSession(true)
        session.maxInactiveInterval = 60 * 120
        session.setAttribute("user", user)
        session.setAttribute("mobile", true)
        user.gameId?.let { session.setAttribute("gameId", it) }

        return UserDto(user.userId, user.username, user.admin, user.gameId, user.status, user.isIdle)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ApiMessageDto {
        val user = requireUser(request)
        user.gameId?.let { gameId ->
            gameRoomManager.getGame(gameId)?.playerMap?.get(user.userId)?.let { player ->
                player.game.playerQuitGame(player)
                player.game.playerExitedGame(player)
            }
        }
        KingdomUtil.logoutUser(user, request)
        return ApiMessageDto("Logged out")
    }

    @GetMapping("/catalog")
    fun catalog(): CatalogDto = mapper.catalog()

    @GetMapping("/lobby")
    fun lobby(request: HttpServletRequest): LobbySnapshotDto {
        val user = requireUser(request)
        val game = getGameForUser(user)
        val startGame = game != null && game.status != GameStatus.WaitingForPlayers && game.status != GameStatus.Finished && game.playerMap.containsKey(user.userId)
        return mapper.lobby(user, lobbyChats.chats, startGame)
    }

    @PostMapping("/lobby/chat")
    fun lobbyChat(@RequestBody body: ChatRequest, request: HttpServletRequest): LobbySnapshotDto {
        val user = requireUser(request)
        val message = body.message?.trim()
        if (!message.isNullOrEmpty()) {
            lobbyChats.addChat(user, message)
            LoggedInUsers.refreshLobbyChat()
        }
        return lobby(request)
    }

    @PostMapping("/games")
    fun createGame(@RequestBody body: CreateGameRequest, request: HttpServletRequest): GameSnapshotDto {
        val user = requireUser(request)
        if (user.gameId != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User is already in a game")
        }

        val game = gameRoomManager.nextAvailableGame
                ?: throw ResponseStatusException(HttpStatus.CONFLICT, "Maximum game room limit reached")

        game.creatorId = user.userId
        game.creatorName = user.username
        game.title = body.title?.trim().takeUnless { it.isNullOrEmpty() } ?: "${user.username}'s Game"
        game.mobile = true
        game.isPrivateGame = body.privateGame ?: false
        game.password = body.password ?: ""
        game.isShowVictoryPoints = body.showVictoryPoints ?: true
        game.isIdenticalStartingHands = body.identicalStartingHands ?: false

        val botCount = (body.numEasyBots ?: 0) + (body.numMediumBots ?: 0) + (body.numHardBots ?: 0) + (body.numBigMoneyBots ?: 0)
        game.numPlayers = maxOf(body.numPlayers ?: maxOf(2, botCount + 1), botCount + 1)
        game.numComputerPlayers = botCount
        game.numEasyComputerPlayers = body.numEasyBots ?: 0
        game.numMediumComputerPlayers = body.numMediumBots ?: 0
        game.numHardComputerPlayers = body.numHardBots ?: 0
        game.numBMUComputerPlayers = body.numBigMoneyBots ?: 0
        game.isAllComputerOpponents = botCount == game.numPlayers - 1

        game.decks = (body.deckNames ?: listOf(Deck.Base.name))
                .mapNotNull { name -> Deck.values().firstOrNull { it.name.equals(name, true) } }
                .filterNot { it == Deck.None }
                .distinct()
                .toMutableList()
        if (game.decks.isEmpty()) {
            game.decks = mutableListOf(Deck.Base)
        }

        game.randomizingOptions = randomizingOptions(body)
        cardManager.setRandomKingdomCardsAndEvents(game)

        if (body.includeColonyAndPlatinum == true) {
            game.isAlwaysIncludeColonyAndPlatinum = true
        } else if (body.includeColonyAndPlatinum == false) {
            game.isNeverIncludeColonyAndPlatinum = true
        }
        body.includeShelters?.let {
            game.isIncludeShelters = it
            game.isExcludeShelters = !it
        }

        finalizeConfiguredGame(game, user)
        request.session.setAttribute("gameId", game.gameId)
        return mapper.snapshot(game, game.playerMap[user.userId]!!)
    }

    @PostMapping("/games/{gameId}/join")
    fun joinGame(@PathVariable gameId: String, @RequestBody body: JoinGameRequest, request: HttpServletRequest): GameSnapshotDto {
        val user = requireUser(request)
        if (user.gameId != null && user.gameId != gameId) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User is already in another game")
        }
        val game = requireGame(gameId)
        if (game.status != GameStatus.WaitingForPlayers && game.status != GameStatus.InProgress) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Game is not joinable")
        }
        if (game.isPrivateGame && body.password != game.password) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid game password")
        }
        if (!game.playerMap.containsKey(user.userId)) {
            if (game.players.size >= game.numPlayers) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Game room is full")
            }
            addPlayerToGame(game, user)
        }
        request.session.setAttribute("gameId", gameId)
        return mapper.snapshot(game, game.playerMap[user.userId]!!)
    }

    @PostMapping("/games/{gameId}/leave")
    fun leaveGame(@PathVariable gameId: String, request: HttpServletRequest): LobbySnapshotDto {
        val user = requireUser(request)
        val game = requireGame(gameId)
        if (game.status != GameStatus.WaitingForPlayers || user.gameId != gameId) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Cannot leave this game now")
        }
        removePlayerFromGame(game, user)
        request.session.removeAttribute("gameId")
        return lobby(request)
    }

    @PostMapping("/games/{gameId}/cancel")
    fun cancelGame(@PathVariable gameId: String, request: HttpServletRequest): LobbySnapshotDto {
        val user = requireUser(request)
        val game = requireGame(gameId)
        if (!user.admin && game.creatorId != user.userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can cancel this game")
        }
        if (game.status == GameStatus.InProgress) {
            game.addInfoLog("${user.username} cancelled the game")
            game.gameOver()
        } else {
            game.reset()
        }
        request.session.removeAttribute("gameId")
        return lobby(request)
    }

    @GetMapping("/games/{gameId}/snapshot")
    fun snapshot(@PathVariable gameId: String, request: HttpServletRequest): GameSnapshotDto {
        val user = requireUser(request)
        val game = requireGame(gameId)
        val player = game.playerMap[user.userId]
                ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not in this game")
        request.session.setAttribute("gameId", gameId)
        return mapper.snapshot(game, player)
    }

    @PostMapping("/games/{gameId}/commands")
    fun command(@PathVariable gameId: String, @RequestBody body: GameCommandRequest, request: HttpServletRequest): GameSnapshotDto {
        val user = requireUser(request)
        val game = requireGame(gameId)
        val player = game.playerMap[user.userId]
                ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not in this game")
        val type = body.type?.trim()?.toLowerCase()
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Command type is required")

        when (type) {
            "clickcard" -> {
                val cardId = body.cardId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cardId is required")
                val cardName = body.cardName ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "cardName is required")
                gameController.cardClicked(game, player, parseLocation(body.location), cardName, cardId)
            }
            "playalltreasures" -> {
                if (player.currentAction == null) {
                    player.playAllTreasureCards()
                }
            }
            "endturn" -> {
                if (player.currentAction == null) {
                    player.endTurn()
                } else {
                    game.refreshGame()
                }
            }
            "choice" -> {
                val action = player.currentAction ?: throw ResponseStatusException(HttpStatus.CONFLICT, "No action is pending")
                val choice = body.choice ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "choice is required")
                player.actionResult(action, ActionResult().apply { choiceSelected = choice })
            }
            "donotuse" -> {
                val action = player.currentAction ?: throw ResponseStatusException(HttpStatus.CONFLICT, "No action is pending")
                player.actionResult(action, ActionResult().apply { isDoNotUse = true })
            }
            "done" -> {
                val action = player.currentAction ?: throw ResponseStatusException(HttpStatus.CONFLICT, "No action is pending")
                player.actionResult(action, ActionResult().apply { isDoneWithAction = true })
            }
            "usecoffers" -> player.useCoffers((body.amount ?: 0).coerceIn(0, player.coffers))
            "usevillagers" -> player.useVillagers((body.amount ?: 0).coerceIn(0, player.villagers))
            "paydebt" -> player.payOffDebt()
            "showtaverncards" -> (player as? HumanPlayer)?.showTavernCards()
            "quit" -> game.playerQuitGame(player)
            "exit" -> game.playerExitedGame(player)
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown command type")
        }

        game.refreshPlayerCardAction(player)
        game.refreshSupply()
        player.refreshPlayerHandArea()
        return mapper.snapshot(game, player)
    }

    @PostMapping("/games/{gameId}/chat")
    fun gameChat(@PathVariable gameId: String, @RequestBody body: ChatRequest, request: HttpServletRequest): GameSnapshotDto {
        val user = requireUser(request)
        val game = requireGame(gameId)
        val player = game.playerMap[user.userId]
                ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not in this game")
        val message = body.message?.trim()
        if (!message.isNullOrEmpty()) {
            game.addChat(player, message)
        }
        return mapper.snapshot(game, player)
    }

    @GetMapping("/games/{gameId}/results")
    fun results(@PathVariable gameId: String, request: HttpServletRequest): GameSnapshotDto = snapshot(gameId, request)

    private fun finalizeConfiguredGame(game: Game, user: User) {
        game.status = GameStatus.WaitingForPlayers
        val hasBlackMarket = game.topKingdomCards.any { it.name == "Black Market" }
        val includePrizes = game.topKingdomCards.any { it.name == "Tournament" }
        if (hasBlackMarket) {
            val blackMarketCards = (cardManager.allCards - game.topKingdomCards).toMutableList()
            blackMarketCards.shuffle()
            game.blackMarketCards = blackMarketCards
        }
        if (game.isAlwaysIncludeColonyAndPlatinum || game.topKingdomCards[0].deck == Deck.Prosperity && !game.isNeverIncludeColonyAndPlatinum) {
            game.isIncludeColonyCards = true
            game.isIncludePlatinumCards = true
        }
        game.isIncludeShelters = !game.isExcludeShelters && (game.isIncludeShelters || game.topKingdomCards[1].deck == Deck.DarkAges)
        if (includePrizes || hasBlackMarket) {
            game.prizeCards = cardManager.prizeCards
        }
        game.setupGame()
        addPlayerToGame(game, user)
        LoggedInUsers.refreshLobbyGameRooms()
    }

    private fun randomizingOptions(body: CreateGameRequest): RandomizingOptions {
        val options = RandomizingOptions()
        options.numEventsAndLandmarksAndProjectsAndWays = body.numEventsAndLandmarksAndProjectsAndWays ?: 2
        options.customCardSelection = body.customCardNames.orEmpty().map { cardManager.getCard(it) }
        options.excludedCards = body.excludedCardNames.orEmpty().map { cardManager.getCard(it) }
        options.customEventSelection = body.customEventNames.orEmpty().map { cardManager.getEvent(it) }
        options.customLandmarkSelection = body.customLandmarkNames.orEmpty().map { cardManager.getLandmark(it) }
        options.customProjectSelection = body.customProjectNames.orEmpty().map { cardManager.getProject(it) }
        options.customWaySelection = body.customWayNames.orEmpty().map { cardManager.getWay(it) }
        return options
    }

    private fun addPlayerToGame(game: Game, user: User) {
        user.gameId = game.gameId
        user.status = ""
        game.addPlayer(user)
        LoggedInUsers.updateUserStatus(user)
        LoggedInUsers.refreshLobbyPlayers()
        LoggedInUsers.refreshLobbyGameRooms()
    }

    private fun removePlayerFromGame(game: Game, user: User) {
        user.gameId = null
        game.removePlayer(user)
        LoggedInUsers.updateUser(user)
        LoggedInUsers.refreshLobbyPlayers()
        LoggedInUsers.refreshLobbyGameRooms()
    }

    private fun parseLocation(location: String?): CardLocation {
        if (location == null) {
            return CardLocation.Unknown
        }
        val fromWebName = gameController.getCardLocationFromSource(location)
        if (fromWebName != CardLocation.Unknown) {
            return fromWebName
        }
        return CardLocation.values().firstOrNull { it.name.equals(location, true) } ?: CardLocation.Unknown
    }

    private fun requireUser(request: HttpServletRequest): User {
        val user = KingdomUtil.getUser(request) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required")
        if (user.isExpired) {
            KingdomUtil.logoutUser(user, request)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired")
        }
        user.lastActivity = Date()
        LoggedInUsers.updateUser(user)
        return user
    }

    private fun getGameForUser(user: User): Game? = user.gameId?.let { gameRoomManager.getGame(it) }

    private fun requireGame(gameId: String): Game =
            gameRoomManager.getGame(gameId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found")

    private fun isAccessAllowed(request: HttpServletRequest): Boolean =
            request.cookies?.firstOrNull { it.name.trim().toLowerCase() == "kingdomaccess" }?.value?.trim()?.toLowerCase() == ACCESS_PASSWORD

    private fun getUsernameCookie(request: HttpServletRequest): String? =
            request.cookies?.firstOrNull { it.name.trim().toLowerCase() == USERNAME_COOKIE }?.value?.trim()

    companion object {
        private const val ACCESS_PASSWORD = "winner"
    }
}
