package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import com.kingdom.util.KingdomUtil
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException
import java.util.*

class Game() {
    val gameId: String = UUID.randomUUID().toString()

    var turn: Int = 0

    var status: GameStatus = GameStatus.None

    var creatorId: Int = 0
    var creatorName = ""

    var title = ""

    var mobile: Boolean = false

    var isPrivateGame = false
    var password = ""

    lateinit var players: List<Player>
    val playerMap: MutableMap<Int, Player> = HashMap(6)

    var decks: MutableList<Deck> = ArrayList()

    var kingdomCards: MutableList<Card> = ArrayList()

    val supplyCards = ArrayList<Card>()
    val supplyAmounts = HashMap<String, Int>()

    var blackMarketCards: MutableList<Card> = ArrayList(0)

    var trashedCards: MutableList<Card> = ArrayList()

    var currentPlayerIndex: Int = 0
    var currentPlayerId = -1

    //todo maybe get rid of these?
    var costDiscount = 0
    var actionCardDiscount: Int = 0
    var actionCardsInPlay = 0
    var numActionsCardsPlayed = 0

    var isGameOver: Boolean = false
        private set

    var isCreateGameLog = true

    var gameLog = StringBuilder()

    private val currentTurnLog = StringBuilder()

    var quitGamePlayer: Player? = null
        private set

    val chats = ArrayList<ChatMessage>()

    val needsRefresh: MutableMap<Int, Refresh> = HashMap(6)

    var gameEndReason = ""
    var winnerString = ""

    var isAbandonedGame: Boolean = false

    val showGameLog = false
    var logId: Int = 0

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
    var isShowSins: Boolean = false
    
    val tradeRouteTokenMap = HashMap<String, Boolean>(0)
    var isTrackTradeRouteTokens: Boolean = false
    var tradeRouteTokensOnMat: Int = 0
    
    val embargoTokens = HashMap<String, Int>()

    val cardsPlayed = LinkedList<Card>()
    val cardsBought = ArrayList<Card>()

    val recentTurnHistory = LinkedList<PlayerTurn>()

    var isShowPrizeCards: Boolean = false
    var prizeCards: MutableList<Card> = ArrayList(0)


    fun setupGame() {
        currentPlayerIndex = 0

        turn = 1

        kingdomCards.sortBy { it.cost }

        lastActivity = Date()

        setupSupply()
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
            supplyAmounts[card.name] = numEachCard
        }
        if (numPlayers > 4) {
            supplyAmounts[Copper.NAME] = 120
        } else {
            supplyAmounts[Copper.NAME] = 60
        }
        if (numPlayers > 4) {
            supplyAmounts[Silver.NAME] = 80
        } else {
            supplyAmounts[Silver.NAME] = 40
        }
        if (numPlayers > 4) {
            supplyAmounts[Gold.NAME] = 60
        } else {
            supplyAmounts[Gold.NAME] = 30
        }
        if (isIncludePlatinumCards) {
            supplyAmounts[Platinum.NAME] = 12
        }
        if (numPlayers == 2) {
            supplyAmounts[Estate.NAME] = 8
            supplyAmounts[Duchy.NAME] = 8
            supplyAmounts[Province.NAME] = 8
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 8
            }
            supplyAmounts[Curse.NAME] = 10
        } else if (numPlayers > 4) {
            supplyAmounts[Estate.NAME] = 12
            supplyAmounts[Duchy.NAME] = 12
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 5) {
                supplyAmounts[Province.NAME] = 15
                supplyAmounts[Curse.NAME] = 40
            } else {
                supplyAmounts[Province.NAME] = 18
                supplyAmounts[Curse.NAME] = 50
            }
        } else {
            supplyAmounts[Estate.NAME] = 12
            supplyAmounts[Duchy.NAME] = 12
            supplyAmounts[Province.NAME] = 12
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 3) {
                supplyAmounts[Curse.NAME] = 20
            } else {
                supplyAmounts[Curse.NAME] = 30
            }
        }
    }

    fun startGame() {
        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun startTurnInNewThreadIfComputerVsHuman() {
        if (currentPlayer.isBot && currentPlayer.opponents.any{it is HumanPlayer}) {
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
        gameLog("End of turn " + turn)

        for (player in players!!) {
            //todo calculate if game over
            if (false) {
                gameOver()
                return
            }
        }

        if (currentPlayerIndex == players!!.size - 1) {
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

        if (!isGameOver) {
            startTurnInNewThreadIfComputerVsHuman()
        }
    }

    private fun gameOver() {
        isGameOver = true
        gameLog("-----------------------------")
        gameLog("Game over")
        gameLog("Turns: " + turn)
        for (player in players!!) {
            player.isWaitingForComputer = false
            val playerName = player.playerName
            //todo show score
        }
        for (player in players!!) {
            gameLog("----")
            val playerName = player.playerName
            gameLog(playerName + "'s cards: ")
            player.allCards.forEach { c -> gameLog(c.name) }
        }

        writeGameLog()
    }

    val gameLogFile: File?
        get() {
            //todo
            return null
            /*val userDirectory = FileUtils.getUserDirectory()
            val gameLogDirectory = File(userDirectory, "kingdomgamelogs")

            var gameLogFileName = "game_"

            if (quitGamePlayer != null) {
                gameLogFileName += quitGamePlayer!!.playerName + "_quit_"
            }

            if (timedOut) {
                gameLogFileName += "timeout_"
            }

            gameLogFileName += winner.infoForGameLogName + "_over_" + loser.infoForGameLogName + "_" + gameId

            return File(gameLogDirectory, gameLogFileName)*/
        }

    private fun writeGameLog() {
        try {
            //todo
            /*val gameLogFile = gameLogFile
            //gameLog.append("Game log file: ").append(gameLogFile.getAbsolutePath());
            FileUtils.writeStringToFile(gameLogFile,
                    gameLog.toString().replace("<br/>".toRegex(), "\n").replace("<b>".toRegex(), "").replace("</b>".toRegex(), ""),
                    "UTF-8")*/
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //todo
    /*val winner: Player
        get() {
            return players!!.stream().max { p1, p2 -> Integer.compare(p1.authority, p2.authority) }.get()
        }

    val loser: Player
        get() {
            return players!!.stream().min { p1, p2 -> Integer.compare(p1.authority, p2.authority) }.get()
        }*/

    fun gameLog(log: String) {
        gameLog(log, false)
    }

    private fun gameLog(log: String, simulationInfo: Boolean) {
        if (showGameLog) {
            println(log)
        }
        if (isCreateGameLog) {
            gameLog.append(log).append("<br/>")
            currentTurnLog.append(log).append("<br/>")
        }
    }

    val currentPlayer: Player
        get() = players!![currentPlayerIndex]

    fun trashCardFromSupply(card: Card) {
        //todo
        gameLog("Trashed " + card.name + " from supply")
        trashedCards.add(card)
    }

    fun quitGame(player: Player) {
        quitGamePlayer = player
        gameLog(player.playerName + " quit the game")
        gameOver()
    }

    val recentTurnsLog: String
        get() = currentTurnLog.toString() + StringUtils.join(recentTurnLogs, "")

    fun gameTimedOut() {
        gameLog("Game timed out")
        timedOut = true
        writeGameLog()
    }

    fun isCardAvailableInSupply(card: Card): Boolean {
        return supplyAmounts.containsKey(card.name) && supplyAmounts[card.name]!! > 0
    }

    fun removeCardFromSupply(card: Card) {
        //todo
    }

    val nonEmptySupplyCards
        get() = supplyCards.filter { supplyAmounts[it.name]!! > 0 }

    val emptySupplyPiles
        get() = supplyCards.size - nonEmptySupplyCards.size

    fun addGameChat(message: String) {
        chats.add(ChatMessage(message, "black"))
        refreshAllPlayersChat()
    }

    fun saveGameHistory() {
        //todo
    }

    fun reset() {
        //todo
    }

    fun logError(error: GameError) {
        //todo
    }

    val prizeCardsString: String
        get() = if (prizeCards.isEmpty()) {
            "None"
        } else {
            KingdomUtil.getCardNames(prizeCards)
        }


    fun refreshAll(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshGameStatus = true
        //todo
        /*if (player.isShowCardAction && player.oldCardAction != null) {
            refresh.isRefreshCardAction = true
        }*/
        refresh.isRefreshHandArea = true
        refresh.isRefreshPlayers = true
        refresh.isRefreshPlayingArea = true
        refresh.isRefreshSupply = true

        //todo
        /*if (status == GameStatus.InProgress && !hasIncompleteCard() && !currentPlayer!!.isShowCardAction) {
            if (!repeatedActions.isEmpty()) {
                playRepeatedAction(currentPlayer!!, false)
            } else if (!golemActions.isEmpty()) {
                playGolemActionCard(currentPlayer)
            }
        }*/
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

    fun refreshAllPlayersHistory() {
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

    fun refreshAllPlayersGameStatus() {
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

    fun refreshAllPlayersChat() {
        for (refresh in needsRefresh.values) {
            refresh.isRefreshChat = true
        }
    }

    fun refreshAllPlayersTitle() {
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
        //todo
    }

    fun getSupplyCard(cardName: String): Card {
        //todo
        return Copper()
    }
}
