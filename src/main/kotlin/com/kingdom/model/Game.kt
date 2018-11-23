package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.darkages.Spoils
import com.kingdom.model.cards.darkages.ruins.*
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.modifiers.CardCostModifierForCardsInPlay
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.BotPlayer
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import com.kingdom.model.players.bots.BigMoneyBotPlayer
import com.kingdom.model.players.bots.EasyBotPlayer
import com.kingdom.model.players.bots.HardBotPlayer
import com.kingdom.model.players.bots.MediumBotPlayer
import com.kingdom.service.GameManager
import com.kingdom.service.GameMessageService
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.KingdomUtil
import com.kingdom.util.toCardNames
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.createInstance

class Game(private val gameManager: GameManager, private val gameMessageService: GameMessageService) {
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

    val humanPlayers: List<Player>
        get() = players.filterNot { it.isBot }

    val playerMap: MutableMap<Int, Player> = HashMap(6)
    private val playersExited = HashSet<Int>(6)

    private val computerPlayers: List<Player>
        get() = players.filter { it.isBot }

    var decks: MutableList<Deck> = ArrayList()

    var kingdomCards = mutableListOf<Card>()

    private var twoCostKingdomCards = 0

    private val supplyCards = ArrayList<Card>()

    val cardsInSupply: List<Card>
        get() {
            return when {
                isIncludeRuins -> when {
                    ruinsPile.isEmpty() -> supplyCards + listOf(RuinsPlaceholder())
                    else -> supplyCards + listOf(ruinsPile.first())
                }
                else -> supplyCards
            }
        }

    private val cardMap = HashMap<String, Card>()

    val allCards: List<Card>
        get() = cardsInSupply + kingdomCards

    val copyOfAllCards: List<Card>
        get() = allCards.map { getNewInstanceOfCard(it.name) }

    private val pileAmounts = HashMap<String, Int>()

    val numInPileMap: Map<String, Int>
        get() {
            val amounts = pileAmounts.toMutableMap()

            if (isIncludeRuins) {
                if (ruinsPile.isEmpty()) {
                    amounts[RuinsPlaceholder.NAME] = 0
                } else {
                    amounts[ruinsPile.first().name] = ruinsPile.size
                }
            }

            return amounts
        }

    var cardsNotInSupply: MutableList<Card> = ArrayList()

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

    var gameEndReason = ""
    var winnerString = ""

    private var determinedWinner = false
    private var savedGameHistory = false

    var isAbandonedGame: Boolean = false

    var logId: Int = 0

    private var historyEntriesAddedThisTurn = 0

    var isTestGame: Boolean = false

    private val recentTurnLogs = ArrayList<String>()

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

    var isIncludeShelters: Boolean = false
    var isExcludeShelters: Boolean = false

    var isIncludeSpoils: Boolean = false

    var isIncludeRuins: Boolean = false
    var ruinsPile: MutableList<Card> = ArrayList()

    var randomizingOptions: RandomizingOptions? = null

    var isRandomizerReplacementCardNotFound: Boolean = false

    var isShowCoinTokens: Boolean = false

    var isShowEmbargoTokens: Boolean = false

    var isShowDuration: Boolean = false

    var isShowIslandCards: Boolean = false

    val victoryCards: List<Card>
        get() = allCards
                .filter { it.isVictory }
                .sortedByDescending { it.cost }

    var isShowVictoryCoins: Boolean = false
    var isShowNativeVillage: Boolean = false
    var isShowPirateShipCoins: Boolean = false

    val tradeRouteTokenMap = HashMap<String, Boolean>(0)
    var isTrackTradeRouteTokens: Boolean = false
    var tradeRouteTokensOnMat: Int = 0

    val embargoTokens = HashMap<String, Int>()

    val cardsPlayed = LinkedList<Card>()
    val cardsBought = ArrayList<Card>()

    val currentPlayerCardCostModifiers = mutableListOf<CardCostModifier>()

    val gameCardCostModifiers = mutableListOf<CardCostModifier>()

    val cardCostModifiers: List<CardCostModifier>
        get() {
            val modifiers: MutableList<CardCostModifier> = (currentPlayerCardCostModifiers + gameCardCostModifiers).toMutableList()
            currentPlayer.inPlay.filter { it is CardCostModifierForCardsInPlay }.forEach { modifiers.add(it as CardCostModifier) }
            return modifiers
        }

    var previousPlayerId = 0
    val previousPlayerCardsPlayed = ArrayList<Card>()
    val previousPlayerCardsBought = ArrayList<Card>()
    val previousPlayer: Player?
        get() = playerMap[previousPlayerId]

    private var currentTurn: PlayerTurn? = null
    private val turnHistory = ArrayList<PlayerTurn>()
    val recentTurnHistory = LinkedList<PlayerTurn>()
    private var maxHistoryTurnSize: Int = 0

    var isShowPrizeCards: Boolean = false
    var prizeCards: MutableList<Card> = ArrayList(0)

    fun getPlayerToLeft(player: Player): Player {
        val playerIndex = players.indexOf(player)

        val nextPlayerIndex = if (playerIndex == players.size - 1) {
            0
        } else {
            playerIndex + 1
        }

        return players[nextPlayerIndex]
    }

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

        kingdomCards.forEach {
            cardMap[it.name] = it

            if (it is GameSetupModifier) {
                it.modifyGameSetup(this)
            }

            if (it.isDuration) {
                isShowDuration = true
            }

            if (it.isLooter) {
                isIncludeRuins = true
            }
        }

        setupSupply()

        if (isIncludeSpoils) {
            cardsNotInSupply.add(Spoils())
            pileAmounts[Spoils.NAME] = 15
        }

        cardsInSupply.forEach { cardMap[it.name] = it }

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

        if (isIncludeRuins) {
            createRuinsPile()
        }
    }

    private fun createRuinsPile() {
        val ruinsCards = mutableListOf<Card>()

        repeat(10) {
            ruinsCards.add(AbandonedMine())
        }
        repeat(10) {
            ruinsCards.add(RuinedLibrary())
        }
        repeat(10) {
            ruinsCards.add(RuinedMarket())
        }
        repeat(10) {
            ruinsCards.add(RuinedVillage())
        }
        repeat(10) {
            ruinsCards.add(Survivors())
        }

        ruinsCards.shuffle()

        ruinsPile = ruinsCards.subList(0, 10 * (numPlayers - 1))
    }

    private fun startGame() {
        players.shuffle()
        status = GameStatus.InProgress

        maxHistoryTurnSize = players.size + 1

        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun startTurnInNewThreadIfComputerVsHuman(refreshPreviousPlayerCardsBought: Boolean = false) {
        if (recentTurnHistory.size == maxHistoryTurnSize) {
            recentTurnHistory.removeFirst()
        }
        if (currentTurn != null) {
            currentTurn!!.addHistory("")
        }
        currentTurn = PlayerTurn(currentPlayer)
        recentTurnHistory.add(currentTurn!!)
        turnHistory.add(currentTurn!!)

        refreshHistory()

        if (currentPlayer.isBot && currentPlayer.opponents.any { it is HumanPlayer }) {
            currentPlayer.opponents.filter { it is HumanPlayer }.forEach { p ->
                p.isWaitingForComputer = true
            }

            Thread { currentPlayer.startTurn(refreshPreviousPlayerCardsBought) }.start()
        } else {
            currentPlayer.startTurn(refreshPreviousPlayerCardsBought)
        }
    }

    fun refreshGame() {
        gameMessageService.refreshGame(this)
    }

    fun refreshPlayerGame(player: Player) {
        gameMessageService.refreshGame(player)
    }

    fun refreshSupply() {
        gameMessageService.refreshSupply(this)
    }

    fun refreshPlayerSupply(player: Player) {
        gameMessageService.refreshSupply(player)
    }

    fun refreshCardsBought() {
        gameMessageService.refreshCardsBought(this)
    }

    fun refreshPlayerCardsBought(player: Player) {
        gameMessageService.refreshCardsBought(player)
    }

    fun refreshPreviousPlayerCardsBought(player: Player) {
        gameMessageService.refreshPreviousPlayerCardsBought(player)
    }

    fun refreshCardsPlayed() {
        gameMessageService.refreshCardsPlayed(this)
    }

    fun refreshPlayerCardsPlayed(player: Player) {
        gameMessageService.refreshCardsPlayed(player)
    }

    fun refreshHistory() {
        gameMessageService.refreshHistory(this)
    }

    fun refreshPlayerCardAction(player: Player) {
        gameMessageService.refreshCardAction(player)
    }

    fun refreshPlayerHandArea(player: Player) {
        gameMessageService.refreshHandArea(player)
    }

    fun refreshChat() {
        gameMessageService.refreshChat(this)
    }

    fun showInfoMessage(player: Player, message: String) {
        gameMessageService.showInfoMessage(player, message)
    }

    fun turnEnded(isAutoEnd: Boolean) {
        addHistory("End of turn $turn")

        if (emptyPiles >= 3
                || pileAmounts[Province.NAME] == 0
                || (isIncludeColonyCards && pileAmounts[Colony.NAME] == 0)) {
            gameOver()
            return
        }

        previousPlayerCardsPlayed.clear()
        previousPlayerCardsBought.clear()
        previousPlayerCardsPlayed.addAll(cardsPlayed)
        previousPlayerCardsBought.addAll(cardsBought)

        cardsPlayed.clear()
        cardsBought.clear()

        currentPlayerCardCostModifiers.clear()

        previousPlayerId = currentPlayerId

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

        startTurnInNewThreadIfComputerVsHuman(isAutoEnd || previousPlayer!!.isBot)
    }

    private fun gameOver() {
        addHistory("GAME OVER")
        status = GameStatus.Finished
        determineWinner()
        refreshGame()
    }

    fun trashCardFromSupply(card: Card) {
        removeCardFromSupply(card)
        addHistory("Trashed ${card.cardNameWithBackgroundColor} from supply")
        trashedCards.add(card)
    }

    fun isCardAvailableInSupply(card: Card): Boolean {
        return numInPileMap.containsKey(card.name) && numInPileMap[card.name]!! > 0
    }

    fun removeCardFromSupply(card: Card, refreshSupply: Boolean = true) {
        if (card.isRuins) {
            ruinsPile.removeAt(0)
        } else {
            pileAmounts[card.name] = pileAmounts[card.name]!!.minus(1)
        }

        if (refreshSupply) {
            refreshSupply()
        }
    }

    fun returnCardToSupply(card: Card) {
        if (card.isRuins) {
            ruinsPile.add(0, card)
        } else {
            pileAmounts[card.name] = pileAmounts[card.name]!!.plus(1)
        }
        refreshSupply()
    }

    private val nonEmptyPiles: Int
        get() = allCards.filter { numInPileMap[it.name]!! > 0 }.size

    val emptyPiles
        get() = allCards.size - nonEmptyPiles

    val availableCards
        get() = allCards.filter { isCardAvailableInSupply(it) }

    fun addGameChat(message: String) {
        chats.add(ChatMessage(message, "black"))
        players.forEach { showInfoMessage(it, message) }
        refreshChat()
    }

    fun addChat(player: Player, message: String) {
        updateLastActivity()
        chats.add(ChatMessage(player.username + ": " + message, player.chatColor))
        players.filterNot { it.userId == player.userId }.forEach { showInfoMessage(it, "Chat from ${player.username}: $message") }
        refreshChat()
    }

    fun addPrivateChat(sender: User, receiver: User, message: String) {
        updateLastActivity()
        chats.add(ChatMessage("Private chat from " + sender.username + ": " + message, "black", receiver.userId))
        gameMessageService.showInfoMessageForUserId(receiver.userId, "Private chat from ${sender.username}: $message")
        refreshChat()
    }

    fun saveGameHistory() {
        if (!savedGameHistory) {
            savedGameHistory = true
            val history = GameHistory()
            history.gameId = gameId
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

    fun reset() {
        status = GameStatus.None
        for (player in players) {
            LoggedInUsers.gameReset(player.userId)
        }
        LoggedInUsers.refreshLobbyPlayers()
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

    fun getNewInstanceOfCard(cardName: String): Card {
        if (isIncludeRuins && ruinsPile.firstOrNull()?.name == cardName) {
            return ruinsPile.first().javaClass.kotlin.createInstance()
        }
        if (cardsNotInSupply.any { it.name == cardName }) {
            return cardsNotInSupply.first { it.name == cardName }.javaClass.kotlin.createInstance()
        }
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

        if (players.size == numPlayers) {
            startGame()
        }
    }

    fun removePlayer(user: User) {
        val player = playerMap[user.userId]!!
        players.remove(player)
        playerMap.remove(player.userId)
        if (player.userId == creatorId) {
            creatorId = if (players.isEmpty()) {
                0
            } else {
                players[0].userId
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
        quitGamePlayer = player
        gameEndReason = "${player.username} quit the game"
        addHistory(gameEndReason)
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
        refreshHistory()
    }

    fun setupAmountForPile(cardName: String, amount: Int) {
        pileAmounts[cardName] = amount
    }
}
