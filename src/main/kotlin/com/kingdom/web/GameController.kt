package com.kingdom.web

import com.kingdom.model.*
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.BotPlayer
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import com.kingdom.service.*
import com.kingdom.util.KingdomUtil
import com.kingdom.util.groupedString
import freemarker.ext.beans.BeansWrapper
import freemarker.template.TemplateModelException
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

val emptyModelAndView = ModelAndView("empty")

@Suppress("unused")
@Controller
class GameController(private val cardManager: CardManager,
                     private val gameManager: GameManager,
                     private val gameRoomManager: GameRoomManager,
                     private val lobbyChats: LobbyChats,
                     private val gameMessageService: GameMessageService) {

    val gameControllerLock: Any = Object()

    @ResponseBody
    @RequestMapping(value = ["/getUserId"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getUserId(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {

        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            val model = HashMap<String, Any>()
            model["redirectToLogin"] = true
            return model
        }

        val model = HashMap<String, Any>()
        model["userId"] = user.userId
        return model
    }

    @RequestMapping("/createGame.html")
    fun createGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)

        var game = getGame(request)
        if (game == null) {
            game = gameRoomManager.nextAvailableGame
        }
        if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val modelAndView = ModelAndView("selectCards")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("createGame", true)
        modelAndView.addObject("title", "Create Game")
        modelAndView.addObject("action", "saveGame.html")

        try {
            request.session.setAttribute("gameId", game.gameId)
            if (user.guest) {
                return ModelAndView("redirect:/showGameRooms.html")
            }
            game.creatorId = user.userId
            game.creatorName = user.username
            LoggedInUsers.refreshLobbyGameRooms()
            addSelectCardsObjects(user, modelAndView, true)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/selectCards.html")
    fun selectCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = User()

        val modelAndView = ModelAndView("selectCards")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("createGame", false)
        modelAndView.addObject("title", "Select Cards")
        modelAndView.addObject("action", "generateCards.html")

        try {
            addSelectCardsObjects(user, modelAndView, false)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return ModelAndView("empty")
        }

    }

    private fun addSelectCardsObjects(user: User, modelAndView: ModelAndView, includeTesting: Boolean) {
        modelAndView.addObject("user", user)
        modelAndView.addObject("decks", getDecks(includeTesting))
        modelAndView.addObject("events", cardManager.allEvents.sortedBy { it.name })
        modelAndView.addObject("landmarks", cardManager.allLandmarks.sortedBy { it.name })
        modelAndView.addObject("projects", cardManager.allProjects.sortedBy { it.name })
        modelAndView.addObject("ways", cardManager.allWays.sortedBy { it.name })
        modelAndView.addObject("excludedCards", user.excludedCardNames)
    }

    private fun getDecks(includeTesting: Boolean): List<UserDeckInfo> {
        return Deck.values().filterNot { it == Deck.None }.map { deck ->
            UserDeckInfo(deck, cardManager.getCards(deck, includeTesting))
        }
    }

    @RequestMapping("/generateCards.html")
    @Throws(TemplateModelException::class)
    fun generateCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = User()
        val game = Game(gameManager, gameMessageService)

        processCards(request, game, user)

        return showRandomConfirmPage(request, user, game)
    }

    private fun processCards(request: HttpServletRequest, game: Game, user: User) {
        val generateType = request.getParameter("generateType")

        val eventSelection = request.getParameter("eventSelection")
        val landmarkSelection = request.getParameter("landmarkSelection")
        val projectSelection = request.getParameter("projectSelection")
        val waySelection = request.getParameter("waySelection")

        val decks = ArrayList<Deck>()
        val customCardSelection = ArrayList<Card>()
        val excludedCards = ArrayList<Card>(0)
        val customEventSelection = ArrayList<Event>()
        val customLandmarkSelection = ArrayList<Landmark>()
        val customProjectSelection = ArrayList<Project>()
        val customWaySelection = ArrayList<Way>()

        parseCardAndEventSelectionRequest(request, decks, customCardSelection, excludedCards, customEventSelection, customLandmarkSelection, customProjectSelection, customWaySelection)

        setRandomizingOptions(request, game, customCardSelection, excludedCards, generateType, customEventSelection, customLandmarkSelection, customProjectSelection, customWaySelection, eventSelection, landmarkSelection, projectSelection, waySelection)

        game.decks = decks

        cardManager.setRandomKingdomCardsAndEvents(game)

        user.excludedCards = KingdomUtil.getCommaSeparatedCardNames(excludedCards)
    }

    @RequestMapping("/saveGame.html")
    fun saveGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            if (game.status == GameStatus.BeingConfigured) {

                var numPlayers = 1
                var numComputerPlayers = 0
                var numEasyComputerPlayers = 0
                var numMediumComputerPlayers = 0
                var numHardComputerPlayers = 0
                var numBMUComputerPlayers = 0

                for (i in 2..6) {
                    when {
                        request.getParameter("player$i") == "human" -> numPlayers++
                        request.getParameter("player$i") == "computer_easy" -> {
                            numPlayers++
                            numComputerPlayers++
                            numEasyComputerPlayers++
                        }
                        request.getParameter("player$i") == "computer_medium" -> {
                            numPlayers++
                            numComputerPlayers++
                            numMediumComputerPlayers++
                        }
                        request.getParameter("player$i") == "computer_hard" -> {
                            numPlayers++
                            numComputerPlayers++
                            numHardComputerPlayers++
                        }
                        request.getParameter("player$i") == "computer_bmu" -> {
                            numPlayers++
                            numComputerPlayers++
                            numBMUComputerPlayers++
                        }
                    }
                }

                game.numPlayers = numPlayers
                game.numComputerPlayers = numComputerPlayers
                game.numEasyComputerPlayers = numEasyComputerPlayers
                game.numMediumComputerPlayers = numMediumComputerPlayers
                game.numHardComputerPlayers = numHardComputerPlayers
                game.numBMUComputerPlayers = numBMUComputerPlayers

                game.isShowVictoryPoints = KingdomUtil.getRequestBoolean(request, "showVictoryPoints")
                game.isIdenticalStartingHands = KingdomUtil.getRequestBoolean(request, "identicalStartingHands")

                game.title = request.getParameter("title")
                game.isPrivateGame = KingdomUtil.getRequestBoolean(request, "privateGame")
                if (game.isPrivateGame) {
                    game.password = request.getParameter("gamePassword")
                }
                game.mobile = KingdomUtil.isMobile(request)

                processCards(request, game, user)

                return ModelAndView("redirect:/confirmCards.html")
            }
            return ModelAndView("redirect:/showGameRooms.html")
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun parseCardAndEventSelectionRequest(request: HttpServletRequest, decks: MutableList<Deck>, customCardSelection: MutableList<Card>, excludedCards: MutableList<Card>, customEventSelection: MutableList<Event>, customLandmarkSelection: MutableList<Landmark>, customProjectSelection: MutableList<Project>, customWaySelection: MutableList<Way>) {
        val parameterNames = request.parameterNames
        while (parameterNames.hasMoreElements()) {
            val name = parameterNames.nextElement() as String
            when {
                name.startsWith("deck_") && !name.startsWith("deck_weight_") -> {
                    val deck = Deck.valueOf(request.getParameter(name))
                    val weight: Int = request.getParameter("deck_weight_" + deck.name).toInt()

                    repeat(weight * weight) {
                        decks.add(deck)
                    }
                }
                name.startsWith("card_") -> {
                    val cardName = name.substring(5)
                    val card = cardManager.getCard(cardName)
                    customCardSelection.add(card)
                }
                name.startsWith("event_") -> {
                    val eventName = name.substring(6)
                    val event = cardManager.getEvent(eventName)
                    customEventSelection.add(event)
                }
                name.startsWith("landmark_") -> {
                    val landmarkName = name.substring(9)
                    val landmark = cardManager.getLandmark(landmarkName)
                    customLandmarkSelection.add(landmark)
                }
                name.startsWith("project_") -> {
                    val projectName = name.substring(8)
                    val project = cardManager.getProject(projectName)
                    customProjectSelection.add(project)
                }
                name.startsWith("way_") -> {
                    val wayName = name.substring(4)
                    val way = cardManager.getWay(wayName)
                    customWaySelection.add(way)
                }
                name.startsWith("excluded_card_") -> {
                    val cardName = name.substring(14)
                    val card = cardManager.getCard(cardName)
                    excludedCards.add(card)
                }
            }
        }
    }

    private fun setRandomizingOptions(request: HttpServletRequest, game: Game, customCardSelection: List<Card>, excludedCards: List<Card>, generateType: String, customEventSelection: List<Event>, customLandmarkSelection: List<Landmark>, customProjectSelection: List<Project>, customWaySelection: List<Way>, eventSelection: String, landmarkSelection: String, projectSelection: String, waySelection: String) {
        val options = RandomizingOptions()

        options.excludedCards = excludedCards

        if (generateType == "custom") {
            game.custom = true
            options.customCardSelection = customCardSelection
            if (KingdomUtil.getRequestBoolean(request, "includeColonyAndPlatinumCards")) {
                game.isAlwaysIncludeColonyAndPlatinum = true
            }
        } else {
            options.isOneOfEachCost = KingdomUtil.getRequestBoolean(request, "oneOfEachCost")
            options.isOneWithBuy = KingdomUtil.getRequestBoolean(request, "oneWithBuy")
            options.isOneWithActions = KingdomUtil.getRequestBoolean(request, "oneWithActions")
            options.isDefenseForAttack = KingdomUtil.getRequestBoolean(request, "defenseForAttack")
            game.isAlwaysIncludeColonyAndPlatinum = KingdomUtil.getRequestBoolean(request, "alwaysIncludeColonyAndPlatinum")
        }

        options.numEventsAndLandmarksAndProjectsAndWays = KingdomUtil.getRequestInt(request, "numEventsAndLandmarksAndProjectsAndWays", 2)

        if (eventSelection == "custom") {
            options.customEventSelection = customEventSelection
        }

        if (landmarkSelection == "custom") {
            options.customLandmarkSelection = customLandmarkSelection
        }
        
        if (projectSelection == "custom") {
            options.customProjectSelection = customProjectSelection
        }

        if (waySelection == "custom") {
            options.customWaySelection = customWaySelection
        }

        game.randomizingOptions = options
    }

    @RequestMapping("/confirmCards.html")
    fun confirmCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                showRandomConfirmPage(request, user, game)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @Throws(TemplateModelException::class)
    private fun showRandomConfirmPage(request: HttpServletRequest, user: User, game: Game): ModelAndView {

        val includeColonyAndPlatinum = game.isAlwaysIncludeColonyAndPlatinum || game.topKingdomCards[0].deck == Deck.Prosperity && !game.isNeverIncludeColonyAndPlatinum
        val includeShelters = !game.isExcludeShelters && (game.isIncludeShelters || game.topKingdomCards[1].deck == Deck.DarkAges)

        val modelAndView = ModelAndView("randomConfirm")
        modelAndView.addObject("createGame", KingdomUtil.getRequestBoolean(request, "createGame"))
        modelAndView.addObject("player", HumanPlayer(user, game))
        modelAndView.addObject("currentPlayerId", -1)
        modelAndView.addObject("cards", game.topKingdomCards)
        modelAndView.addObject("eventsAndLandmarksAndProjectsAndWays", game.events + game.landmarks + game.projects + game.ways)
        modelAndView.addObject("artifacts", game.topKingdomCards.filterIsInstance<ArtifactAction>().flatMap { it.artifacts })
        modelAndView.addObject("includeColonyAndPlatinum", includeColonyAndPlatinum)
        modelAndView.addObject("includeShelters", includeShelters)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("randomizerReplacementCardNotFound", game.isRandomizerReplacementCardNotFound)
        return modelAndView
    }

    @RequestMapping("/changeRandomCards.html")
    fun changeRandomCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.setRandomKingdomCardsAndEvents(game)
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/swapRandomCard.html")
    fun swapRandomCard(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.swapRandomCard(game, request.getParameter("cardName"))
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/swapEvent.html")
    fun swapEvent(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.swapEvent(game, request.getParameter("eventName"))
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/swapLandmark.html")
    fun swapLandmark(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.swapLandmark(game, request.getParameter("landmarkName"))
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/swapProject.html")
    fun swapProject(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.swapProject(game, request.getParameter("projectName"))
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/togglePlatinumAndColony.html")
    fun togglePlatinumAndColony(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                val include = KingdomUtil.getRequestBoolean(request, "include")
                game.isAlwaysIncludeColonyAndPlatinum = include
                game.isNeverIncludeColonyAndPlatinum = !include
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/toggleShelters.html")
    fun toggleShelters(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                val include = KingdomUtil.getRequestBoolean(request, "includeShelters")
                game.isIncludeShelters = include
                game.isExcludeShelters = !include
                confirmCards(request, response)
            } else {
                if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }
    }

    @RequestMapping("/keepRandomCards.html")
    fun keepRandomCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            if (game.status == GameStatus.BeingConfigured) {
                game.status = GameStatus.WaitingForPlayers
                LoggedInUsers.refreshLobbyGameRooms()
                var hasBlackMarket = false
                var includePrizes = false

                for (card in game.topKingdomCards) {
                    if (card.name == "Black Market") {
                        hasBlackMarket = true
                    } else if (card.name == "Tournament") {
                        includePrizes = true
                    }
                }

                if (hasBlackMarket) {
                    setBlackMarketCards(game)
                }
                if (game.isAlwaysIncludeColonyAndPlatinum || game.topKingdomCards[0].deck == Deck.Prosperity && !game.isNeverIncludeColonyAndPlatinum) {
                    game.isIncludeColonyCards = true
                    game.isIncludePlatinumCards = true
                }

                val includeShelters = !game.isExcludeShelters && (game.isIncludeShelters || game.topKingdomCards[1].deck == Deck.DarkAges)
                game.isIncludeShelters = includeShelters

                if (includePrizes || hasBlackMarket) {
                    game.prizeCards = cardManager.prizeCards
                }
                game.setupGame()
                addPlayerToGame(game, user)
            }
            return if (game.status == GameStatus.InProgress) {
                ModelAndView("redirect:/showGame.html")
            } else {
                ModelAndView("redirect:/showGameRooms.html")
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun setBlackMarketCards(game: Game) {
        val allCards = cardManager.allCards
        val blackMarketCards = (allCards - game.topKingdomCards).toMutableList()
        blackMarketCards.shuffle()
        game.blackMarketCards = blackMarketCards
    }

    @RequestMapping("/cancelGame.html")
    fun cancelGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val gameId = request.getParameter("gameId")
        if (user == null || gameId == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val game = gameRoomManager.getGame(gameId)
        if (game != null && (user.admin || game.creatorId == user.userId)) {
            if (game.status == GameStatus.InProgress) {
                game.addInfoLog("${user.username} cancelled the game")
                game.gameOver()
            } else {
                request.session.removeAttribute("gameId")
                game.reset()
            }
        }

        if (user.gameId == gameId) {
            return ModelAndView("redirect:/showGameResults.html")
        }

        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/endCurrentPlayersTurn.html")
    fun endCurrentPlayersTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val gameId = request.getParameter("gameId")
        if (user == null || gameId == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val game = gameRoomManager.getGame(gameId)
        if (game != null && (user.admin || game.creatorId == user.userId)) {
            game.currentPlayer.endTurn()
            game.refreshGame()
        }
        return ModelAndView("redirect:/showGame.html")
    }

    @RequestMapping("/clearAllPlayerActions.html")
    fun clearAllPlayerActions(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val gameId = request.getParameter("gameId")
        if (user == null || gameId == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val game = gameRoomManager.getGame(gameId)
        if (game != null && (user.admin || game.creatorId == user.userId)) {
            game.players.forEach {
                it.currentAction = null
                if (it.isBot) {
                    (it as BotPlayer).isWaitingForPlayers = false
                }
            }
            game.refreshGame()
        }
        return ModelAndView("redirect:/showGame.html")
    }

    @RequestMapping("/cancelCreateGame.html")
    fun cancelCreateGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        game.reset()
        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/showGameRooms.html")
    fun showGameRooms(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (user.isExpired) {
            KingdomUtil.logoutUser(user, request)
            return KingdomUtil.getLoginModelAndView(request)
        }
        val game = getGame(request)
        try {
            if (game != null && game.status != GameStatus.WaitingForPlayers && game.status != GameStatus.Finished && game.playerMap.containsKey(user.userId)) {
                return ModelAndView("redirect:/showGame.html")
            }
            user.refreshLobby.isRefreshPlayers = false
            user.refreshLobby.isRefreshGameRooms = false
            user.refreshLobby.isRefreshChat = false
            LoggedInUsers.refreshLobby(user)
            val modelAndView = ModelAndView("gameRooms")
            modelAndView.addObject("user", user)
            modelAndView.addObject("players", LoggedInUsers.getUsers())
            modelAndView.addObject("gameRooms", gameRoomManager.lobbyGameRooms)
            modelAndView.addObject("chats", lobbyChats.chats)
            modelAndView.addObject("maxGameRoomLimitReached", gameRoomManager.maxGameRoomLimitReached())
            modelAndView.addObject("numGamesInProgress", gameRoomManager.gamesInProgress.size)

            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            if (game != null) {
                val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
                game.logError(error)
            }
            return ModelAndView("empty")
        }

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

    @RequestMapping("/leaveGame.html")
    fun leaveGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        if (user.gameId == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        val game = gameRoomManager.getGame(user.gameId!!)
        if (game == null || game.status != GameStatus.WaitingForPlayers) {
            return ModelAndView("redirect:/showGameRooms.html")
        } else {
            removePlayerFromGame(game, user)
        }
        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/joinGame.html")
    fun joinGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        if (user.gameId != null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        val gameId = request.getParameter("gameId")
        val game: Game?
        if (gameId != null) {
            game = gameRoomManager.getGame(gameId)
        } else {
            game = getGame(request)
        }
        if (game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            if (game.players.size == game.numPlayers || game.isPrivateGame) {
                return showGameRooms(request, response)
            } else {
                if (!game.playerMap.containsKey(user.userId)) {
                    if (gameId != null) {
                        request.session.setAttribute("gameId", gameId)
                    }
                    addPlayerToGame(game, user)
                }
                return if (game.status == GameStatus.InProgress) {
                    ModelAndView("redirect:/showGame.html")
                } else {
                    ModelAndView("redirect:/showGameRooms.html")
                }
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @ResponseBody
    @RequestMapping(value = ["/joinPrivateGame"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun joinPrivateGame(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        if (user == null) {
            model["redirectToLogin"] = true
            return model
        }
        val gameId = request.getParameter("gameId")
        val game: Game?
        if (gameId != null) {
            game = gameRoomManager.getGame(gameId)
        } else {
            game = getGame(request)
        }
        if (game == null) {
            model["redirectToLogin"] = true
            return model
        }
        try {
            var message = "Success"
            val gamePassword = request.getParameter("gamePassword")
            if (gamePassword == null || gamePassword != game.password) {
                message = "Invalid Password"
            } else if (game.players.size == game.numPlayers) {
                message = "Game Room Full"
            } else if (!game.playerMap.containsKey(user.userId)) {
                if (gameId != null) {
                    request.session.setAttribute("gameId", Integer.parseInt(gameId))
                }
                addPlayerToGame(game, user)
            }

            model["message"] = message
            model["start"] = game.status == GameStatus.InProgress

            return model
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return model
        }

    }

    @RequestMapping("/showGame.html")
    fun showGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        try {
            val modelAndView = ModelAndView("game")
            val player = game.playerMap[user.userId] ?: return showGameRooms(request, response)

            KingdomUtil.addUsernameCookieToResponse(player.username, response)

            addGameObjects(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @ResponseBody
    @RequestMapping(value = ["/getGameInfo"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getGameInfo(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        val game = getGame(request)
        val model = HashMap<String, Any>()
        if (user == null || game == null) {
            model["redirectToLogin"] = true
            return model
        }

        val player = game.playerMap[user.userId]

        if (player == null) {
            model["redirectToLogin"] = true
            return model
        }

        model["refreshGameData"] = RefreshGameData(player.game.status, player.isYourTurn)
        if (player.isYourTurn) {
            model["showYourTurnMessage"] = player.isShowYourTurnMessage
            player.isShowYourTurnMessage = false
        }

        return model
    }

    class RefreshGameData(val gameStatus: GameStatus,
                          val isCurrentPlayer: Boolean) {
        var title: String? = null
    }

    @ResponseBody
    @RequestMapping(value = ["/clickCard"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun clickCard(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        try {
            val clickType = request.getParameter("clickType")
            val cardId = request.getParameter("cardId")
            val cardName = request.getParameter("cardName")
            if (cardId != null && cardName != null) {
                val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGameRooms.html")
                cardClicked(game, player, getCardLocationFromSource(clickType), cardName, cardId)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    fun getCardLocationFromSource(source: String): CardLocation {
        return when (source) {
            "supply" -> CardLocation.Supply
            "hand" -> CardLocation.Hand
            "discard" -> CardLocation.Discard
            "playArea" -> CardLocation.PlayArea
            "cardAction" -> CardLocation.CardAction
            "event" -> CardLocation.Event
            "landmark" -> CardLocation.Landmark
            "project" -> CardLocation.Project
            "way" -> CardLocation.Way
            else -> CardLocation.Unknown
        }
    }

    fun cardClicked(game: Game, player: Player, source: CardLocation, cardName: String, cardId: String) {
        if (!player.isYourTurn && player.currentAction == null) {
            return
        }

        val action = player.currentAction

        when (source) {
            CardLocation.Supply -> {
                val card = game.getNewInstanceOfCard(cardName)

                if (highlightSupplyCard(player, card)) {
                    if (action != null) {
                        handleCardClickedForAction(player, card, source)
                    } else {
                        if (player.hand.any { it.isTreasure } && !player.isTreasureCardsPlayedInBuyPhase && card.debtCost > 0) {
                            player.yesNoChoice(object : ChoiceActionCard {
                                override val name: String = "PlayTreasuresBeforeBuyingDebtCard"

                                override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                                    if (choice == 1) {
                                        player.buyCard(card)
                                        game.refreshCardsBought()
                                    }
                                }

                            }, "Are you sure you want to buy ${card.cardNameWithBackgroundColor} before playing your treasure cards?")

                            return
                        }

                        player.buyCard(card)

                        if (player.buys == 0) {
                            if (player.currentAction != null || card.isPreventAutoEndTurnWhenBought) {
                                game.refreshCardsBought()
                            } else {
                                player.endTurn(true)
                            }
                        }
                    }
                }
            }
            CardLocation.Landmark -> {
                val landmark = game.getNewInstanceOfLandmark(cardName)

                if (highlightLandmarkCard(player, landmark)) {

                    player.useLandmark(landmark)

                    if (player.buys == 0 && player.currentAction == null) {
                        player.endTurn(true)
                    }
                }
            }
            CardLocation.Event -> {
                val eventCard = game.getNewInstanceOfEvent(cardName)

                if (highlightEventCard(player, eventCard)) {

                    if (player.hand.any { it.isTreasure } && (player.buys > 1 || eventCard.debtCost > 0)) {
                        player.yesNoChoice(object : ChoiceActionCard {
                            override val name: String = "PlayTreasuresBeforeBuyingDebtEvent"

                            override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                                if (choice == 1) {
                                    player.buyEvent(eventCard)
                                }
                            }

                        }, "Are you sure you want to buy ${eventCard.cardNameWithBackgroundColor} before playing your treasure cards?")

                        return
                    }

                    player.buyEvent(eventCard)

                    if (player.buys == 0 && player.currentAction == null) {
                        player.endTurn(true)
                    }
                }
            }
            CardLocation.Project -> {
                val projectCard = game.getNewInstanceOfProject(cardName)

                if (highlightProjectCard(player, projectCard)) {

                    if (player.hand.any { it.isTreasure } && player.buys > 1) {
                        player.yesNoChoice(object : ChoiceActionCard {
                            override val name: String = "PlayTreasuresBeforeBuyingDebtProject"

                            override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                                if (choice == 1) {
                                    player.buyProject(projectCard)
                                }
                            }

                        }, "Are you sure you want to buy ${projectCard.cardNameWithBackgroundColor} before playing your treasure cards?")

                        return
                    }

                    player.buyProject(projectCard)

                    if (player.buys == 0 && player.currentAction == null) {
                        player.endTurn(true)
                    }
                }
            }
            CardLocation.Hand -> {
                val card = findCardById(player.hand, cardId)!!
                if (highlightCard(player, card, source)) {

                    if (action == null && !player.isBuyPhase && card.isTreasure && !card.isAction && player.hand.any { it.isAction } && player.actions > 0) {

                        player.yesNoChoice(object : ChoiceActionCard {
                            override val name: String = "PlayActionsBeforePlayingTreasures"

                            override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                                if (choice == 1) {
                                    player.playCard(card)
                                    player.refreshPlayerHandArea()
                                }
                            }

                        }, "Are you sure you want to play ${card.cardNameWithBackgroundColor} before playing your action cards?")

                        return
                    }

                    if (action != null) {
                        handleCardClickedForAction(player, card, source)
                    } else {
                        player.playCard(card)
                        player.refreshPlayerHandArea()
                    }
                }
            }
            CardLocation.Discard -> {
                val card = findCardById(player.cardsInDiscard, cardId)!!
                if (highlightCard(player, card, source)) {
                    if (action != null) {
                        handleCardClickedForAction(player, card, source)
                    }
                }
            }
            CardLocation.CardAction -> {
                val card = findCardById(action?.cardChoices ?: emptyList(), cardId)
                if (card != null) {
                    handleCardClickedForAction(player, card, source)
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    fun handleCardClickedForAction(player: Player, card: Card, cardLocation: CardLocation) {
        val action = player.currentAction
        val result = ActionResult()
        result.cardLocation = cardLocation

        result.selectedCard = card

        player.actionResult(action!!, result)

        player.game.refreshPlayerCardAction(player)
        player.game.refreshSupply()
        player.refreshPlayerHandArea()
    }

    fun highlightCard(player: Player, card: Card?, cardLocation: CardLocation): Boolean {
        val action = player.currentAction

        if (card == null) {
            println("Error highlighting card for location")
            return false
        }

        if (!player.isYourTurn && action == null) {
            return false
        }

        return action?.isCardActionable(card, cardLocation, player) ?: card.isActionable(player, cardLocation)
    }

    fun findCardById(cards: List<Card>, cardId: String): Card? {
        return cards.firstOrNull { it.id == cardId }
    }

    fun highlightSupplyCard(player: Player, card: Card?): Boolean {
        val action = player.currentAction

        return when {
            !player.isYourTurn || card == null -> false
            action != null -> action.isCardActionable(card, CardLocation.Supply, player)
            else -> player.isCardBuyable(card)
        }
    }

    fun highlightEventCard(player: Player, card: Card?): Boolean {
        return player.currentAction == null && card?.isActionable(player, CardLocation.Event) ?: false
    }

    fun highlightLandmarkCard(player: Player, card: Card?): Boolean {
        return player.currentAction == null && card?.isActionable(player, CardLocation.Landmark) ?: false
    }

    fun highlightProjectCard(player: Player, card: Card?): Boolean {
        return player.currentAction == null && card?.isActionable(player, CardLocation.Project) ?: false
    }

    @ResponseBody
    @RequestMapping(value = ["/playAllTreasureCards"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun playAllTreasureCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGameRooms.html")

        if (player.currentAction != null) {
            return emptyModelAndView
        }

        if (!player.isBuyPhase && player.hand.any { it.isAction } && player.actions > 0) {

            player.yesNoChoice(object : ChoiceActionCard {
                override val name: String = "PlayActionsBeforePlayingTreasures"

                override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                    if (choice == 1) {
                        player.playAllTreasureCards()
                    }
                }

            }, "Are you sure you want to play treasure cards before playing your action cards?")

            return emptyModelAndView
        }

        try {
            player.playAllTreasureCards()
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/endTurn"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun endTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val player = game.playerMap[user.userId]!!

        if (player.currentAction != null) {
            game.refreshGame()
        } else {
            player.endTurn()
            return ModelAndView("redirect:/showGame.html")
        }

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/submitCardActionChoice"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun submitCardActionChoice(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val player = game.playerMap[user.userId]!!
        if (player.currentAction != null) {
            val result = ActionResult()
            result.choiceSelected = request.getParameter("choice").toInt()
            player.actionResult(player.currentAction!!, result)
        }

        game.refreshPlayerCardAction(player)
        game.refreshSupply()
        player.refreshPlayerHandArea()

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/submitDoNotUseAction"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun submitDoNotUseAction(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val player = game.playerMap[user.userId]!!
        if (player.currentAction != null) {
            player.actionResult(player.currentAction!!, ActionResult().apply { isDoNotUse = true })
        }

        game.refreshPlayerCardAction(player)
        player.game.refreshSupply()
        player.refreshPlayerHandArea()

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/submitDoneWithAction"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun submitDoneWithAction(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        val player = game.playerMap[user.userId]!!
        if (player.currentAction != null) {
            player.actionResult(player.currentAction!!, ActionResult().apply { isDoneWithAction = true })
        }

        game.refreshPlayerCardAction(player)
        game.refreshSupply()
        player.refreshPlayerHandArea()

        return emptyModelAndView
    }


    @RequestMapping("/getGameDiv.html")
    fun getGameDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("gameDiv")
            addGameObjects(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getPlayersDiv.html")
    fun getPlayersDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "playersDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "playersDivMobile"
            }
            val modelAndView = ModelAndView(template)
            modelAndView.addObject("players", game.players)
            modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getSupplyDiv.html")
    fun getSupplyDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else getSupplyDiv(request, user, game)
    }

    @RequestMapping("/getSupplyDivOnEndTurn.html")
    fun getSupplyDivOnEndTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else {
            val modelAndView = getSupplyDiv(request, user, game)
            modelAndView.addObject("currentPlayerId", 0)
            modelAndView
        }
    }

    private fun getSupplyDiv(request: HttpServletRequest, user: User, game: Game): ModelAndView {
        try {
            var supplyDivTemplate = "supplyDiv"
            if (KingdomUtil.isMobile(request)) {
                supplyDivTemplate = "supplyDivMobile"
            }

            val modelAndView = ModelAndView(supplyDivTemplate)

            addGameObjects(game, user, modelAndView, request)

            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun addSupplyDataToModelAndView(game: Game, player: Player, modelAndView: ModelAndView) {
        val kingdomCards = game.topKingdomCards.map { it.isHighlighted = highlightCard(player, it, CardLocation.Supply); it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it); it }
        modelAndView.addObject("kingdomCards", kingdomCards)

        val supplyCards = game.cardsInSupply.map { it.isHighlighted = highlightCard(player, it, CardLocation.Supply); it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it); it }
        modelAndView.addObject("supplyCards", supplyCards)

        game.events.forEach { it.isHighlighted = highlightCard(player, it, CardLocation.Event) }
        game.landmarks.forEach { it.isHighlighted = highlightCard(player, it, CardLocation.Landmark) }
        game.projects.forEach { it.isHighlighted = highlightCard(player, it, CardLocation.Project) }
        modelAndView.addObject("eventsAndLandmarksAndProjectsAndWays", game.events + game.landmarks + game.projects + game.ways)

        try {
            val bw = BeansWrapper()
            modelAndView.addObject("supply", bw.wrap(game.numInPileMap))
            modelAndView.addObject("victoryPointsOnSupplyPile", bw.wrap(game.victoryPointsOnSupplyPile))
            modelAndView.addObject("debtOnSupplyPile", bw.wrap(game.debtOnSupplyPile))
            modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens)
            if (game.isShowEmbargoTokens) {
                modelAndView.addObject("embargoTokens", bw.wrap(game.embargoTokens))
            }
            if (game.isTrackTradeRouteTokens) {
                modelAndView.addObject("tradeRouteTokenMap", bw.wrap(game.tradeRouteTokenMap))
            }
        } catch (e: TemplateModelException) {
            //
        }
    }

    @RequestMapping("/getPlayingAreaDiv.html")
    fun getPlayingAreaDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "playingAreaDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "playingAreaDivMobile"
            }
            val modelAndView = ModelAndView(template)

            val player = game.playerMap[user.userId]!!
            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            addPlayingAreaDataToModelView(game, player, modelAndView)

            modelAndView.addObject("playTreasureCards", player.isTreasuresPlayable)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun addPlayingAreaDataToModelView(game: Game, player: Player, modelAndView: ModelAndView) {
        addCardsPlayedDataToModelAndView(game, player, modelAndView)

        addCardsBoughtToModelAndView(game, modelAndView)
    }

    private fun addCardsBoughtToModelAndView(game: Game, modelAndView: ModelAndView) {
        val cardsBoughtCopy = game.currentPlayer.cardsBoughtCopy

        cardsBoughtCopy.forEach {
            it.isHighlighted = false
            it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it)
        }

        modelAndView.addObject("cardsBought", cardsBoughtCopy)
    }

    private fun addCardsPlayedDataToModelAndView(game: Game, player: Player, modelAndView: ModelAndView) {
        val cardsPlayed = game.currentPlayer.inPlay.map { it.isHighlighted = highlightCard(player, it, CardLocation.PlayArea); it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it); it }
        modelAndView.addObject("cardsPlayed", cardsPlayed)
    }

    @RequestMapping("/getCardsPlayedDiv.html")
    fun getCardsPlayedDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "cardsPlayedDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "cardsPlayedDivMobile"
            }

            val modelAndView = ModelAndView(template)

            val player = game.playerMap[user.userId]!!

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            addCardsPlayedDataToModelAndView(game, player, modelAndView)

            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getCardsBoughtDiv.html")
    fun getCardsBoughtDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "cardsBoughtDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "cardsBoughtDivMobile"
            }

            val modelAndView = ModelAndView(template)

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            addCardsBoughtToModelAndView(game, modelAndView)

            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getPreviousPlayerCardsBoughtDiv.html")
    fun getPreviousPlayerCardsBoughtDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "cardsBoughtDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "cardsBoughtDivMobile"
            }
            val modelAndView = ModelAndView(template)

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            modelAndView.addObject("cardsBought", game.previousPlayer?.lastTurnSummary?.cardsBought ?: emptyList<Card>())
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getHistoryDiv.html")
    fun getHistoryDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("historyDiv")
            modelAndView.addObject("recentHistory", game.recentHistory)
            modelAndView.addObject("turnHistory", game.recentTurnHistory)
            modelAndView.addObject("lastTurnSummaries", game.lastTurnSummaries)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getHandDiv.html")
    fun getHandDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("handDiv")

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun addPlayerAndGameDataToModelAndView(game: Game, user: User, modelAndView: ModelAndView, request: HttpServletRequest) {
        val player = game.playerMap[user.userId]!!

        synchronized(gameControllerLock) {
            player.hand.forEach {
                it.isHighlighted = highlightCard(player, it, CardLocation.Hand)
                it.isSelected = isCardSelected(player, it)
                it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it)
            }

            player.durationCards.forEach {
                it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it)
            }

            player.currentAction?.cardChoices?.forEach {
                it.isHighlighted = highlightCard(player, it, CardLocation.CardAction)
                it.isSelected = isCardSelected(player, it)
                it.adjustedCost = game.currentPlayer.getCardCostWithModifiers(it)
            }
        }

        modelAndView.addObject("user", user)
        modelAndView.addObject("player", player)
        modelAndView.addObject("currentPlayerId", game.currentPlayerId)
        modelAndView.addObject("currentPlayer", game.currentPlayer)

        modelAndView.addObject("gameStatus", game.status)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
    }

    private fun isCardSelected(player: Player, card: Card): Boolean {
        return player.currentAction?.isCardSelected(card) ?: false
    }

    @RequestMapping("/getHandAreaDiv.html")
    fun getHandAreaDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else getHandAreaDiv(request, user, game)
    }

    @RequestMapping("/getHandAreaDivOnEndTurn.html")
    fun getHandAreaDivOnEndTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else {
            val modelAndView = getHandAreaDiv(request, user, game)
            modelAndView.addObject("currentPlayerId", 0)
            modelAndView
        }
    }

    private fun getHandAreaDiv(request: HttpServletRequest, user: User, game: Game): ModelAndView {
        try {
            var template = "handAreaDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "handAreaDivMobile"
            }
            val modelAndView = ModelAndView(template)

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            val player = game.playerMap[user.userId]!!

            modelAndView.addObject("showDuration", game.isShowDuration)
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showExileCards", game.isShowExileCards)
            modelAndView.addObject("showTavern", game.isShowTavern)
            modelAndView.addObject("showJourneyToken", game.isShowJourneyToken)
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
            modelAndView.addObject("playTreasureCards", player.isTreasuresPlayable)
            modelAndView.addObject("artifacts", game.artifacts)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getDurationDiv.html")
    fun getDurationDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("durationDiv")
            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getDiscardDiv.html")
    fun getDiscardDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            var template = "discardDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "discardDivMobile"
            }
            val modelAndView = ModelAndView(template)
            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getChatDiv.html")
    fun getChatDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val chats = game.chats
            var template = "chatDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "chatDivMobile"
                //Collections.reverse(chats);
            }
            val modelAndView = ModelAndView(template)
            modelAndView.addObject("chats", chats)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getCardActionDiv.html")
    fun getCardActionDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("cardActionDiv")
            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getGameInfoDiv.html")
    fun getGameInfoDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("gameInfoDiv")
            addGameObjects(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/showGameResults.html")
    fun showGameResults(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val modelAndView = ModelAndView("gameResults")
        try {
            addGameObjects(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/exitGame.html")
    fun exitGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            request.session.removeAttribute("gameId")
            user.gameId = null
            LoggedInUsers.updateUser(user)
            LoggedInUsers.refreshLobbyPlayers()
            val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGame.html")
            game.playerExitedGame(player)
        } catch (t: Throwable) {
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return ModelAndView("empty")
    }

    @ResponseBody
    @RequestMapping(value = ["/quitGame"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun quitGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            if (game.status == GameStatus.WaitingForPlayers) {
                game.reset()
                return ModelAndView("redirect:/showGameRooms.html")
            }
            if (game.status != GameStatus.Finished) {
                val player = game.playerMap[user.userId]!!
                game.playerQuitGame(player)
            }

            game.refreshGame()

            return emptyModelAndView
        } catch (t: Throwable) {
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return emptyModelAndView
        }

    }

    private fun processChatCommand(user: User, commandString: String) {
        try {
            val command = commandString.substring(1, commandString.indexOf(" "))
            val remainingString = commandString.substring(command.length + 2)
            if (command.equals("lobby", ignoreCase = true)) {
                sendLobbyChat(user, remainingString)
            } else if (command.equals("whisper", ignoreCase = true) || command.equals("w", ignoreCase = true)) {
                val username = remainingString.substring(0, remainingString.indexOf(" "))
                val message = remainingString.substring(username.length + 1)
                val receivingUser = LoggedInUsers.getUserByUsername(username)
                if (receivingUser != null) {
                    sendPrivateChat(user, message, receivingUser.userId)
                }
            }
            //todo help command
        } catch (e: Exception) {
            //todo display invalid command message
        }

    }

    private fun sendLobbyChat(user: User, message: String?) {
        if (message != null && message != "") {
            lobbyChats.addChat(user, message)
        }
    }

    private fun sendPrivateChat(user: User, message: String?, receivingUserId: String?) {
        if (message != null && message != "" && receivingUserId != null) {
            val receivingUser = LoggedInUsers.getUser(receivingUserId)
            if (receivingUser != null) {
                if (receivingUser.gameId != null) {
                    val game = gameRoomManager.getGame(receivingUser.gameId!!)!!
                    game.addPrivateChat(user, receivingUser, message)
                } else {
                    lobbyChats.addPrivateChat(user, receivingUser, message)
                    LoggedInUsers.refreshLobbyChat()
                }
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = ["/sendChat"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun sendChat(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId]!!
            val message = request.getParameter("message")
            if (message != null && message != "") {
                if (message.startsWith("/")) {
                    processChatCommand(user, message)
                } else {
                    game.addChat(player, message)
                }
            }

            game.refreshChat()

            return emptyModelAndView
        } catch (t: Throwable) {
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return emptyModelAndView
        }

    }

    @ResponseBody
    @RequestMapping(value = ["/sendLobbyChat"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun sendLobbyChat(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model["redirectToLogin"] = true
            return model
        }
        LoggedInUsers.updateUser(user)
        val message = request.getParameter("message")
        if (message != null && message.startsWith("/")) {
            processChatCommand(user, message)
        } else {
            sendLobbyChat(user, message)
        }
        LoggedInUsers.refreshLobbyChat()
        return refreshLobby(request, response)
    }

    @ResponseBody
    @RequestMapping(value = ["/sendPrivateChat"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun sendPrivateChat(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model["redirectToLogin"] = true
            return model
        }
        val message = request.getParameter("message")
        val receivingUserId = request.getParameter("receivingUserId")
        sendPrivateChat(user, message, receivingUserId)
        return refreshLobby(request, response)
    }

    private fun logErrorAndReturnEmpty(t: Throwable, game: Game): ModelAndView {
        t.printStackTrace()
        val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
        game.logError(error)
        return ModelAndView("empty")
    }

    @RequestMapping("/showNativeVillageCards.html")
    fun showNativeVillageCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val player = game.playerMap[user.userId]!!

        return getShowCardsDiv(request, response, player.nativeVillageCards, "Native Village Mat")
    }

    @RequestMapping("/showIslandCards.html")
    fun showIslandCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val player = game.playerMap[user.userId]!!

        return getShowCardsDiv(request, response, player.islandCards, "Island Cards")
    }

    @RequestMapping("/showExileCards.html")
    fun showExileCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val player = game.playerMap[user.userId]!!

        return getShowCardsDiv(request, response, player.exileCards, "Exile Cards")
    }

    @RequestMapping("/showCardsNotInSupply.html")
    fun showCardsNotInSupply(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        return getShowCardsDiv(request, response, game.cardsNotInSupply, "Cards not in supply")
    }

    private fun getShowCardsDiv(request: HttpServletRequest, response: HttpServletResponse, cardsToShow: List<Card>, cardsToShowTitle: String): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("showCardsDiv")

            modelAndView.addObject("cardsToShowTitle", cardsToShowTitle)
            modelAndView.addObject("cardsToShow", cardsToShow)

            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun addGameObjects(game: Game, user: User, modelAndView: ModelAndView, request: HttpServletRequest) {
        val player = game.playerMap[user.userId]!!

        addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

        addSupplyDataToModelAndView(game, player, modelAndView)

        modelAndView.addObject("players", game.players)

        addPlayingAreaDataToModelView(game, player, modelAndView)

        modelAndView.addObject("recentHistory", game.recentHistory)
        modelAndView.addObject("turnHistory", game.recentTurnHistory)
        modelAndView.addObject("lastTurnSummaries", game.lastTurnSummaries)
        modelAndView.addObject("chats", game.chats)
        modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents)
        modelAndView.addObject("showDuration", game.isShowDuration)
        modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens)
        modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
        modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
        modelAndView.addObject("showIslandCards", game.isShowIslandCards)
        modelAndView.addObject("showExileCards", game.isShowExileCards)
        modelAndView.addObject("showTavern", game.isShowTavern)
        modelAndView.addObject("showJourneyToken", game.isShowJourneyToken)
        modelAndView.addObject("playTreasureCards", player.isTreasuresPlayable)
        modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)
        modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens)
        modelAndView.addObject("tradeRouteTokensOnMat", game.tradeRouteTokensOnMat)
        modelAndView.addObject("trashedCards", game.trashedCards.groupedString)
        modelAndView.addObject("prizeCards", game.prizeCardsString)
        modelAndView.addObject("artifacts", game.artifacts)

        modelAndView.addObject("showPrizeCards", game.isShowPrizeCards)

        modelAndView.addObject("gameEndReason", game.gameEndReason)
        modelAndView.addObject("winnerString", game.winnerString)
        modelAndView.addObject("victoryCards", game.victoryCards)
        modelAndView.addObject("scoringLandmarks", game.landmarks.filterIsInstance<VictoryPointsCalculator>())

        modelAndView.addObject("showCardsNotInSupply", game.cardsNotInSupply.isNotEmpty())
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }

    private fun getGame(request: HttpServletRequest): Game? {
        val gameId = request.session.getAttribute("gameId") ?: return null
        return gameRoomManager.getGame(gameId as String)
    }

    @ResponseBody
    @RequestMapping(value = ["/changeStatus"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun changeStatus(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model["redirectToLogin"] = true
            return model
        }
        val status = request.getParameter("status")
        if (status != null) {
            user.status = status
        }
        LoggedInUsers.updateUserStatus(user)
        LoggedInUsers.refreshLobbyPlayers()
        return refreshLobby(request, response)
    }

    @RequestMapping("/showLobbyPlayers.html")
    fun showLobbyPlayers(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("lobbyPlayers")
        modelAndView.addObject("user", getUser(request)!!)
        modelAndView.addObject("players", LoggedInUsers.getUsers())
        return modelAndView
    }

    @RequestMapping("/showModifyHand.html")
    fun showModifyHand(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        if (!game.isTestGame && !user.admin) {
            return ModelAndView("redirect:/showGame.html")
        }
        val player = game.playerMap[user.userId]!!
        val modelAndView = ModelAndView("modifyHand")
        modelAndView.addObject("user", user)
        modelAndView.addObject("cards", (game.allCards + game.cardsNotInSupply).sortedBy { it.name })
        modelAndView.addObject("myPlayer", player)
        modelAndView.addObject("players", game.players)
        return modelAndView
    }

    @RequestMapping("/modifyHand.html")
    fun modifyHand(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        if (!user.admin) {
            return ModelAndView("redirect:/showGame.html")
        }

        for (player in game.players) {

            val addActionsParam = request.getParameter("addActions_" + player.userId)
            val addBuysParam = request.getParameter("addBuys_" + player.userId)
            val addCoinsParam = request.getParameter("addCoins_" + player.userId)
            val addCoffersParam = request.getParameter("addCoffers_" + player.userId)
            val addVillagersParam = request.getParameter("addVillagers_" + player.userId)
            val addDebtParam = request.getParameter("addDebt_" + player.userId)
            val addCardsParam = request.getParameter("addCards_" + player.userId)

            val removeCardsFromSupply: Boolean = request.getParameter("removeCardsFromSupply_" + player.userId) == "on"

            if (addActionsParam.isNotEmpty()) {
                player.addActions(addActionsParam.toInt())
            }
            if (addBuysParam.isNotEmpty()) {
                player.addBuys(addBuysParam.toInt())
            }
            if (addCoinsParam.isNotEmpty()) {
                player.addCoins(addCoinsParam.toInt())
            }
            if (addCoffersParam.isNotEmpty()) {
                player.addCoffers(addCoffersParam.toInt())
            }
            if (addVillagersParam.isNotEmpty()) {
                player.addVillagers(addVillagersParam.toInt())
            }
            if (addDebtParam.isNotEmpty()) {
                player.addDebt(addDebtParam.toInt())
            }
            if (addCardsParam.isNotEmpty()) {
                player.drawCards(addCardsParam.toInt())
            }

            val currentHandChoice = request.getParameter("currentHandChoice_" + player.userId)
            val currentCards = ArrayList(player.hand)
            if (currentHandChoice == "discard") {
                currentCards.forEach { player.discardCardFromHand(it) }
            } else if (currentHandChoice == "trash") {
                currentCards.forEach { player.trashCardFromHand(it) }
            }

            val parameterNames = request.parameterNames
            while (parameterNames.hasMoreElements()) {
                val name = parameterNames.nextElement() as String
                if (name.startsWith("card_") && name.endsWith("_" + player.userId)) {
                    val ids = name.substring(5)
                    val cardName = ids.substring(0, ids.indexOf("_"))
                    val numCards = KingdomUtil.getRequestInt(request, name, 0)
                    repeat(numCards) {
                        val supplyCard = game.getNewInstanceOfCard(cardName)
                        if (removeCardsFromSupply) {
                            game.removeCardFromSupply(supplyCard, false)
                        }
                        player.hand.add(supplyCard)
                    }
                }
            }
            game.refreshGame()
        }
        return ModelAndView("redirect:/showGame.html")
    }

    private fun showGame(game: Game?, user: User?): Boolean {
        return game != null && game.status != GameStatus.WaitingForPlayers && game.status != GameStatus.Finished && game.playerMap.containsKey(user!!.userId)
    }

    @ResponseBody
    @RequestMapping(value = ["/refreshLobby"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun refreshLobby(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        val refresh: RefreshLobby
        if (user == null) {
            refresh = RefreshLobby()
            refresh.isRedirectToLogin = true
        } else {
            refresh = user.refreshLobby
            if (user.isExpired) {
                KingdomUtil.logoutUser(user, request)
                refresh.isRedirectToLogin = true
            }
        }
        val game = getGame(request)
        if (showGame(game, user)) {
            refresh.isStartGame = true
        }
        val model = HashMap<String, Any>()
        model["redirectToLogin"] = refresh.isRedirectToLogin
        if (refresh.isRedirectToLogin) {
            refresh.isRedirectToLogin = false
        }
        model["startGame"] = refresh.isStartGame
        if (refresh.isStartGame) {
            refresh.isStartGame = false
        }
        var divsToLoad = 0
        model["refreshPlayers"] = refresh.isRefreshPlayers
        if (refresh.isRefreshPlayers) {
            divsToLoad++
            refresh.isRefreshPlayers = false
        }
        model["refreshGameRooms"] = refresh.isRefreshGameRooms
        if (refresh.isRefreshGameRooms) {
            divsToLoad++
            refresh.isRefreshGameRooms = false
        }
        model["refreshChat"] = refresh.isRefreshChat
        if (refresh.isRefreshChat) {
            divsToLoad++
            refresh.isRefreshChat = false
        }
        model["divsToLoad"] = divsToLoad

        return model
    }

    @RequestMapping("/getLobbyPlayersDiv")
    fun getLobbyPlayersDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val refresh: RefreshLobby
        if (user == null) {
            refresh = RefreshLobby()
            refresh.isRedirectToLogin = true
        } else {
            refresh = user.refreshLobby
            if (user.isExpired) {
                KingdomUtil.logoutUser(user, request)
                refresh.isRedirectToLogin = true
            }
        }
        val game = getGame(request)
        if (showGame(game, user)) {
            refresh.isStartGame = true
        }
        LoggedInUsers.refreshLobby(user!!)
        val modelAndView = ModelAndView("lobbyPlayersDiv")
        modelAndView.addObject("players", LoggedInUsers.getUsers())
        return modelAndView
    }

    @RequestMapping("/getLobbyChatDiv")
    fun getLobbyChatDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val refresh: RefreshLobby
        if (user == null) {
            refresh = RefreshLobby()
            refresh.isRedirectToLogin = true
        } else {
            refresh = user.refreshLobby
            if (user.isExpired) {
                KingdomUtil.logoutUser(user, request)
                refresh.isRedirectToLogin = true
            }
        }
        val game = getGame(request)
        if (showGame(game, user)) {
            refresh.isStartGame = true
        }
        LoggedInUsers.refreshLobby(user!!)
        var template = "lobbyChatDiv"
        if (KingdomUtil.isMobile(request)) {
            template = "lobbyChatDivMobile"
        }
        val modelAndView = ModelAndView(template)
        modelAndView.addObject("user", user)
        modelAndView.addObject("chats", lobbyChats.chats)
        return modelAndView
    }

    @RequestMapping("/getLobbyGameRoomsDiv")
    fun getLobbyGameRoomsDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val refresh: RefreshLobby
        if (user == null) {
            refresh = RefreshLobby()
            refresh.isRedirectToLogin = true
        } else {
            refresh = user.refreshLobby
            if (user.isExpired) {
                KingdomUtil.logoutUser(user, request)
                refresh.isRedirectToLogin = true
            }
        }
        val game = getGame(request)
        if (showGame(game, user)) {
            refresh.isStartGame = true
        }
        LoggedInUsers.refreshLobby(user!!)
        val modelAndView = ModelAndView("lobbyGameRoomsDiv")
        modelAndView.addObject("user", user)
        modelAndView.addObject("gameRooms", gameRoomManager.lobbyGameRooms)
        modelAndView.addObject("maxGameRoomLimitReached", gameRoomManager.maxGameRoomLimitReached())
        modelAndView.addObject("numGamesInProgress", gameRoomManager.gamesInProgress.size)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/showGamesInProgress.html")
    fun showGamesInProgress(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("gamesInProgress")
        modelAndView.addObject("games", gameRoomManager.gamesInProgress)
        return modelAndView
    }

    @RequestMapping("/showGameCards.html")
    fun showGameCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val modelAndView = ModelAndView("gameCards")

        val cards = game.kingdomCards.map { it.isHighlighted = false; it }.toMutableList()

        cards.filterIsInstance<MultiTypePile>().forEach { cards.addAll(it.otherCardsInPile) }

        if (game.isIncludeShelters) {
            cards.addAll(cardManager.shelters)
        }

        if (game.isIncludeRuins) {
            cards.addAll(cardManager.ruins)
        }

        modelAndView.addObject("adjustFontSizeForMobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("cards", cards)
        modelAndView.addObject("cardsNotInSupply", game.cardsNotInSupply)
        modelAndView.addObject("eventsAndLandmarksAndProjectsAndWays", (game.events + game.landmarks + game.projects + game.ways).map { it.isHighlighted = false; it })
        modelAndView.addObject("artifacts", game.artifacts)
        modelAndView.addObject("prizeCards", game.prizeCards)
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards && game.isIncludePlatinumCards)
        return modelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/useCoffers"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun useCoffers(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGameRooms.html")

            if (!player.isYourTurn) {
                player.showInfoMessage("You can only use Coffers on your turn")
                return emptyModelAndView
            }

            if (player.isCardsBought) {
                player.showInfoMessage("You can't use Coffers after you have bought a card")
                return emptyModelAndView
            }

            val choices = mutableListOf<Choice>()

            for (i in 0..player.coffers) {
                choices.add(Choice(i, i.toString()))
            }

            player.makeChoiceFromList(object : ChoiceActionCard {
                override val name: String
                    get() = "Coffers"

                override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                    player.useCoffers(choice)
                }
            }, "How many Coffers do you want to use?", choices)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/useVillagers"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun useVillagers(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGameRooms.html")

            if (!player.isYourTurn) {
                player.showInfoMessage("You can only use Villagers on your turn")
                return emptyModelAndView
            }

            if (player.isBuyPhase) {
                player.showInfoMessage("You can't use Villagers in your buy phase")
                return emptyModelAndView
            }

            val choices = mutableListOf<Choice>()

            for (i in 0..player.villagers) {
                choices.add(Choice(i, i.toString()))
            }

            player.makeChoiceFromList(object : ChoiceActionCard {
                override val name: String
                    get() = "Villagers"

                override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                    player.useVillagers(choice)
                }
            }, "How many Villagers do you want to use?", choices)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/payOffDebt"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun payOffDebt(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId] ?: return ModelAndView("redirect:/showGameRooms.html")

            if (!player.isYourTurn) {
                player.showInfoMessage("You can only pay off debt on your turn")
                return emptyModelAndView
            }

            if (player.hand.any { it.isTreasure } && !player.isTreasureCardsPlayedInBuyPhase) {
                player.yesNoChoice(object : ChoiceActionCard {
                    override val name: String = "PlayTreasuresBeforePayingOffDebt"

                    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
                        if (choice == 1) {
                            player.payOffDebt()
                        }
                    }

                }, "Are you sure you want to pay off debt before playing your treasure cards?")

                return emptyModelAndView
            }

            player.payOffDebt()
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/showTavernCards"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun showTavernCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId] as? HumanPlayer ?: return ModelAndView("redirect:/showGameRooms.html")
            player.showTavernCards()
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }
}
