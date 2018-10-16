package com.kingdom.web

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.actions.ActionResult
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import com.kingdom.service.*
import com.kingdom.util.KingdomUtil
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
                     private val userManager: UserManager,
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
        if (game == null || gameRoomManager.isUpdatingWebsite) {
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
            val includeTesting = user.admin
            addSelectCardsObjects(user, modelAndView, includeTesting)
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
            val includeTesting = false
            addSelectCardsObjects(user, modelAndView, includeTesting)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return ModelAndView("empty")
        }

    }

    private fun addSelectCardsObjects(user: User, modelAndView: ModelAndView, includeTesting: Boolean) {
        modelAndView.addObject("user", user)
        modelAndView.addObject("kingdomCards", cardManager.getCards(Deck.Kingdom, includeTesting))
        modelAndView.addObject("intrigueCards", cardManager.getCards(Deck.Intrigue, includeTesting))
        modelAndView.addObject("seasideCards", cardManager.getCards(Deck.Seaside, includeTesting))
        modelAndView.addObject("prosperityCards", cardManager.getCards(Deck.Prosperity, includeTesting))
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Deck.Cornucopia, includeTesting))
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Deck.Hinterlands, includeTesting))
        modelAndView.addObject("promoCards", cardManager.getCards(Deck.Promo, includeTesting))
        modelAndView.addObject("annotatedGames", gameManager.annotatedGames)
        modelAndView.addObject("recentGames", gameManager.getGameHistoryList(user.userId))
        modelAndView.addObject("excludedCards", user.excludedCardNames)
        modelAndView.addObject("recommendedSets", gameManager.recommendedSets)
    }

    @RequestMapping("/generateCards.html")
    @Throws(TemplateModelException::class)
    fun generateCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = User()
        val game = Game(gameManager, gameMessageService)

        val generateType = request.getParameter("generateType")

        val decks = ArrayList<Deck>()
        val customSelection = ArrayList<Card>()
        val excludedCards = ArrayList<Card>(0)
        parseCardSelectionRequest(request, user, decks, customSelection, excludedCards, generateType)

        setRandomizingOptions(request, game, customSelection, excludedCards, generateType)

        game.decks = decks
        cardManager.setRandomKingdomCards(game)

        user.excludedCards = KingdomUtil.getCommaSeparatedCardNames(excludedCards)
        userManager.saveUser(user)

        return showRandomConfirmPage(request, user, game)
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

                val generateType = request.getParameter("generateType")

                var numPlayers = 1
                var numComputerPlayers = 0
                var numEasyComputerPlayers = 0
                var numMediumComputerPlayers = 0
                var numHardComputerPlayers = 0
                var numBMUComputerPlayers = 0

                user.baseChecked = request.getParameter("deck_kingdom") != null
                user.intrigueChecked = request.getParameter("deck_intrigue") != null
                user.seasideChecked = request.getParameter("deck_seaside") != null
                user.prosperityChecked = request.getParameter("deck_prosperity") != null
                user.cornucopiaChecked = request.getParameter("deck_cornucopia") != null
                user.hinterlandsChecked = request.getParameter("deck_hinterlands") != null
                user.promoChecked = request.getParameter("promo_cards") != null
                user.alwaysPlayTreasureCards = KingdomUtil.getRequestBoolean(request, "playTreasureCards")
                user.showVictoryPoints = KingdomUtil.getRequestBoolean(request, "showVictoryPoints")
                user.identicalStartingHands = KingdomUtil.getRequestBoolean(request, "identicalStartingHands")

                user.baseWeight = KingdomUtil.getRequestInt(request, "deck_weight_kingdom", 3)
                user.intrigueWeight = KingdomUtil.getRequestInt(request, "deck_weight_intrigue", 3)
                user.seasideWeight = KingdomUtil.getRequestInt(request, "deck_weight_seaside", 3)
                user.prosperityWeight = KingdomUtil.getRequestInt(request, "deck_weight_prosperity", 3)
                user.cornucopiaWeight = KingdomUtil.getRequestInt(request, "deck_weight_cornucopia", 3)
                user.hinterlandsWeight = KingdomUtil.getRequestInt(request, "deck_weight_hinterlands", 3)
                user.promoWeight = KingdomUtil.getRequestInt(request, "deck_weight_promo", 3)

                for (i in 2..6) {
                    user.setPlayerDefault(i, request.getParameter("player" + i))

                    when {
                        request.getParameter("player" + i) == "human" -> numPlayers++
                        request.getParameter("player" + i) == "computer_easy" -> {
                            numPlayers++
                            numComputerPlayers++
                            numEasyComputerPlayers++
                        }
                        request.getParameter("player" + i) == "computer_medium" -> {
                            numPlayers++
                            numComputerPlayers++
                            numMediumComputerPlayers++
                        }
                        request.getParameter("player" + i) == "computer_hard" -> {
                            numPlayers++
                            numComputerPlayers++
                            numHardComputerPlayers++
                        }
                        request.getParameter("player" + i) == "computer_bmu" -> {
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

                game.isPlayTreasureCards = KingdomUtil.getRequestBoolean(request, "playTreasureCards")
                game.title = request.getParameter("title")
                game.isPrivateGame = KingdomUtil.getRequestBoolean(request, "privateGame")
                if (game.isPrivateGame) {
                    game.password = request.getParameter("gamePassword")
                }
                game.mobile = KingdomUtil.isMobile(request)

                val decks = ArrayList<Deck>()
                val customSelection = ArrayList<Card>()
                val excludedCards = ArrayList<Card>(0)
                parseCardSelectionRequest(request, user, decks, customSelection, excludedCards, generateType)

                if (generateType == "annotatedGame" || generateType == "recentGame" || generateType == "recommendedSet") {
                    var cards: String
                    var includePlatinumAndColony = false
                    if (generateType == "annotatedGame") {
                        val annotatedGame = gameManager.getAnnotatedGame(Integer.parseInt(request.getParameter("annotatedGameId")))
                        cards = annotatedGame.cards
                        includePlatinumAndColony = annotatedGame.includeColonyAndPlatinum
                        game.isAnnotatedGame = true
                    } else {
                        if (generateType == "recentGame") {
                            cards = request.getParameter("recentGameCards")
                            game.isRecentGame = true
                        } else {
                            cards = request.getParameter("recommendedSetCards")
                            game.isRecommendedSet = true
                        }
                        if (cards.endsWith("Platinum,Colony")) {
                            cards = cards.substring(0, cards.indexOf(",Platinum,Colony"))
                            includePlatinumAndColony = true
                        }
                    }
                    for (cardString in cards.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        val card = if (generateType == "annotatedGame") {
                            cardManager.getCard(cardString)
                        } else {
                            cardManager.getCard(cardString)
                        }
                        customSelection.add(card)
                    }

                    game.isAlwaysIncludeColonyAndPlatinum = includePlatinumAndColony
                }

                setRandomizingOptions(request, game, customSelection, excludedCards, generateType)

                game.decks = decks
                cardManager.setRandomKingdomCards(game)

                user.excludedCards = KingdomUtil.getCommaSeparatedCardNames(excludedCards)
                userManager.saveUser(user)

                return ModelAndView("redirect:/confirmCards.html")
            }
            return ModelAndView("redirect:/showGameRooms.html")
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun parseCardSelectionRequest(request: HttpServletRequest, user: User, decks: MutableList<Deck>, customSelection: MutableList<Card>, excludedCards: MutableList<Card>, generateType: String) {
        val parameterNames = request.parameterNames
        while (parameterNames.hasMoreElements()) {
            val name = parameterNames.nextElement() as String
            when {
                name.startsWith("deck_") && !name.startsWith("deck_weight_") -> {
                    val deck = Deck.valueOf(request.getParameter(name))
                    var weight = when (deck) {
                        Deck.Kingdom -> user.baseWeight
                        Deck.Intrigue -> user.intrigueWeight
                        Deck.Seaside -> user.seasideWeight
                        Deck.Prosperity -> user.prosperityWeight
                        Deck.Cornucopia -> user.cornucopiaWeight
                        Deck.Hinterlands -> user.hinterlandsWeight
                        else -> 3
                    }
                    if (weight > 5) {
                        weight = 5
                    }
                    for (i in 0 until weight) {
                        decks.add(deck)
                    }
                }
                name.startsWith("card_") -> {
                    val cardName = name.substring(5)
                    val card = cardManager.getCard(cardName)
                    customSelection.add(card)
                }
                name.startsWith("excluded_card_") -> {
                    val cardName = name.substring(14)
                    val card = cardManager.getCard(cardName)
                    excludedCards.add(card)
                }
            }
        }

        val promoCards = request.getParameter("promo_cards")
        if (promoCards != null && promoCards == "true") {
            for (i in 0 until user.promoWeight) {
                decks.add(Deck.Promo)
            }
        }
    }

    private fun setRandomizingOptions(request: HttpServletRequest, game: Game, customSelection: List<Card>, excludedCards: List<Card>, generateType: String) {
        val options = RandomizingOptions()

        options.excludedCards = excludedCards

        if (generateType == "custom" || generateType == "annotatedGame" || generateType == "recentGame" || generateType == "recommendedSet") {
            game.custom = true
            options.customSelection = customSelection
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
        val includeColonyAndPlatinum = game.isAlwaysIncludeColonyAndPlatinum || game.kingdomCards[0].isProsperity && !game.isNeverIncludeColonyAndPlatinum
        var playTreasureCardsRequired = false
        for (card in game.kingdomCards) {
            if (card.isPlayTreasureCardsRequired) {
                playTreasureCardsRequired = true
            }
        }
        val modelAndView = ModelAndView("randomConfirm")
        modelAndView.addObject("createGame", KingdomUtil.getRequestBoolean(request, "createGame"))
        modelAndView.addObject("player", HumanPlayer(user, game))
        modelAndView.addObject("currentPlayerId", -1)
        modelAndView.addObject("costDiscount", game.costDiscount)
//        modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
        modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
        modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
        modelAndView.addObject("cards", game.kingdomCards)
        modelAndView.addObject("includeColonyAndPlatinum", includeColonyAndPlatinum)
        modelAndView.addObject("playTreasureCardsRequired", playTreasureCardsRequired)
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
                cardManager.setRandomKingdomCards(game)
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

    @RequestMapping("/swapForTypeOfCard.html")
    fun swapForTypeOfCard(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == GameStatus.BeingConfigured) {
                cardManager.swapForTypeOfCard(game, request.getParameter("cardName"), request.getParameter("cardFilter"))
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
                var playTreasureCardsRequired = false
                var includePrizes = false
                for (card in game.kingdomCards) {
                    if (card.name == "Black Market") {
                        hasBlackMarket = true
                    } else if (card.name == "Tournament" || card.name == "Museum") {
                        includePrizes = true
                    }
                    if (card.isPlayTreasureCardsRequired) {
                        playTreasureCardsRequired = true
                    }
                }
                if (hasBlackMarket) {
                    setBlackMarketCards(game)
                }
                if (playTreasureCardsRequired) {
                    game.isPlayTreasureCards = true
                }
                if (game.isAlwaysIncludeColonyAndPlatinum || game.kingdomCards[0].isProsperity && !game.isNeverIncludeColonyAndPlatinum) {
                    game.isIncludeColonyCards = true
                    game.isIncludePlatinumCards = true
                }
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
        val blackMarketCards = allCards - game.kingdomCards
        Collections.shuffle(blackMarketCards)
        game.blackMarketCards = blackMarketCards.toMutableList()
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
            game.reset()
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
            game.players.forEach { it.currentAction = null }
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
            modelAndView.addObject("updatingWebsite", gameRoomManager.isUpdatingWebsite)
            modelAndView.addObject("updatingMessage", gameRoomManager.updatingMessage ?: "")
            modelAndView.addObject("showNews", gameRoomManager.isShowNews)
            modelAndView.addObject("news", gameRoomManager.news)

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
                val card = game.getSupplyCard(cardName)

                if (highlightSupplyCard(player, card)) {
                    if (action != null) {
                        handleCardClickedForAction(player, card, source)
                    } else {
                        player.buyCard(card)

                        if (player.buys == 0) {
                            player.endTurn(true)
                        }
                    }
                }
            }
            CardLocation.Hand -> {
                val card = findCardById(player.hand, cardId)!!
                if (highlightCard(player, card, source)) {
                    if (action != null) {
                        handleCardClickedForAction(player, card, source)
                    } else {
                        player.playCard(card)
                        game.refreshPlayerHandArea(player)
                    }
                }
            }
            CardLocation.Discard -> {
                val card = findCardById(player.discard, cardId)!!
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
        player.game.refreshPlayerHandArea(player)
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

        return if (!player.isYourTurn || card == null) {
            false
        } else if (action != null) {
            action.isCardActionable(card, CardLocation.Supply, player)
        } else {
            player.isCardBuyable(card)
        }
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
        game.refreshPlayerHandArea(player)

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
        player.game.refreshPlayerHandArea(player)

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
        game.refreshPlayerHandArea(player)

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
        val kingdomCards = game.kingdomCards.map { it.isHighlighted = highlightCard(player, it, CardLocation.Supply); it }
        modelAndView.addObject("kingdomCards", kingdomCards)
        val supplyCards = game.supplyCards.map { it.isHighlighted = highlightCard(player, it, CardLocation.Supply); it }
        modelAndView.addObject("supplyCards", supplyCards)

        try {
            val bw = BeansWrapper()
            modelAndView.addObject("supply", bw.wrap(game.pileAmounts))
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

            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun addPlayingAreaDataToModelView(game: Game, player: Player, modelAndView: ModelAndView) {
        addCardsPlayedDataToModelAndView(game, player, modelAndView)

        game.cardsBought.forEach { it.isHighlighted = false }
        modelAndView.addObject("cardsBought", game.cardsBought)
    }

    private fun addCardsPlayedDataToModelAndView(game: Game, player: Player, modelAndView: ModelAndView) {
        val cardsPlayed = game.cardsPlayed.map { it.isHighlighted = highlightCard(player, it, CardLocation.PlayArea); it }
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

            modelAndView.addObject("cardsBought", game.cardsBought)
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

            modelAndView.addObject("cardsBought", game.previousPlayerCardsBought)
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
            var template = "historyDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "historyDivMobile"
            }
            val modelAndView = ModelAndView(template)
            modelAndView.addObject("turnHistory", game.recentTurnHistory)
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
            }

            player.currentAction?.cardChoices?.forEach {
                it.isHighlighted = true
            }
        }

        modelAndView.addObject("user", user)
        modelAndView.addObject("player", player)
        modelAndView.addObject("currentPlayerId", game.currentPlayerId)
        modelAndView.addObject("currentPlayer", game.currentPlayer)

        modelAndView.addObject("gameStatus", game.status)
        modelAndView.addObject("costDiscount", game.costDiscount)
//            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
        modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
        modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
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

            modelAndView.addObject("showDuration", game.isShowDuration)
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
            modelAndView.addObject("showCoinTokens", game.isShowCoinTokens)
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
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
            val player = game.playerMap[user.userId]!!
            modelAndView.addObject("player", player)
            modelAndView.addObject("players", game.players)
            modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.trashedCards, true))
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
            modelAndView.addObject("showCoinTokens", game.isShowCoinTokens)
            modelAndView.addObject("showDuration", game.isShowDuration)
            modelAndView.addObject("showPrizeCards", game.isShowPrizeCards)
            modelAndView.addObject("prizeCards", game.prizeCardsString)
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
            modelAndView.addObject("user", user)
            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("gameEndReason", game.gameEndReason)
            modelAndView.addObject("winnerString", game.winnerString)
            modelAndView.addObject("players", game.players)
            modelAndView.addObject("turnHistory", game.recentTurnHistory)

            modelAndView.addObject("victoryCards", game.victoryCards)

            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)

            modelAndView.addObject("chats", game.chats)
            modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents)

            modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.trashedCards, true))

            modelAndView.addObject("logId", game.logId)

            modelAndView.addObject("showRepeatGameLink", game.isAllComputerOpponents)

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
            user.gameId = null
            user.stats = null
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
                val receivingUser = userManager.getUser(username)
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

    private fun sendPrivateChat(user: User, message: String?, receivingUserId: Int) {
        if (message != null && message != "" && receivingUserId > 0) {
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
        val receivingUserId = KingdomUtil.getRequestInt(request, "receivingUserId", 0)
        sendPrivateChat(user, message, receivingUserId)
        return refreshLobby(request, response)
    }

    private fun loadPlayerDialogContainingCards(request: HttpServletRequest, response: HttpServletResponse, templateFile: String): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView(templateFile)
            addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun logErrorAndReturnEmpty(t: Throwable, game: Game): ModelAndView {
        t.printStackTrace()
        val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
        game.logError(error)
        return ModelAndView("empty")
    }

    @RequestMapping("/loadNativeVillageDialog.html")
    fun loadNativeVillageDialog(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        return loadPlayerDialogContainingCards(request, response, "nativeVillageDialog")
    }

    @RequestMapping("/loadIslandCardsDialog.html")
    fun loadIslandCardsDialog(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        return loadPlayerDialogContainingCards(request, response, "islandCardsDialog")
    }

    @RequestMapping("/loadMuseumCardsDialog.html")
    fun loadMuseumCardsDialog(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        return loadPlayerDialogContainingCards(request, response, "museumCardsDialog")
    }

    @RequestMapping("/loadCityPlannerCardsDialog.html")
    fun loadCityPlannerCardsDialog(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        return loadPlayerDialogContainingCards(request, response, "cityPlannerCardsDialog")
    }

    private fun addGameObjects(game: Game, user: User, modelAndView: ModelAndView, request: HttpServletRequest) {
        val player = game.playerMap[user.userId]!!

        addPlayerAndGameDataToModelAndView(game, user, modelAndView, request)

        addSupplyDataToModelAndView(game, player, modelAndView)

        modelAndView.addObject("supplySize", game.pileAmounts.size)
        modelAndView.addObject("players", game.players)

        addPlayingAreaDataToModelView(game, player, modelAndView)

        modelAndView.addObject("turnHistory", game.recentTurnHistory)
        modelAndView.addObject("chats", game.chats)
        modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents)
        modelAndView.addObject("showDuration", game.isShowDuration)
        modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens)
        modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
        modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
        modelAndView.addObject("showCoinTokens", game.isShowCoinTokens)
        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
        modelAndView.addObject("showIslandCards", game.isShowIslandCards)
        modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
        modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)
        modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens)
        modelAndView.addObject("tradeRouteTokensOnMat", game.tradeRouteTokensOnMat)
        modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.trashedCards, true))
        modelAndView.addObject("prizeCards", game.prizeCardsString)

        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)

        modelAndView.addObject("showPrizeCards", game.isShowPrizeCards)

        modelAndView.addObject("gameEndReason", game.gameEndReason)
        modelAndView.addObject("winnerString", game.winnerString)
        modelAndView.addObject("showRepeatGameLink", game.isAllComputerOpponents)
        modelAndView.addObject("logId", game.logId)
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }

    private fun getGame(request: HttpServletRequest): Game? {
        val gameId = request.session.getAttribute("gameId") ?: return null
        return gameRoomManager.getGame(gameId as String)
    }

    @RequestMapping("/gameHistory.html")
    fun gameHistory(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("gameHistory")
        modelAndView.addObject("user", user)
        modelAndView.addObject("games", gameManager.gameHistoryList)
        return modelAndView
    }

    @RequestMapping("/gamePlayersHistory.html")
    fun gamePlayersHistory(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val gameId = request.getParameter("gameId")
        val modelAndView = ModelAndView("gamePlayersHistory")
        modelAndView.addObject("user", user)
        modelAndView.addObject("players", gameManager.getGamePlayersHistory(gameId))
        modelAndView.addObject("gameId", gameId)
        return modelAndView
    }

    @RequestMapping("/playerGameHistory.html")
    fun playerGameHistory(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("gameHistory")
        modelAndView.addObject("user", user)
        val userId = Integer.parseInt(request.getParameter("userId"))
        modelAndView.addObject("games", gameManager.getGameHistoryList(userId))
        return modelAndView
    }

    @RequestMapping("/gameErrors.html")
    fun gameErrors(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("gameErrors")
        modelAndView.addObject("user", user)
        modelAndView.addObject("errors", gameManager.gameErrors)
        return modelAndView
    }

    @RequestMapping("/deleteGameError.html")
    fun deleteGameError(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val errorId = Integer.parseInt(request.getParameter("errorId"))
        gameManager.deleteGameError(errorId)
        return gameErrors(request, response)
    }

    @RequestMapping("/showGameLog.html")
    fun showGameLog(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("gameLog")
        val logId = KingdomUtil.getRequestInt(request, "logId", -1)
        val gameId = request.getParameter("gameId")
        var logs = arrayOfNulls<String>(0)
        var log: GameLog? = null
        if (logId > 0) {
            log = gameManager.getGameLog(logId)
        } else if (gameId != null) {
            log = gameManager.getGameLogByGameId(gameId)
        }
        val logNotFound: Boolean
        if (log != null) {
            logNotFound = false
            logs = log.log!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        } else {
            logNotFound = true
        }
        modelAndView.addObject("logs", logs)
        modelAndView.addObject("logNotFound", logNotFound)
        return modelAndView
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

    @RequestMapping("/getPlayerStatsDiv")
    fun getPlayerStatsDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return ModelAndView("redirect:/login.html")
        val modelAndView = ModelAndView("playerStatsDiv")
        userManager.calculateGameStats(user)
        modelAndView.addObject("user", user)
        return modelAndView
    }

    @RequestMapping("/overallGameStats.html")
    fun overallGameStats(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("overallStats")
        val stats = gameManager.overallStats
        val todayStats = gameManager.overallStatsForToday
        val yesterdayStats = gameManager.overallStatsForYesterday
        val weekStats = gameManager.overallStatsForPastWeek
        val monthStats = gameManager.overallStatsForPastMonth
        modelAndView.addObject("overallStats", stats)
        modelAndView.addObject("todayStats", todayStats)
        modelAndView.addObject("yesterdayStats", yesterdayStats)
        modelAndView.addObject("weekStats", weekStats)
        modelAndView.addObject("monthStats", monthStats)
        return modelAndView
    }

    @RequestMapping("/userStats.html")
    fun userStats(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("userStats")
        val stats = gameManager.userStats
        modelAndView.addObject("stats", stats)
        return modelAndView
    }

    @RequestMapping("/annotatedGames.html")
    fun annotatedGames(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("annotatedGames")
        val games = gameManager.annotatedGames
        modelAndView.addObject("games", games)
        return modelAndView
    }

    @RequestMapping("/saveAnnotatedGame.html")
    fun saveAnnotatedGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }

        val cardsNames = ArrayList<String>()
        val parameterNames = request.parameterNames
        while (parameterNames.hasMoreElements()) {
            val name = parameterNames.nextElement() as String
            if (name.startsWith("card_")) {
                val cardName = name.substring(5)
                cardsNames.add(cardName)
            }
        }
        val game: AnnotatedGame
        val id = request.getParameter("id")
        if (id == "0") {
            game = AnnotatedGame()
        } else {
            game = gameManager.getAnnotatedGame(Integer.parseInt(id))
        }
        game.title = request.getParameter("title")
        game.cards = KingdomUtil.implode(cardsNames, ",")
        game.includeColonyAndPlatinum = KingdomUtil.getRequestBoolean(request, "includeColonyAndPlatinumCards")
        gameManager.saveAnnotatedGame(game)
        return annotatedGames(request, response)
    }

    @RequestMapping("/deleteAnnotatedGame.html")
    fun deleteAnnotatedGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val id = request.getParameter("id")
        val game = gameManager.getAnnotatedGame(Integer.parseInt(id))
        gameManager.deleteAnnotatedGame(game)
        return annotatedGames(request, response)
    }

    @RequestMapping("/showAnnotatedGame.html")
    fun showAnnotatedGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("annotatedGame")
        val id = request.getParameter("id")

        val game: AnnotatedGame
        val selectedCards = ArrayList<String>()
        if (id == "0") {
            game = AnnotatedGame()
        } else {
            game = gameManager.getAnnotatedGame(Integer.parseInt(id))
            for (cardName in game.cards.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val card = cardManager.getCard(cardName)
                selectedCards.add(card.name)
            }
        }

        modelAndView.addObject("user", user)
        modelAndView.addObject("selectedCards", selectedCards)
        modelAndView.addObject("kingdomCards", cardManager.getCards(Deck.Kingdom, true))
        modelAndView.addObject("intrigueCards", cardManager.getCards(Deck.Intrigue, true))
        modelAndView.addObject("seasideCards", cardManager.getCards(Deck.Seaside, true))
        modelAndView.addObject("prosperityCards", cardManager.getCards(Deck.Prosperity, true))
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Deck.Cornucopia, true))
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Deck.Hinterlands, true))
        modelAndView.addObject("promoCards", cardManager.getCards(Deck.Promo, true))
        modelAndView.addObject("game", game)
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
        modelAndView.addObject("cards", game.cardMap.values)
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
                    for (i in 0 until numCards) {
                        player.hand.add(game.getSupplyCard(cardName))
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
        modelAndView.addObject("updatingWebsite", gameRoomManager.isUpdatingWebsite)
        modelAndView.addObject("updatingMessage", gameRoomManager.updatingMessage ?: "")
        modelAndView.addObject("showNews", gameRoomManager.isShowNews)
        modelAndView.addObject("news", gameRoomManager.news)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    @RequestMapping("/showGamesInProgress.html")
    fun showGamesInProgress(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val modelAndView = ModelAndView("gamesInProgress")
        modelAndView.addObject("games", gameRoomManager.gamesInProgress)
        return modelAndView
    }

    @RequestMapping("/toggleSound.html")
    fun toggleSound(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        user!!.toggleSoundDefault()
        userManager.saveUser(user)
        return ModelAndView("empty")
    }

    @RequestMapping("/repeatGame.html")
    fun repeatGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        if (gameRoomManager.isUpdatingWebsite) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        game.repeat()
        return ModelAndView("redirect:/showGame.html")
    }

    @RequestMapping("/showGameCards.html")
    fun showGameCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val modelAndView = ModelAndView("gameCards")
        game.kingdomCards.forEach { it.isHighlighted = false }
        modelAndView.addObject("cards", game.kingdomCards)
        modelAndView.addObject("prizeCards", game.prizeCards)
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards && game.isIncludePlatinumCards)
        return modelAndView
    }

    @ResponseBody
    @RequestMapping(value = ["/useCoinTokens"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun useCoinTokens(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)

        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request)
        } else if (game == null) {
            return ModelAndView("redirect:/showGameRooms.html")
        }

        try {
            val player = game.playerMap[user.userId]
            if (player == null) {
                return ModelAndView("redirect:/showGameRooms.html")
            }
            //todo
//            game.showUseFruitTokensCardAction(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return emptyModelAndView
    }

    @RequestMapping("/recommendedSets.html")
    fun recommendedSets(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("recommendedSets")
        val recommendedSets = gameManager.recommendedSets
        modelAndView.addObject("recommendedSets", recommendedSets)
        return modelAndView
    }

    @RequestMapping("/saveRecommendedSet.html")
    fun saveRecommendedSet(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }

        val set: RecommendedSet
        val id = request.getParameter("id")
        if (id == "0") {
            set = RecommendedSet()
        } else {
            set = gameManager.getRecommendedSet(Integer.parseInt(id))
        }
        set.name = request.getParameter("name")
        set.deck = request.getParameter("deck")
        set.cards = request.getParameter("cards")
        gameManager.saveRecommendedSet(set)
        return recommendedSets(request, response)
    }

    @RequestMapping("/deleteRecommendedSet.html")
    fun deleteRecommendedSet(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val id = request.getParameter("id")
        val set = gameManager.getRecommendedSet(Integer.parseInt(id))
        gameManager.deleteRecommendedSet(set)
        return recommendedSets(request, response)
    }

    @RequestMapping("/showRecommendedSet.html")
    fun showRecommendedSet(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val modelAndView = ModelAndView("recommendedSet")
        val id = request.getParameter("id")

        val set: RecommendedSet
        if (id == "0") {
            set = RecommendedSet()
        } else {
            set = gameManager.getRecommendedSet(Integer.parseInt(id))
        }

        modelAndView.addObject("set", set)
        return modelAndView
    }
}
