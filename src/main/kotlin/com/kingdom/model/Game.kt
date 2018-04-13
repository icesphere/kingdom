package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.BotPlayer
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import com.kingdom.model.players.bots.BigMoneyBotPlayer
import com.kingdom.model.players.bots.EasyBotPlayer
import com.kingdom.model.players.bots.HardBotPlayer
import com.kingdom.model.players.bots.MediumBotPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.KingdomUtil
import com.kingdom.util.toCardNames
import org.apache.commons.lang3.StringUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.createInstance

class Game(private val gameManager: GameManager) {
    val gameId: String = UUID.randomUUID().toString()

    var turn: Int = 0

    var status: GameStatus = GameStatus.None

    var creatorId: Int = 0
    var creatorName = ""

    var title = ""

    var mobile: Boolean = false

    var isPrivateGame = false
    var password = ""

    var players: MutableList<Player> = ArrayList()
    val playerMap: MutableMap<Int, Player> = HashMap(6)
    private val playersExited = HashSet<Int>(6)

    private val computerPlayers: List<Player>
        get() = players.filter { it.isBot }

    var decks: MutableList<Deck> = ArrayList()

    var kingdomCards = mutableListOf<Card>()

    private var twoCostKingdomCards = 0

    val supplyCards = ArrayList<Card>()

    val cardMap = HashMap<String, Card>()

    val pileAmounts = HashMap<String, Int>()

    var blackMarketCards: MutableList<Card> = ArrayList(0)

    var trashedCards: MutableList<Card> = ArrayList()

    private var currentPlayerIndex: Int = 0

    val currentPlayerId
        get() = currentPlayer.userId

    val currentPlayer: Player
        get() = players[currentPlayerIndex]

    //todo maybe get rid of these?
    var costDiscount = 0
    var actionCardDiscount: Int = 0
    var actionCardsInPlay = 0
    private var numActionsCardsPlayed = 0

    private val currentTurnLog = StringBuilder()

    private var quitGamePlayer: Player? = null

    val chats = ArrayList<ChatMessage>()

    val needsRefresh: MutableMap<Int, Refresh> = HashMap(6)

    var gameEndReason = ""
    var winnerString = ""

    private var determinedWinner = false
    private var savedGameHistory = false

    var isAbandonedGame: Boolean = false

    var logId: Int = 0

    private var historyEntriesAddedThisTurn = 0

    var isTestGame: Boolean = false

    private val recentTurnLogs = ArrayList<String>()

    private var timedOut: Boolean = false

    var creationTime = Date()

    var lastActivity = Date()

    var isAnnotatedGame: Boolean = false

    var isRecentGame: Boolean = false

    var isRecommendedSet: Boolean = false

    var isIncludePlatinumCards: Boolean = false

    var isIncludeColonyCards: Boolean = false

    var numPlayers: Int = 0

    var isAllComputerOpponents: Boolean = false
    var numComputerPlayers: Int = 0
    var numEasyComputerPlayers: Int = 0
    var numMediumComputerPlayers: Int = 0
    var numHardComputerPlayers: Int = 0
    var numBMUComputerPlayers: Int = 0

    var custom: Boolean = false

    private var repeated: Boolean = false

    var isPlayTreasureCards = false

    var isShowVictoryPoints: Boolean = false
    var isIdenticalStartingHands: Boolean = false

    var isAlwaysIncludeColonyAndPlatinum: Boolean = false
    var isNeverIncludeColonyAndPlatinum: Boolean = false

    var randomizingOptions: RandomizingOptions? = null

    var isRandomizerReplacementCardNotFound: Boolean = false

    var isShowCoinTokens: Boolean = false
    var isShowGardens: Boolean = false
    var isShowFarmlands: Boolean = false
    var isShowVictoryCoins: Boolean = false
    var isShowVineyard: Boolean = false
    var isShowSilkRoads: Boolean = false
    var isShowCathedral: Boolean = false
    var isShowFairgrounds: Boolean = false
    var isShowGreatHall: Boolean = false
    var isShowHarem: Boolean = false
    var isShowDuke: Boolean = false
    var isShowNobles: Boolean = false
    var isShowArchbishops: Boolean = false
    var isShowDuration: Boolean = false
    var isShowEmbargoTokens: Boolean = false
    var isShowIslandCards: Boolean = false
    var isShowMuseumCards: Boolean = false
    var isShowCityPlannerCards: Boolean = false
    var isShowNativeVillage: Boolean = false
    var isShowPirateShipCoins: Boolean = false
    var isShowHedgeWizard: Boolean = false
    var isShowGoldenTouch: Boolean = false

    val tradeRouteTokenMap = HashMap<String, Boolean>(0)
    var isTrackTradeRouteTokens: Boolean = false
    var tradeRouteTokensOnMat: Int = 0

    val embargoTokens = HashMap<String, Int>()

    val cardsPlayed = LinkedList<Card>()
    val cardsBought = ArrayList<Card>()

    private var currentTurn: PlayerTurn? = null
    private val turnHistory = ArrayList<PlayerTurn>()
    val recentTurnHistory = LinkedList<PlayerTurn>()

    var isShowPrizeCards: Boolean = false
    var prizeCards: MutableList<Card> = ArrayList(0)

    private val colors = ArrayList<String>(6)

    private var currentColorIndex = 0

    private val nextColor: String
        get() {
            val color = colors[currentColorIndex]
            if (currentColorIndex == colors.size - 1) {
                currentColorIndex = 0
            } else {
                currentColorIndex++
            }
            return color
        }

    fun setupGame() {
        currentPlayerIndex = 0

        setupPlayerColors()

        turn = 1

        kingdomCards.sortBy { it.cost }

        lastActivity = Date()

        setupSupply()

        kingdomCards.forEach { cardMap[it.name] = it }
        supplyCards.forEach { cardMap[it.name] = it }

        if (numComputerPlayers > 0) {
            addComputerPlayers()
        }
    }

    private fun setupPlayerColors() {
        colors.add("red")
        colors.add("#001090") //dark blue
        colors.add("green")
        colors.add("#0E80DF") //light blue
        colors.add("purple")
        colors.add("#EF7C00") //dark orange
    }

    private fun setupSupply() {
        setupSupplyAmounts()

        supplyCards.add(Copper())
        supplyCards.add(Silver())
        supplyCards.add(Gold())
        if (isIncludePlatinumCards) {
            supplyCards.add(Platinum())
        }
        supplyCards.add(Estate())
        supplyCards.add(Duchy())
        supplyCards.add(Province())
        if (isIncludeColonyCards) {
            supplyCards.add(Colony())
        }
        supplyCards.add(Curse())
    }

    private fun setupSupplyAmounts() {
        for (card in kingdomCards) {
            var numEachCard = 10
            if (card.isVictory) {
                if (numPlayers == 2) {
                    numEachCard = 8
                } else {
                    numEachCard = 12
                }
            }
            pileAmounts[card.name] = numEachCard
        }
        if (numPlayers > 4) {
            pileAmounts[Copper.NAME] = 120
        } else {
            pileAmounts[Copper.NAME] = 60
        }
        if (numPlayers > 4) {
            pileAmounts[Silver.NAME] = 80
        } else {
            pileAmounts[Silver.NAME] = 40
        }
        if (numPlayers > 4) {
            pileAmounts[Gold.NAME] = 60
        } else {
            pileAmounts[Gold.NAME] = 30
        }
        if (isIncludePlatinumCards) {
            pileAmounts[Platinum.NAME] = 12
        }
        if (numPlayers == 2) {
            pileAmounts[Estate.NAME] = 8
            pileAmounts[Duchy.NAME] = 8
            pileAmounts[Province.NAME] = 8
            if (isIncludeColonyCards) {
                pileAmounts[Colony.NAME] = 8
            }
            pileAmounts[Curse.NAME] = 10
        } else if (numPlayers > 4) {
            pileAmounts[Estate.NAME] = 12
            pileAmounts[Duchy.NAME] = 12
            if (isIncludeColonyCards) {
                pileAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 5) {
                pileAmounts[Province.NAME] = 15
                pileAmounts[Curse.NAME] = 40
            } else {
                pileAmounts[Province.NAME] = 18
                pileAmounts[Curse.NAME] = 50
            }
        } else {
            pileAmounts[Estate.NAME] = 12
            pileAmounts[Duchy.NAME] = 12
            pileAmounts[Province.NAME] = 12
            if (isIncludeColonyCards) {
                pileAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 3) {
                pileAmounts[Curse.NAME] = 20
            } else {
                pileAmounts[Curse.NAME] = 30
            }
        }
    }

    private fun startGame() {
        players.shuffle()
        status = GameStatus.InProgress
        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun startTurnInNewThreadIfComputerVsHuman() {
        if (currentPlayer.isBot && currentPlayer.opponents.any { it is HumanPlayer }) {
            currentPlayer.opponents.filter { it is HumanPlayer }.forEach { p ->
                p.isWaitingForComputer = true
            }
            Thread { currentPlayer.startTurn() }.start()
        } else {
            currentPlayer.startTurn()
        }
    }

    fun getCardsAsString(cards: List<*>): String {
        var cardString = ""
        var first = true
        for (card in cards) {
            if (!first) {
                cardString += ", "
            } else {
                first = false
            }
            cardString += (card as Card).name
        }
        return cardString
    }


    fun turnEnded() {
        addHistory("End of turn $turn")

        if (emptyPiles >= 3
                || pileAmounts[Province.NAME] == 0
                || (isIncludeColonyCards && pileAmounts[Colony.NAME] == 0)) {
            gameOver()
            return
        }

        if (currentPlayerIndex == players.size - 1) {
            currentPlayerIndex = 0
        } else {
            currentPlayerIndex++
        }

        turn++

        recentTurnLogs.add(currentTurnLog.toString())

        currentTurnLog.setLength(0)

        if (recentTurnLogs.size > 1) {
            recentTurnLogs.removeAt(0)
        }

        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun gameOver() {
        addHistory("GAME OVER")
        status = GameStatus.Finished
        determineWinner()
        refreshAllPlayersGameStatus()
        refreshAllPlayersTitle()
    }

    fun trashCardFromSupply(card: Card) {
        removeCardFromSupply(card)
        addHistory("Trashed ${card.name} from supply")
        trashedCards.add(card)
    }

    fun quitGame(player: Player) {
        quitGamePlayer = player
        addHistory("${player.username} quit the game")
        gameOver()
    }

    val recentTurnsLog: String
        get() = currentTurnLog.toString() + StringUtils.join(recentTurnLogs, "")


    fun isCardAvailableInSupply(card: Card): Boolean {
        return pileAmounts.containsKey(card.name) && pileAmounts[card.name]!! > 0
    }

    fun removeCardFromSupply(card: Card) {
        pileAmounts[card.name] = pileAmounts[card.name]!!.minus(1)
    }

    val nonEmptyPiles
        get() = cardMap.keys.filter { pileAmounts[it]!! > 0 }

    val emptyPiles
        get() = cardMap.size - nonEmptyPiles.size

    val availableCards
        get() = cardMap.values.filter { isCardAvailableInSupply(it) }

    fun addGameChat(message: String) {
        chats.add(ChatMessage(message, "black"))
        refreshAllPlayersChat()
    }

    fun addChat(player: Player, message: String) {
        updateLastActivity()
        chats.add(ChatMessage(player.username + ": " + message, player.chatColor))
        refreshAllPlayersChat()
    }

    fun addPrivateChat(sender: User, receiver: User, message: String) {
        updateLastActivity()
        chats.add(ChatMessage("Private chat from " + sender.username + ": " + message, "black", receiver.userId))
        refreshChat(receiver.userId)
    }

    fun saveGameHistory() {
        if (!savedGameHistory) {
            savedGameHistory = true
            val history = GameHistory()
            history.startDate = creationTime
            history.endDate = Date()
            history.numPlayers = numPlayers
            history.numComputerPlayers = numComputerPlayers
            history.custom = custom
            history.annotatedGame = isAnnotatedGame
            history.recentGame = isRecentGame
            history.recommendedSet = isRecommendedSet
            history.testGame = isTestGame
            history.abandonedGame = isAbandonedGame
            history.gameEndReason = gameEndReason
            history.winner = winnerString
            history.showVictoryPoints = isShowVictoryPoints
            history.identicalStartingHands = isIdenticalStartingHands
            history.repeated = repeated
            history.mobile = mobile
            val cardNames = java.util.ArrayList<String>()
            for (kingdomCard in kingdomCards) {
                cardNames.add(kingdomCard.name)
            }
            if (isIncludePlatinumCards) {
                cardNames.add("Platinum")
            }
            if (isIncludeColonyCards) {
                cardNames.add("Colony")
            }
            history.cards = KingdomUtil.implode(cardNames, ",")
            gameManager.saveGameHistory(history)

            val sb = StringBuilder()
            for (playerTurn in turnHistory) {
                sb.append(KingdomUtil.implode(playerTurn.history, ";"))
            }

            val log = GameLog()
            log.gameId = history.gameId
            log.log = sb.toString()
            gameManager.saveGameLog(log)
            logId = log.logId

            for (player in players) {
                gameManager.saveGameUserHistory(history.gameId, player)
            }
        }
    }

    fun reset(repeatingGame: Boolean = false) {
        if (!repeatingGame) {
            status = GameStatus.None
            for (player in players) {
                LoggedInUsers.gameReset(player.userId)
            }
            LoggedInUsers.refreshLobbyPlayers()
            numPlayers = 0
            numComputerPlayers = 0
            numEasyComputerPlayers = 0
            numMediumComputerPlayers = 0
            numHardComputerPlayers = 0
            numBMUComputerPlayers = 0
            isAllComputerOpponents = false
            isPlayTreasureCards = false
            isIncludePlatinumCards = false
            isIncludeColonyCards = false
            supplyCards.clear()
            kingdomCards.clear()
            cardMap.clear()
            blackMarketCards.clear()
            isShowDuke = false
            isShowGardens = false
            isShowFarmlands = false
            isShowVictoryCoins = false
            isShowVineyard = false
            isShowSilkRoads = false
            isShowCathedral = false
            isShowFairgrounds = false
            isShowGreatHall = false
            isShowHarem = false
            isShowNobles = false
            isShowArchbishops = false
            isShowDuration = false
            isShowEmbargoTokens = false
            isShowIslandCards = false
            isShowMuseumCards = false
            isShowCityPlannerCards = false
            isShowNativeVillage = false
            isShowPirateShipCoins = false
            isTrackTradeRouteTokens = false
            isAlwaysIncludeColonyAndPlatinum = false
            isNeverIncludeColonyAndPlatinum = false
            isAnnotatedGame = false
            isRecommendedSet = false
            isTestGame = false
            isShowPrizeCards = false
            isShowHedgeWizard = false
            isShowGoldenTouch = false
            isIdenticalStartingHands = false
            creatorId = 0
            creatorName = ""
            title = ""
            isPrivateGame = false
            password = ""
            twoCostKingdomCards = 0
            custom = false
            mobile = false
        }
        players.clear()
        playerMap.clear()
        pileAmounts.clear()
        embargoTokens.clear()
        trashedCards.clear()
        cardsPlayed.clear()
        cardsBought.clear()
        needsRefresh.clear()
        recentTurnHistory.clear()
        turnHistory.clear()
        chats.clear()
        currentPlayerIndex = 0
        currentColorIndex = 0
        playersExited.clear()
        costDiscount = 0
        numActionsCardsPlayed = 0
        actionCardsInPlay = 0
        actionCardDiscount = 0
        tradeRouteTokenMap.clear()
        tradeRouteTokensOnMat = 0
        determinedWinner = false
        savedGameHistory = false
        gameEndReason = ""
        winnerString = ""
        historyEntriesAddedThisTurn = 0
        logId = 0
        prizeCards.clear()
        isAbandonedGame = false
        isRecentGame = false
        isRandomizerReplacementCardNotFound = false
        LoggedInUsers.refreshLobbyGameRooms()
    }

    fun logError(error: GameError, showInChat: Boolean = true) {
        val cardNames = kingdomCards.map { it.name }.toMutableList()

        if (isIncludePlatinumCards) {
            cardNames.add("Platinum")
        }

        if (isIncludeColonyCards) {
            cardNames.add("Colony")
        }

        val kingdomCardsString = KingdomUtil.implode(cardNames, ",")
        val playerNames = players.map { it.username }

        val errorHistory = StringBuilder()
        if (playerNames.isNotEmpty()) {
            errorHistory.append("Players: ").append(KingdomUtil.implode(playerNames, ",")).append("; ")
        }
        if (kingdomCards.isNotEmpty()) {
            errorHistory.append("Kingdom Cards: ").append(kingdomCardsString).append("; ")
        }

        if (players.isNotEmpty()) {
            errorHistory.append("Current Player Hand: ").append(currentPlayer.hand.toCardNames(false)).append("; ")
        }

        if (currentTurn != null) {
            errorHistory.append(KingdomUtil.implode(currentTurn!!.history, ";"))
        }

        error.history = errorHistory.toString()

        gameManager.logError(error)

        if (showInChat) {
            if (error.computerError) {
                addGameChat("The computer encountered an error. This error has been reported and will be fixed as soon as possible. If you would like to keep playing you can quit this game and start a new one with different cards.")
            } else {
                addGameChat("The game encountered an error. Try refreshing the page.")
            }
        }
    }

    val prizeCardsString: String
        get() = if (prizeCards.isEmpty()) {
            "None"
        } else {
            prizeCards.toCardNames()
        }


    fun refreshAll(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshGameStatus = true
        if (player.currentAction != null) {
            refresh.isRefreshCardAction = true
        }
        refresh.isRefreshHandArea = true
        refresh.isRefreshPlayers = true
        refresh.isRefreshPlayingArea = true
        refresh.isRefreshSupply = true
    }

    fun refreshAllPlayersPlayingArea() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshPlayingArea = true
        }
    }

    fun refreshAllPlayersCardsPlayed() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshCardsPlayedDiv = true
        }
    }

    fun refreshAllPlayersCardsBought() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshCardsBoughtDiv = true
        }
    }

    private fun refreshAllPlayersHistory() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshHistory = true
        }
    }

    fun refreshAllPlayersSupply() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshSupply = true
        }
    }

    fun refreshAllPlayersHand() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshHand = true
        }
    }

    fun refreshAllPlayersHandArea() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshHandArea = true
        }
    }

    fun refreshAllPlayersDiscard() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshDiscard = true
        }
    }

    fun refreshAllPlayersPlayers() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshPlayers = true
        }
    }

    private fun refreshAllPlayersGameStatus() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshGameStatus = true
        }
    }

    fun refreshEndTurn(currentPlayerId: Int) {
        for (userId in needsRefresh.keys) {
            if (userId != currentPlayerId) {
                val refresh = needsRefresh[userId]!!
                refresh.isRefreshEndTurn = true
                val refreshHandArea = refresh.isRefreshHand || refresh.isRefreshHandArea || refresh.isRefreshDiscard
                if (refreshHandArea) {
                    refresh.isRefreshHandOnEndTurn = true
                }
                if (refresh.isRefreshSupply) {
                    refresh.isRefreshSupply = true
                }
            }
        }
    }

    private fun refreshChat(userId: Int) {
        val refresh = needsRefresh[userId]!!
        refresh.isRefreshChat = true
    }

    private fun refreshAllPlayersChat() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshChat = true
        }
    }

    private fun refreshAllPlayersTitle() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshTitle = true
        }
    }

    fun refreshPlayingArea(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshPlayingArea = true
    }

    fun refreshCardsBought(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshCardsBoughtDiv = true
    }

    fun refreshSupply(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshSupply = true
    }

    fun refreshHand(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshHand = true
    }

    fun refreshHandArea(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshHandArea = true
    }

    fun refreshDiscard(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshDiscard = true
    }

    fun refreshCardAction(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshCardAction = true
    }

    fun refreshInfoDialog(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshInfoDialog = true
    }

    fun closeLoadingDialog(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isCloseLoadingDialog = true
    }

    fun repeat() {
        val playersCopy = ArrayList(players)
        val computerPlayersCopy = ArrayList(computerPlayers)
        reset(true)
        repeated = true
        setupSupply()
        creationTime = Date()
        updateLastActivity()
        for (player in playersCopy) {
            if (player.isBot) {
                val computerPlayer = computerPlayersCopy[player.userId]!!
                addPlayer(player.user, true, computerPlayer is BigMoneyBotPlayer, (computerPlayer as BotPlayer).difficulty)
            } else {
                addPlayer(player.user)
            }
        }
        playersCopy.clear()
        computerPlayersCopy.clear()

        startGame()
    }

    fun getSupplyCard(cardName: String): Card {
        val card = cardMap[cardName]!!
        return card.javaClass.kotlin.createInstance()
    }

    private fun addComputerPlayers() {
        isAllComputerOpponents = numComputerPlayers == numPlayers - 1
        var i = 1
        run {
            var j = 1
            while (j <= numEasyComputerPlayers) {
                addComputerPlayer(i, false, 1)
                j++
                i++
            }
        }
        run {
            var j = 1
            while (j <= numMediumComputerPlayers) {
                addComputerPlayer(i, false, 2)
                j++
                i++
            }
        }
        run {
            var j = 1
            while (j <= numHardComputerPlayers) {
                addComputerPlayer(i, false, 3)
                j++
                i++
            }
        }
        var j = 1
        while (j <= numBMUComputerPlayers) {
            addComputerPlayer(i, true, 3)
            j++
            i++
        }
    }

    private fun addComputerPlayer(i: Int, bigMoneyUltimate: Boolean, difficulty: Int) {
        val userId = i * -1
        val user = User()
        user.gender = User.COMPUTER

        when {
            bigMoneyUltimate -> {
                user.userId = userId - 40
                user.username = "C$i (BMU)"
                addPlayer(user, true, true, 3)
            }
            difficulty == 1 -> {
                user.userId = userId - 10
                user.username = "C$i (easy)"
                addPlayer(user, true, false, 1)
            }
            difficulty == 2 -> {
                user.userId = userId - 20
                user.username = "C$i (medium)"
                addPlayer(user, true, false, 2)
            }
            difficulty == 3 -> {
                user.userId = userId - 30
                user.username = "C$i (hard)"
                addPlayer(user, true, false, 3)
            }
        }
    }

    fun addPlayer(user: User, computer: Boolean = false, bigMoneyUltimate: Boolean = false, difficulty: Int = 0) {
        val player: Player = if (computer) {
            when {
                bigMoneyUltimate -> BigMoneyBotPlayer(user, this)
                difficulty == 1 -> EasyBotPlayer(user, this)
                difficulty == 2 -> MediumBotPlayer(user, this)
                else -> HardBotPlayer(user, this)
            }
        } else {
            HumanPlayer(user, this)
        }
        player.chatColor = nextColor
        players.add(player)
        playerMap[player.userId] = player
        needsRefresh[player.userId] = Refresh()

        if (!repeated && players.size == numPlayers) {
            startGame()
        }
    }

    fun removePlayer(user: User) {
        val player = playerMap[user.userId]!!
        players.remove(player)
        playerMap.remove(player.userId)
        needsRefresh.remove(player.userId)
        if (player.userId == creatorId) {
            if (players.isEmpty()) {
                creatorId = 0
            } else {
                creatorId = players[0].userId
            }
        }
        if (players.isEmpty()) {
            reset()
        }
    }

    private fun updateLastActivity() {
        lastActivity = Date()
    }

    fun playerExitedGame(player: Player) {
        updateLastActivity()
        if (!player.isBot) {
            addGameChat(player.username + " exited the game")
        }
        playersExited.add(player.userId)
        if (playersExited.size == players.size) {
            reset()
        }
    }

    fun playerQuitGame(player: Player) {
        updateLastActivity()
        player.isQuit = true
        gameEndReason = player.username + " quit the game"
        winnerString = ""
        addGameChat(gameEndReason)
        gameOver()
    }

    @Synchronized
    private fun determineWinner() {
        if (!determinedWinner) {
            determinedWinner = true
            players.sortByDescending { it.getVictoryPoints(true) }
            val firstPlayer = players[0]
            val highScore = firstPlayer.finalVictoryPoints
            val leastTurns = firstPlayer.turns
            val marginOfVictory = players[0].finalVictoryPoints - players[1].finalVictoryPoints
            val winners = ArrayList<String>()
            for (player in players) {
                if (player.finalVictoryPoints == highScore && leastTurns == player.turns) {
                    player.isWinner = true
                    player.marginOfVictory = marginOfVictory
                    winners.add(player.username)
                } else {
                    break
                }
            }

            if (winners.size == 1) {
                winnerString = winners[0] + " wins!"
            } else {
                val sb = StringBuilder()
                for (i in winners.indices) {
                    if (i != 0) {
                        sb.append(", ")
                    }
                    if (i == winners.size - 1) {
                        sb.append("and ")
                    }
                    sb.append(winners[i])
                }
                winnerString = sb.toString() + " tie for the win!"
            }

            saveGameHistory()

            for (computerPlayer in computerPlayers) {
                playerExitedGame(computerPlayer)
            }
        }
    }

    fun addHistory(history: String) {
        currentTurn?.addHistory(history)
        historyEntriesAddedThisTurn++
        refreshAllPlayersHistory()
    }
}
