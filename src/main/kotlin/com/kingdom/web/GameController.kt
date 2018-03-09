package com.kingdom.web

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.service.*
import com.kingdom.util.KingdomUtil
import freemarker.ext.beans.BeansWrapper
import freemarker.template.TemplateModelException
import org.apache.commons.collections.CollectionUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class GameController(private var cardManager: CardManager,
                     private var userManager: UserManager,
                     private var gameManager: GameManager,
                     private var gameRoomManager: GameRoomManager,
                     private var lobbyChats: LobbyChats) {

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
        modelAndView.addObject("alchemyCards", cardManager.getCards(Deck.Alchemy, includeTesting))
        modelAndView.addObject("prosperityCards", cardManager.getCards(Deck.Prosperity, includeTesting))
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Deck.Cornucopia, includeTesting))
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Deck.Hinterlands, includeTesting))
        modelAndView.addObject("promoCards", cardManager.getCards(Deck.Promo, includeTesting))
        modelAndView.addObject("salvationCards", cardManager.getCards(Deck.Salvation, includeTesting))
        modelAndView.addObject("fairyTaleCards", cardManager.getCards(Deck.FairyTale, includeTesting))
        modelAndView.addObject("proletariatCards", cardManager.getCards(Deck.Proletariat, includeTesting))
        modelAndView.addObject("fanCards", cardManager.getCards(Deck.Fan, includeTesting))
        modelAndView.addObject("annotatedGames", gameManager.annotatedGames)
        modelAndView.addObject("recentGames", gameManager.getGameHistoryList(user.userId))
        modelAndView.addObject("excludedCards", user.excludedCardNames)
        modelAndView.addObject("recommendedSets", gameManager.recommendedSets)
    }

    @RequestMapping("/generateCards.html")
    @Throws(TemplateModelException::class)
    fun generateCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = User()
        val game = OldGame(-1)

        val generateType = request.getParameter("generateType")
        val includeLeaders = KingdomUtil.getRequestBoolean(request, "include_leaders")

        if (includeLeaders) {
            game.isUsingLeaders = true
            game.availableLeaders = cardManager.availableLeaderCards
        }

        val decks = ArrayList<Deck>()
        val customSelection = ArrayList<Card>()
        val excludedCards = ArrayList<Card>(0)
        parseCardSelectionRequest(request, user, game, decks, customSelection, excludedCards, generateType)

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
            if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {

                val generateType = request.getParameter("generateType")

                var numPlayers = 1
                var numComputerPlayers = 0
                var numEasyComputerPlayers = 0
                var numMediumComputerPlayers = 0
                var numHardComputerPlayers = 0
                var numBMUComputerPlayers = 0

                val includeLeaders = KingdomUtil.getRequestBoolean(request, "include_leaders")

                user.baseChecked = request.getParameter("deck_kingdom") != null
                user.intrigueChecked = request.getParameter("deck_intrigue") != null
                user.seasideChecked = request.getParameter("deck_seaside") != null
                user.alchemyChecked = request.getParameter("deck_alchemy") != null
                user.prosperityChecked = request.getParameter("deck_prosperity") != null
                user.cornucopiaChecked = request.getParameter("deck_cornucopia") != null
                user.hinterlandsChecked = request.getParameter("deck_hinterlands") != null
                user.promoChecked = request.getParameter("promo_cards") != null
                user.salvationChecked = request.getParameter("deck_salvation") != null
                user.fairyTaleChecked = request.getParameter("deck_fairytale") != null
                user.proletariatChecked = request.getParameter("deck_proletariat") != null
                user.otherFanCardsChecked = request.getParameter("other_fan_cards") != null
                user.leadersChecked = includeLeaders
                user.alwaysPlayTreasureCards = KingdomUtil.getRequestBoolean(request, "playTreasureCards")
                user.showVictoryPoints = KingdomUtil.getRequestBoolean(request, "showVictoryPoints")
                user.identicalStartingHands = KingdomUtil.getRequestBoolean(request, "identicalStartingHands")

                user.baseWeight = KingdomUtil.getRequestInt(request, "deck_weight_kingdom", 3)
                user.intrigueWeight = KingdomUtil.getRequestInt(request, "deck_weight_intrigue", 3)
                user.seasideWeight = KingdomUtil.getRequestInt(request, "deck_weight_seaside", 3)
                user.alchemyWeight = KingdomUtil.getRequestInt(request, "deck_weight_alchemy", 3)
                user.prosperityWeight = KingdomUtil.getRequestInt(request, "deck_weight_prosperity", 3)
                user.cornucopiaWeight = KingdomUtil.getRequestInt(request, "deck_weight_cornucopia", 3)
                user.hinterlandsWeight = KingdomUtil.getRequestInt(request, "deck_weight_hinterlands", 3)
                user.promoWeight = KingdomUtil.getRequestInt(request, "deck_weight_promo", 3)
                user.salvationWeight = KingdomUtil.getRequestInt(request, "deck_weight_salvation", 3)
                user.fairyTaleWeight = KingdomUtil.getRequestInt(request, "deck_weight_fairytale", 3)
                user.proletariatWeight = KingdomUtil.getRequestInt(request, "deck_weight_proletariat", 3)
                user.fanWeight = KingdomUtil.getRequestInt(request, "deck_weight_fan", 3)

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
                parseCardSelectionRequest(request, user, game, decks, customSelection, excludedCards, generateType)

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
                    val kingdomCards = ArrayList<Card>()
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

                if (includeLeaders) {
                    game.isUsingLeaders = true
                    game.availableLeaders = cardManager.availableLeaderCards
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

    private fun parseCardSelectionRequest(request: HttpServletRequest, user: User, game: OldGame, decks: MutableList<Deck>, customSelection: MutableList<Card>, excludedCards: MutableList<Card>, generateType: String) {
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
                        Deck.Alchemy -> user.alchemyWeight
                        Deck.Prosperity -> user.prosperityWeight
                        Deck.Cornucopia -> user.cornucopiaWeight
                        Deck.Hinterlands -> user.hinterlandsWeight
                        Deck.Salvation -> user.salvationWeight
                        Deck.FairyTale -> user.fairyTaleWeight
                        Deck.Proletariat -> user.proletariatWeight
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
        val otherFanCards = request.getParameter("other_fan_cards")
        if (otherFanCards != null && otherFanCards == "true") {
            for (i in 0 until user.fanWeight) {
                decks.add(Deck.Fan)
            }
        }
    }

    private fun setRandomizingOptions(request: HttpServletRequest, game: OldGame, customSelection: List<Card>, excludedCards: List<Card>, generateType: String) {
        val options = RandomizingOptions()

        options.excludedCards = excludedCards

        if (generateType == "custom" || generateType == "annotatedGame" || generateType == "recentGame" || generateType == "recommendedSet") {
            game.setCustom(true)
            options.isThreeToFiveAlchemy = true
            options.customSelection = customSelection
            if (KingdomUtil.getRequestBoolean(request, "includeColonyAndPlatinumCards")) {
                game.isAlwaysIncludeColonyAndPlatinum = true
            }
        } else {
            options.isThreeToFiveAlchemy = KingdomUtil.getRequestBoolean(request, "threeToFiveAlchemy")
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
            return if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                showRandomConfirmPage(request, user, game)
            } else {
                if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
    private fun showRandomConfirmPage(request: HttpServletRequest, user: User, game: OldGame): ModelAndView {
        val includeColonyAndPlatinum = game.isAlwaysIncludeColonyAndPlatinum || game.kingdomCards[0].isProsperity && !game.isNeverIncludeColonyAndPlatinum
        var playTreasureCardsRequired = false
        for (card in game.kingdomCards) {
            if (card.playTreasureCards) {
                playTreasureCardsRequired = true
            }
        }
        val modelAndView = ModelAndView("randomConfirm")
        modelAndView.addObject("createGame", KingdomUtil.getRequestBoolean(request, "createGame"))
        modelAndView.addObject("player", OldPlayer(user, game))
        modelAndView.addObject("currentPlayerId", game.currentPlayerId)
        modelAndView.addObject("costDiscount", game.costDiscount)
        modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
        modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
        addTrollTokenObjects(game, modelAndView)
        modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
        modelAndView.addObject("cards", game.kingdomCards)
        modelAndView.addObject("includeColonyAndPlatinum", includeColonyAndPlatinum)
        modelAndView.addObject("playTreasureCardsRequired", playTreasureCardsRequired)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("randomizerReplacementCardNotFound", game.isRandomizerReplacementCardNotFound)
        return modelAndView
    }

    @Throws(TemplateModelException::class)
    private fun addTrollTokenObjects(game: OldGame, modelAndView: ModelAndView) {
        modelAndView.addObject("showTrollTokens", game.isShowTrollTokens)
        if (game.isShowTrollTokens) {
            val bw = BeansWrapper()
            modelAndView.addObject("trollTokens", bw.wrap(game.trollTokens))
        }
    }

    @RequestMapping("/changeRandomCards.html")
    fun changeRandomCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        try {
            return if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.setRandomKingdomCards(game)
                confirmCards(request, response)
            } else {
                if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
            return if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.swapRandomCard(game, request.getParameter("cardName"))
                confirmCards(request, response)
            } else {
                if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
            return if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.swapForTypeOfCard(game, request.getParameter("cardName"), request.getParameter("cardType"))
                confirmCards(request, response)
            } else {
                if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
            return if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                val include = KingdomUtil.getRequestBoolean(request, "include")
                game.isAlwaysIncludeColonyAndPlatinum = include
                game.isNeverIncludeColonyAndPlatinum = !include
                confirmCards(request, response)
            } else {
                if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
            if (game.status == OldGame.STATUS_GAME_BEING_CONFIGURED) {
                game.status = OldGame.STATUS_GAME_WAITING_FOR_PLAYERS
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
                    if (card.playTreasureCards) {
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
                game.setGameManager(gameManager)
                game.init()
                addPlayerToGame(game, user)
            }
            return if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
                ModelAndView("redirect:/showGame.html")
            } else {
                ModelAndView("redirect:/showGameRooms.html")
            }
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun setBlackMarketCards(game: OldGame) {
        val allCards = cardManager.getAllCards(false)
        val blackMarketCards = CollectionUtils.subtract(allCards, game.kingdomCards) as MutableList<Card>
        Collections.shuffle(blackMarketCards)
        game.blackMarketCards = blackMarketCards
    }

    @RequestMapping("/cancelGame.html")
    fun cancelGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val gameIdParam = request.getParameter("gameId")
        if (user == null || gameIdParam == null) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val gameId = Integer.parseInt(gameIdParam)
        val game = gameRoomManager.getGame(gameId)
        if (game != null && (user.admin || game.creatorId == user.userId)) {
            game.reset()
        }
        return ModelAndView("redirect:/showGameRooms.html")
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
            if (game != null && game.status != OldGame.STATUS_GAME_WAITING_FOR_PLAYERS && game.status != OldGame.STATUS_GAME_FINISHED && game.playerMap.containsKey(user.userId)) {
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

    private fun addPlayerToGame(game: OldGame, user: User) {
        user.gameId = game.gameId
        user.status = ""
        game.addPlayer(user)
        LoggedInUsers.updateUserStatus(user)
        LoggedInUsers.refreshLobbyPlayers()
        LoggedInUsers.refreshLobbyGameRooms()
    }

    private fun removePlayerFromGame(game: OldGame, user: User) {
        user.gameId = 0
        game.removePlayer(user)
        LoggedInUsers.updateUser(user)
        LoggedInUsers.refreshLobbyPlayers()
        LoggedInUsers.refreshLobbyGameRooms()
    }

    @RequestMapping("/leaveGame.html")
    fun leaveGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        if (user.gameId == 0) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        val game = gameRoomManager.getGame(user.gameId)
        if (game == null || game.status != OldGame.STATUS_GAME_WAITING_FOR_PLAYERS) {
            return ModelAndView("redirect:/showGameRooms.html")
        } else {
            removePlayerFromGame(game, user)
        }
        return ModelAndView("redirect:/showGameRooms.html")
    }

    @RequestMapping("/joinGame.html")
    fun joinGame(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request) ?: return KingdomUtil.getLoginModelAndView(request)
        if (user.gameId != 0) {
            return ModelAndView("redirect:/showGameRooms.html")
        }
        val gameIdParam = request.getParameter("gameId")
        val game: OldGame?
        if (gameIdParam != null) {
            val gameId = Integer.parseInt(gameIdParam)
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
                    if (gameIdParam != null) {
                        request.session.setAttribute("gameId", Integer.parseInt(gameIdParam))
                    }
                    addPlayerToGame(game, user)
                }
                return if (game.status == OldGame.STATUS_GAME_IN_PROGRESS) {
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
    @RequestMapping(value = "/joinPrivateGame", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun joinPrivateGame(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        if (user == null) {
            model.put("redirectToLogin", true)
            return model
        }
        val gameIdParam = request.getParameter("gameId")
        val game: OldGame?
        if (gameIdParam != null) {
            val gameId = Integer.parseInt(gameIdParam)
            game = gameRoomManager.getGame(gameId)
        } else {
            game = getGame(request)
        }
        if (game == null) {
            model.put("redirectToLogin", true)
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
                if (gameIdParam != null) {
                    request.session.setAttribute("gameId", Integer.parseInt(gameIdParam))
                }
                addPlayerToGame(game, user)
            }

            model.put("message", message)
            model.put("start", game.status == OldGame.STATUS_GAME_IN_PROGRESS)

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
            game.refreshAll(player)
            game.closeLoadingDialog(player)
            addGameObjects(game, player, modelAndView, request)
            modelAndView.addObject("user", user)
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @ResponseBody
    @RequestMapping(value = "/refreshGame", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun refreshGame(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        val game = getGame(request)
        val model = HashMap<String, Any>()
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
        }
        try {
            val refresh = game.needsRefresh[user.userId]
            if (refresh == null) {
                model.put("redirectToLobby", true)
                return model
            }
            model.put("refreshEndTurn", refresh.isRefreshEndTurn)
            if (refresh.isRefreshEndTurn) {
                refresh.isRefreshEndTurn = false
                val player = game.playerMap[user.userId]!!
                if (player.turns > 0) {
                    val refreshHandArea = refresh.isRefreshHand || refresh.isRefreshHandArea || refresh.isRefreshDiscard
                    model.put("refreshHandOnEndTurn", refresh.isRefreshHandOnEndTurn)
                    refresh.isRefreshHandOnEndTurn = false
                    model.put("refreshSupplyOnEndTurn", refresh.isRefreshSupplyOnEndTurn)
                    refresh.isRefreshSupplyOnEndTurn = false
                }
                model.put("refreshPlayersOnEndTurn", refresh.isRefreshPlayers)
                refresh.isRefreshPlayers = false
                return model
            }
            model.put("refreshGameStatus", refresh.isRefreshGameStatus)
            if (refresh.isRefreshGameStatus) {
                refresh.isRefreshGameStatus = false
                model.put("gameStatus", game.status)
                val currentPlayer = (game.status == OldGame.STATUS_GAME_IN_PROGRESS && user.userId == game.currentPlayerId).toString()
                model.put("currentPlayer", currentPlayer)
            }
            model.put("closeCardActionDialog", refresh.isCloseCardActionDialog)
            if (refresh.isCloseCardActionDialog) {
                refresh.isCloseCardActionDialog = false
            }
            model.put("closeLoadingDialog", refresh.isCloseLoadingDialog)
            if (refresh.isCloseLoadingDialog) {
                refresh.isCloseLoadingDialog = false
            }
            var divsToLoad = 0
            model.put("refreshPlayers", refresh.isRefreshPlayers)
            if (refresh.isRefreshPlayers) {
                divsToLoad++
                refresh.isRefreshPlayers = false
            }
            model.put("refreshSupply", refresh.isRefreshSupply)
            if (refresh.isRefreshSupply) {
                divsToLoad++
                refresh.isRefreshSupply = false
            }
            model.put("refreshPlayingArea", refresh.isRefreshPlayingArea)
            if (refresh.isRefreshPlayingArea) {
                divsToLoad++
                refresh.isRefreshPlayingArea = false
            }
            model.put("refreshCardsPlayed", refresh.isRefreshCardsPlayedDiv)
            if (refresh.isRefreshCardsPlayedDiv) {
                divsToLoad++
                refresh.isRefreshCardsPlayedDiv = false
            }
            model.put("refreshCardsBought", refresh.isRefreshCardsBoughtDiv)
            if (refresh.isRefreshCardsBoughtDiv) {
                divsToLoad++
                refresh.isRefreshCardsBoughtDiv = false
            }
            model.put("refreshHistory", refresh.isRefreshHistory)
            if (refresh.isRefreshHistory) {
                divsToLoad++
                refresh.isRefreshHistory = false
            }
            model.put("refreshHandArea", refresh.isRefreshHandArea)
            if (refresh.isRefreshHandArea) {
                divsToLoad++
                refresh.isRefreshHandArea = false
            }
            model.put("refreshHand", refresh.isRefreshHand)
            if (refresh.isRefreshHand) {
                divsToLoad++
                refresh.isRefreshHand = false
            }
            model.put("refreshDiscard", refresh.isRefreshDiscard)
            if (refresh.isRefreshDiscard) {
                divsToLoad++
                refresh.isRefreshDiscard = false
            }
            model.put("refreshChat", refresh.isRefreshChat)
            if (refresh.isRefreshChat) {
                divsToLoad++
                refresh.isRefreshChat = false
            }
            model.put("refreshCardAction", refresh.isRefreshCardAction)
            if (refresh.isRefreshCardAction) {
                val player = game.playerMap[user.userId]!!
                if (player.oldCardAction == null) {
                    val error = GameError(GameError.GAME_ERROR, "Card action is null for user: " + player.username + ", show card action: " + player.isShowCardAction)
                    game.logError(error, false)
                    model.put("refreshCardAction", false)
                } else {
                    val cardAction = player.oldCardAction!!
                    model.put("cardActionCardsSize", cardAction.cards.size)
                    model.put("cardActionNumCards", cardAction.numCards)
                    model.put("cardActionType", cardAction.type)
                    model.put("cardActionWidth", cardAction.width)
                    model.put("cardActionSelectExact", cardAction.isSelectExact)
                    model.put("cardActionSelectUpTo", cardAction.isSelectUpTo)
                    model.put("cardActionSelectAtLeast", cardAction.isSelectAtLeast)
                    divsToLoad++
                }
                refresh.isRefreshCardAction = false
            }
            model.put("refreshInfoDialog", refresh.isRefreshInfoDialog)
            if (refresh.isRefreshInfoDialog) {
                val player = game.playerMap[user.userId]!!
                model.put("infoDialogHideMethod", player.infoDialog!!.hideMethod!!)
                model.put("infoDialogWidth", player.infoDialog!!.width)
                model.put("infoDialogHeight", player.infoDialog!!.height)
                model.put("infoDialogTimeout", player.infoDialog!!.timeout)
                divsToLoad++
                refresh.isRefreshInfoDialog = false
            }
            model.put("playBeep", refresh.isPlayBeep)
            if (refresh.isPlayBeep) {
                refresh.isPlayBeep = false
            }
            model.put("refreshTitle", refresh.isRefreshTitle)
            if (refresh.isRefreshTitle) {
                refresh.isRefreshTitle = false
                if (game.status == OldGame.STATUS_GAME_FINISHED) {
                    model.put("title", "Game Over")
                } else if (game.currentPlayerId == user.userId) {
                    model.put("title", "Your Turn")
                } else {
                    model.put("title", game.currentPlayer!!.username + "'s Turn")
                }
            }
            model.put("divsToLoad", divsToLoad)

            return model
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return model
        }

    }

    @ResponseBody
    @RequestMapping(value = "/clickCard", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun clickCard(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
        }
        try {
            val clickType = request.getParameter("clickType")
            val cardName = request.getParameter("cardName")
            if (cardName != null) {
                val player = game.playerMap[user.userId]
                if (player == null) {
                    model.put("redirectToLobby", true)
                    return model
                }
                game.cardClicked(player, clickType, cardName)
                game.closeLoadingDialog(player)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return refreshGame(request, response)
    }

    @ResponseBody
    @RequestMapping(value = "/playAllTreasureCards", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun playAllTreasureCards(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLogin", true)
            return model
        }
        val player = game.playerMap[user.userId]
        if (player == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLobby", true)
            return model
        }
        try {
            game.playAllTreasureCards(player)
            game.closeLoadingDialog(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return refreshGame(request, response)
    }

    @ResponseBody
    @RequestMapping(value = "/endTurn", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun endTurn(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLogin", true)
            return model
        }
        try {
            val player = game.playerMap[user.userId]!!
            game.endPlayerTurn(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return refreshGame(request, response)
    }

    @RequestMapping("/submitCardAction.html")
    fun submitCardAction(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            //todo error handling if choice is null
            val player = game.playerMap[user.userId]!!
            if (player.oldCardAction != null) {
                if (player.oldCardAction!!.type == OldCardAction.TYPE_INFO) {
                    game.cardActionSubmitted(player, emptyList(), null, null, -1)
                } else if (player.oldCardAction!!.type == OldCardAction.TYPE_YES_NO) {
                    if (request.getParameter("answer") == null) {
                        val error = GameError(GameError.GAME_ERROR, "Card Action answer was null")
                        game.logError(error, false)
                        //todo
                    } else {
                        game.cardActionSubmitted(player, emptyList(), request.getParameter("answer"), null, -1)
                    }
                } else if (player.oldCardAction!!.type == OldCardAction.TYPE_CHOICES) {
                    if (request.getParameter("choice") == null) {
                        val error = GameError(GameError.GAME_ERROR, "Card Action choice was null")
                        game.logError(error, false)
                        //todo
                    } else {
                        game.cardActionSubmitted(player, emptyList(), null, request.getParameter("choice"), -1)
                    }
                } else if (player.oldCardAction!!.type == OldCardAction.TYPE_CHOOSE_NUMBER_BETWEEN || player.oldCardAction!!.type == OldCardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN) {
                    if (request.getParameter("numberChosen") == null) {
                        val error = GameError(GameError.GAME_ERROR, "Card Action number chosen was null")
                        game.logError(error, false)
                        //todo
                    } else {
                        game.cardActionSubmitted(player, emptyList(), null, null, Integer.parseInt(request.getParameter("numberChosen")))
                    }
                } else {
                    if (request.getParameter("selectedCards") == null) {
                        val error = GameError(GameError.GAME_ERROR, "Card Action selected cards string was null")
                        game.logError(error, false)
                        //todo
                    } else {
                        val selectedCardsString = request.getParameter("selectedCards")
                        val selectedCardsStrings = selectedCardsString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val selectedCardNames = ArrayList<String>()
                        for (cardName in selectedCardsStrings) {
                            if (cardName != "") {
                                selectedCardNames.add(cardName)
                            }
                        }
                        game.cardActionSubmitted(player, selectedCardNames, null, null, -1)
                    }
                }
            }
            game.closeLoadingDialog(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return ModelAndView("empty")
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
        } else getSupplyDiv(request, user, game, game.currentPlayerId)
    }

    @RequestMapping("/getSupplyDivOnEndTurn.html")
    fun getSupplyDivOnEndTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else getSupplyDiv(request, user, game, 0)
    }

    private fun getSupplyDiv(request: HttpServletRequest, user: User, game: OldGame, currentPlayerId: Int): ModelAndView {
        try {
            var supplyDivTemplate = "supplyDiv"
            if (KingdomUtil.isMobile(request)) {
                supplyDivTemplate = "supplyDivMobile"
            }
            val modelAndView = ModelAndView(supplyDivTemplate)
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", currentPlayerId)
            modelAndView.addObject("kingdomCards", game.kingdomCards)
            modelAndView.addObject("supplyCards", game.supplyCards)
            try {
                val bw = BeansWrapper()
                modelAndView.addObject("supply", bw.wrap(game.supply))
                if (game.isShowEmbargoTokens) {
                    modelAndView.addObject("embargoTokens", bw.wrap(game.embargoTokens))
                }
                addTrollTokenObjects(game, modelAndView)
                if (game.isTrackTradeRouteTokens) {
                    modelAndView.addObject("tradeRouteTokenMap", bw.wrap(game.tradeRouteTokenMap))
                }
            } catch (e: TemplateModelException) {
                //
            }

            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens)
            modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens)
            modelAndView.addObject("tradeRouteTokensOnMat", game.tradeRouteTokensOnMat)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getPreviousPlayerPlayingAreaDiv.html")
    fun getPreviousPlayerPlayingAreaDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
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
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.previousPlayerId)
            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("currentPlayer", game.previousPlayer!!)
            modelAndView.addObject("user", user)
            modelAndView.addObject("cardsPlayed", game.previousPlayerCardsPlayed)
            modelAndView.addObject("cardsBought", game.previousPlayerCardsBought)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("showPotions", game.isUsePotions)
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
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
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("currentPlayer", game.currentPlayer!!)
            modelAndView.addObject("user", user)
            modelAndView.addObject("cardsPlayed", game.cardsPlayed)
            modelAndView.addObject("cardsBought", game.cardsBought)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("showPotions", game.isUsePotions)
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

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
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("currentPlayer", game.currentPlayer!!)
            modelAndView.addObject("user", user)
            modelAndView.addObject("cardsPlayed", game.cardsPlayed)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
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
            val player = game.playerMap[user.userId]!!
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("gameStatus", game.status)
            modelAndView.addObject("currentPlayer", game.currentPlayer!!)
            modelAndView.addObject("user", user)
            modelAndView.addObject("cardsBought", game.cardsBought)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("showPotions", game.isUsePotions)
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
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
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } catch (t: Throwable) {
            t.printStackTrace()
            return logErrorAndReturnEmpty(t, game)
        }

    }

    @RequestMapping("/getHandAreaDiv.html")
    fun getHandAreaDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else getHandAreaDiv(request, user, game, game.currentPlayerId)
    }

    @RequestMapping("/getHandAreaDivOnEndTurn.html")
    fun getHandAreaDivOnEndTurn(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        return if (user == null || game == null) {
            ModelAndView("redirect:/login.html")
        } else getHandAreaDiv(request, user, game, 0)
    }

    private fun getHandAreaDiv(request: HttpServletRequest, user: User, game: OldGame, currentPlayerId: Int): ModelAndView {
        try {
            var template = "handAreaDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "handAreaDivMobile"
            }
            val modelAndView = ModelAndView(template)
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("showDuration", game.isShowDuration)
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards)
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards)
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
            modelAndView.addObject("showFruitTokens", game.isShowFruitTokens)
            modelAndView.addObject("showCattleTokens", game.isShowCattleTokens)
            modelAndView.addObject("showSins", game.isShowSins)
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("currentPlayerId", currentPlayerId)
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
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
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
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
            val player = game.playerMap[user.userId]!!
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
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
            var template = "cardActionDiv"
            if (KingdomUtil.isMobile(request)) {
                template = "cardActionDivMobile"
            }
            val modelAndView = ModelAndView(template)
            val player = game.playerMap[user.userId]!!
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            if (player.oldCardAction!!.type == OldCardAction.TYPE_SETUP_LEADERS) {
                modelAndView.addObject("kingdomCards", game.kingdomCards)
                modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards && game.isIncludePlatinumCards)
            }
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
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("players", game.players)
            modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.trashedCards, true))
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards)
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards)
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
            modelAndView.addObject("showFruitTokens", game.isShowFruitTokens)
            modelAndView.addObject("showCattleTokens", game.isShowCattleTokens)
            modelAndView.addObject("showSins", game.isShowSins)
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

            modelAndView.addObject("showGarden", game.isShowGardens)
            modelAndView.addObject("showFarmlands", game.isShowFarmlands)
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
            modelAndView.addObject("showVineyard", game.isShowVineyard)
            modelAndView.addObject("showSilkRoads", game.isShowSilkRoads)
            modelAndView.addObject("showCathedral", game.isShowCathedral)
            modelAndView.addObject("showFairgrounds", game.isShowFairgrounds)
            modelAndView.addObject("showGreatHall", game.isShowGreatHall)
            modelAndView.addObject("showHarem", game.isShowHarem)
            modelAndView.addObject("showDuke", game.isShowDuke)
            modelAndView.addObject("showNobles", game.isShowNobles)
            modelAndView.addObject("showArchbishops", game.isShowArchbishops)
            modelAndView.addObject("showIslandCards", game.isShowIslandCards)
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards)
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards)
            modelAndView.addObject("showColony", game.isIncludeColonyCards)
            modelAndView.addObject("showSins", game.isShowSins)
            modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)
            modelAndView.addObject("showEnchantedPalace", game.isCheckEnchantedPalace)
            modelAndView.addObject("showHedgeWizard", game.isShowHedgeWizard)
            modelAndView.addObject("showGoldenTouch", game.isShowGoldenTouch)

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

    @RequestMapping("/getInfoDialogDiv.html")
    fun getInfoDialogDiv(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        try {
            val modelAndView = ModelAndView("infoDialogDiv")
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
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
            user.gameId = 0
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
    @RequestMapping(value = "/quitGame", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun quitGame(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
        }
        try {
            if (game.status == OldGame.STATUS_GAME_WAITING_FOR_PLAYERS) {
                game.reset()
                model.put("redirectToLobby", true)
                return model
            }
            if (game.status != OldGame.STATUS_GAME_FINISHED) {
                val player = game.playerMap[user.userId]!!
                game.playerQuitGame(player)
            }
            return refreshGame(request, response)
        } catch (t: Throwable) {
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return model
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
                if (receivingUser.gameId > 0) {
                    val game = gameRoomManager.getGame(receivingUser.gameId)!!
                    game.addPrivateChat(user, receivingUser, message)
                } else {
                    lobbyChats.addPrivateChat(user, receivingUser, message)
                    LoggedInUsers.refreshLobbyChat()
                }
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/sendChat", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun sendChat(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
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
            return refreshGame(request, response)
        } catch (t: Throwable) {
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
            return model
        }

    }

    @ResponseBody
    @RequestMapping(value = "/sendLobbyChat", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun sendLobbyChat(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLogin", true)
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
    @RequestMapping(value = "/sendPrivateChat", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun sendPrivateChat(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLogin", true)
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
            val player = game.playerMap[user.userId]
            modelAndView.addObject("player", player)
            modelAndView.addObject("currentPlayerId", game.currentPlayerId)
            modelAndView.addObject("costDiscount", game.costDiscount)
            modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
            modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
            addTrollTokenObjects(game, modelAndView)
            modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
            return modelAndView
        } catch (t: Throwable) {
            return logErrorAndReturnEmpty(t, game)
        }

    }

    private fun logErrorAndReturnEmpty(t: Throwable, game: OldGame): ModelAndView {
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

    private fun addGameObjects(game: OldGame, player: OldPlayer, modelAndView: ModelAndView, request: HttpServletRequest) {
        val bw = BeansWrapper()
        modelAndView.addObject("player", player)
        modelAndView.addObject("kingdomCards", game.kingdomCards)
        modelAndView.addObject("supplyCards", game.supplyCards)
        try {
            modelAndView.addObject("supply", bw.wrap(game.supply))
            if (game.isShowEmbargoTokens) {
                modelAndView.addObject("embargoTokens", bw.wrap(game.embargoTokens))
            }
            addTrollTokenObjects(game, modelAndView)
            if (game.isTrackTradeRouteTokens) {
                modelAndView.addObject("tradeRouteTokenMap", bw.wrap(game.tradeRouteTokenMap))
            }
        } catch (e: TemplateModelException) {
            //
        }

        modelAndView.addObject("supplySize", game.supply.size)
        modelAndView.addObject("players", game.players)
        modelAndView.addObject("currentPlayer", game.currentPlayer!!)
        modelAndView.addObject("currentPlayerId", game.currentPlayerId)
        modelAndView.addObject("gameStatus", game.status)
        modelAndView.addObject("cardsPlayed", game.cardsPlayed)
        modelAndView.addObject("cardsBought", game.cardsBought)
        modelAndView.addObject("turnHistory", game.recentTurnHistory)
        modelAndView.addObject("chats", game.chats)
        modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents)
        modelAndView.addObject("costDiscount", game.costDiscount)
        modelAndView.addObject("fruitTokensPlayed", game.fruitTokensPlayed)
        modelAndView.addObject("actionCardDiscount", game.actionCardDiscount)
        modelAndView.addObject("actionCardsInPlay", game.actionCardsInPlay)
        modelAndView.addObject("showDuration", game.isShowDuration)
        modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens)
        modelAndView.addObject("showIslandCards", game.isShowIslandCards)
        modelAndView.addObject("showMuseumCards", game.isShowMuseumCards)
        modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards)
        modelAndView.addObject("showNativeVillage", game.isShowNativeVillage)
        modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins)
        modelAndView.addObject("showFruitTokens", game.isShowFruitTokens)
        modelAndView.addObject("showCattleTokens", game.isShowCattleTokens)
        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
        modelAndView.addObject("showPotions", game.isUsePotions)
        modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards)
        modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints)
        modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens)
        modelAndView.addObject("tradeRouteTokensOnMat", game.tradeRouteTokensOnMat)
        modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.trashedCards, true))
        modelAndView.addObject("prizeCards", game.prizeCardsString)

        modelAndView.addObject("showGarden", game.isShowGardens)
        modelAndView.addObject("showFarmlands", game.isShowFarmlands)
        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins)
        modelAndView.addObject("showSins", game.isShowSins)
        modelAndView.addObject("showVineyard", game.isShowVineyard)
        modelAndView.addObject("showSilkRoads", game.isShowSilkRoads)
        modelAndView.addObject("showCathedral", game.isShowCathedral)
        modelAndView.addObject("showFairgrounds", game.isShowFairgrounds)
        modelAndView.addObject("showGreatHall", game.isShowGreatHall)
        modelAndView.addObject("showHarem", game.isShowHarem)
        modelAndView.addObject("showDuke", game.isShowDuke)
        modelAndView.addObject("showNobles", game.isShowNobles)
        modelAndView.addObject("showArchbishops", game.isShowArchbishops)
        modelAndView.addObject("showIslandCards", game.isShowIslandCards)
        modelAndView.addObject("showMuseumCards", game.isShowMuseumCards)
        modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards)
        modelAndView.addObject("showColony", game.isIncludeColonyCards)
        modelAndView.addObject("showEnchantedPalace", game.isCheckEnchantedPalace)
        modelAndView.addObject("showHedgeWizard", game.isShowHedgeWizard)
        modelAndView.addObject("showGoldenTouch", game.isShowGoldenTouch)

        modelAndView.addObject("showPrizeCards", game.isShowPrizeCards)

        modelAndView.addObject("gameEndReason", game.gameEndReason)
        modelAndView.addObject("winnerString", game.winnerString)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        modelAndView.addObject("showRepeatGameLink", game.isAllComputerOpponents)
        modelAndView.addObject("logId", game.logId)
    }

    fun setCardManager(cardManager: CardManager) {
        this.cardManager = cardManager
    }

    fun setUserManager(userManager: UserManager) {
        this.userManager = userManager
    }

    fun setGameManager(gameManager: GameManager) {
        this.gameManager = gameManager
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }

    private fun getGame(request: HttpServletRequest): OldGame? {
        val gameId = request.session.getAttribute("gameId") ?: return null
        return gameRoomManager.getGame(gameId as Int)
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
        val gameId = Integer.parseInt(request.getParameter("gameId"))
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
        val gameId = KingdomUtil.getRequestInt(request, "gameId", -1)
        var logs = arrayOfNulls<String>(0)
        var log: GameLog? = null
        if (logId > 0) {
            log = gameManager.getGameLog(logId)
        } else if (gameId > 0) {
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
    @RequestMapping(value = "/changeStatus", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun changeStatus(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val user = getUser(request)
        if (user == null) {
            val model = HashMap<String, Any>()
            model.put("redirectToLogin", true)
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
        modelAndView.addObject("alchemyCards", cardManager.getCards(Deck.Alchemy, true))
        modelAndView.addObject("prosperityCards", cardManager.getCards(Deck.Prosperity, true))
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Deck.Cornucopia, true))
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Deck.Hinterlands, true))
        modelAndView.addObject("proletariatCards", cardManager.getCards(Deck.Proletariat, true))
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
        val player = game.playerMap[user.userId]
        val modelAndView = ModelAndView("modifyHand")
        modelAndView.addObject("user", user)
        modelAndView.addObject("cards", game.supplyMap.values)
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
        if (!game.isTestGame && !user.admin) {
            return ModelAndView("redirect:/showGame.html")
        }

        for (player in game.players) {
            val currentHandChoice = request.getParameter("currentHandChoice_" + player.userId)
            val currentCards = ArrayList(player.hand)
            if (currentHandChoice == "discard") {
                for (card in currentCards) {
                    player.discardCardFromHand(card)
                }
            } else if (currentHandChoice == "trash") {
                for (card in currentCards) {
                    player.removeCardFromHand(card)
                }
            }

            val parameterNames = request.parameterNames
            while (parameterNames.hasMoreElements()) {
                val name = parameterNames.nextElement() as String
                if (name.startsWith("card_") && name.endsWith("_" + player.userId)) {
                    val ids = name.substring(5)
                    val cardName = ids.substring(0, ids.indexOf("_"))
                    val card = game.supplyMap[cardName]!!
                    val numCards = KingdomUtil.getRequestInt(request, name, 0)
                    for (i in 0 until numCards) {
                        player.addCardToHand(card)
                    }
                }
            }
            game.refreshHand(player)
        }
        return ModelAndView("redirect:/showGame.html")
    }

    private fun showGame(game: OldGame?, user: User?): Boolean {
        return game != null && game.status != OldGame.STATUS_GAME_WAITING_FOR_PLAYERS && game.status != OldGame.STATUS_GAME_FINISHED && game.playerMap.containsKey(user!!.userId)
    }

    @ResponseBody
    @RequestMapping(value = "/refreshLobby", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
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
        model.put("redirectToLogin", refresh.isRedirectToLogin)
        if (refresh.isRedirectToLogin) {
            refresh.isRedirectToLogin = false
        }
        model.put("startGame", refresh.isStartGame)
        if (refresh.isStartGame) {
            refresh.isStartGame = false
        }
        var divsToLoad = 0
        model.put("refreshPlayers", refresh.isRefreshPlayers)
        if (refresh.isRefreshPlayers) {
            divsToLoad++
            refresh.isRefreshPlayers = false
        }
        model.put("refreshGameRooms", refresh.isRefreshGameRooms)
        if (refresh.isRefreshGameRooms) {
            divsToLoad++
            refresh.isRefreshGameRooms = false
        }
        model.put("refreshChat", refresh.isRefreshChat)
        if (refresh.isRefreshChat) {
            divsToLoad++
            refresh.isRefreshChat = false
        }
        model.put("divsToLoad", divsToLoad)

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
        modelAndView.addObject("updatingMessage", gameRoomManager.updatingMessage!!)
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
        modelAndView.addObject("cards", game.kingdomCards)
        modelAndView.addObject("prizeCards", game.prizeCards)
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards && game.isIncludePlatinumCards)
        return modelAndView
    }

    @RequestMapping("/showLeaders.html")
    fun showLeaders(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            return ModelAndView("redirect:/login.html")
        }
        val modelAndView = ModelAndView("gameCards")
        modelAndView.addObject("cards", game.availableLeaders)
        modelAndView.addObject("prizeCards", game.prizeCards)
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards && game.isIncludePlatinumCards)
        return modelAndView
    }

    @ResponseBody
    @RequestMapping(value = "/useFruitTokens", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun useFruitTokens(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
        }
        try {
            val player = game.playerMap[user.userId]
            if (player == null) {
                model.put("redirectToLobby", true)
                return model
            }
            game.showUseFruitTokensCardAction(player)
            game.closeLoadingDialog(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return refreshGame(request, response)
    }

    @ResponseBody
    @RequestMapping(value = "/useCattleTokens", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun useCattleTokens(request: HttpServletRequest, response: HttpServletResponse): Map<*, *> {
        val model = HashMap<String, Any>()
        val user = getUser(request)
        val game = getGame(request)
        if (user == null || game == null) {
            model.put("redirectToLogin", true)
            return model
        }
        try {
            val player = game.playerMap[user.userId]
            if (player == null) {
                model.put("redirectToLobby", true)
                return model
            }
            game.showUseCattleTokensCardAction(player)
            game.closeLoadingDialog(player)
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t))
            game.logError(error)
        }

        return refreshGame(request, response)
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
        val selectedCards = ArrayList<String>()
        if (id == "0") {
            set = RecommendedSet()
        } else {
            set = gameManager.getRecommendedSet(Integer.parseInt(id))
        }

        modelAndView.addObject("set", set)
        return modelAndView
    }
}
