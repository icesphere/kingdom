package com.kingdom.model

import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.ArtifactAction
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.adventures.InheritanceEstate
import com.kingdom.model.cards.darkages.Spoils
import com.kingdom.model.cards.darkages.ruins.*
import com.kingdom.model.cards.darkages.shelters.Hovel
import com.kingdom.model.cards.darkages.shelters.Necropolis
import com.kingdom.model.cards.darkages.shelters.OvergrownEstate
import com.kingdom.model.cards.listeners.GameStartedListener
import com.kingdom.model.cards.menagerie.Horse
import com.kingdom.model.cards.menagerie.UsesExileMat
import com.kingdom.model.cards.menagerie.UsesHorses
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.modifiers.CardCostModifierForCardsInPlay
import com.kingdom.model.cards.supply.*
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Game(private val gameManager: GameManager, private val gameMessageService: GameMessageService) {

    val gameId: String = UUID.randomUUID().toString()

    var startTime: String? = null

    var turn: Int = 0

    var status: GameStatus = GameStatus.None

    var creatorId: String? = null
    var creatorName = ""

    var title = ""

    var mobile: Boolean = false

    var isPrivateGame = false
    var password = ""

    var players: MutableList<Player> = ArrayList()

    @Suppress("unused")
    val playerList: String
        get() = players.map { it.username }.joinToString(", ")

    val humanPlayers: List<Player>
        get() = players.filterNot { it.isBot }

    val playerMap: MutableMap<String, Player> = HashMap(6)
    private val playersExited = HashSet<String>(6)

    private val computerPlayers: List<Player>
        get() = players.filter { it.isBot }

    var decks: MutableList<Deck> = ArrayList()

    var kingdomCards = mutableListOf<Card>()

    @Suppress("unused")
    val cardList: String
        get() = kingdomCards.map { it.name }.joinToString(", ")

    val topKingdomCards: List<Card>
        get() {
            return kingdomCards.map {
                if (isMultiTypePileCard(it) && multiTypePileMap[it.pileName]!!.isNotEmpty()) {
                    multiTypePileMap[it.pileName]!!.first()
                } else {
                    it
                }
            }
        }

    var events = mutableListOf<Event>()

    var landmarks = mutableListOf<Landmark>()
    
    var projects = mutableListOf<Project>()

    var ways = mutableListOf<Way>()

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

    private val multiTypePileMap = HashMap<String, MutableList<Card>>()

    val victoryPointsOnSupplyPile = HashMap<String, Int>()

    val debtOnSupplyPile = HashMap<String, Int>()

    val allCards: List<Card>
        get() {
            val cards = (cardsInSupply + kingdomCards + cardsNotInSupply).toMutableList()
            kingdomCards.filterIsInstance<MultiTypePile>().forEach { cards.addAll(it.otherCardsInPile) }
            return cards
        }

    val allCardsCopy: List<Card>
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

    private val currentTurnLog = StringBuilder()

    private var quitGamePlayer: Player? = null

    val chats = ArrayList<ChatMessage>()

    var gameEndReason = ""
    var winnerString = ""

    private var determinedWinner = false

    var isAbandonedGame: Boolean = false

    var isTestGame: Boolean = false

    private val recentTurnLogs = ArrayList<String>()

    var creationTime = Date()

    var lastActivity = Date()

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

    var isShowVictoryPoints: Boolean = false
    var isIdenticalStartingHands: Boolean = false

    var isAlwaysIncludeColonyAndPlatinum: Boolean = false
    var isNeverIncludeColonyAndPlatinum: Boolean = false

    var isIncludeShelters: Boolean = false
    var isExcludeShelters: Boolean = false

    var isIncludeSpoils: Boolean = false

    var isIncludeRuins: Boolean = false
    var ruinsPile: MutableList<Card> = ArrayList()

    var isIncludeHorse: Boolean = false

    var randomizingOptions: RandomizingOptions? = null

    var isRandomizerReplacementCardNotFound: Boolean = false

    var isShowTavern: Boolean = false

    var isShowJourneyToken: Boolean = false

    var isShowEmbargoTokens: Boolean = false

    var isShowDuration: Boolean = false

    var isShowIslandCards: Boolean = false

    var isShowExileCards: Boolean = false

    val victoryCards: List<Card>
        get() = allCards
                .filter { it.isVictory }
                .sortedByDescending { it.cost }

    var isShowNativeVillage: Boolean = false
    var isShowPirateShipCoins: Boolean = false

    val tradeRouteTokenMap = HashMap<String, Boolean>(0)
    var isTrackTradeRouteTokens: Boolean = false
    var tradeRouteTokensOnMat: Int = 0

    val isGameOver: Boolean
        get() = numEmptyPiles >= 3
                || pileAmounts[Province.NAME] == 0
                || (isIncludeColonyCards && pileAmounts[Colony.NAME] == 0)

    val embargoTokens = HashMap<String, Int>()

    val treasureCardsPlayedInActionPhase = mutableListOf<Card>()

    val currentPlayerCardCostModifiers = mutableListOf<CardCostModifier>()

    val gameCardCostModifiers = mutableListOf<CardCostModifier>()

    val cardCostModifiers: List<CardCostModifier>
        get() {
            val modifiers = (currentPlayerCardCostModifiers +
                    gameCardCostModifiers +
                    currentPlayer.inPlayWithDuration.filterIsInstance<CardCostModifierForCardsInPlay>() +
                    currentPlayer.projectsBought.filterIsInstance<CardCostModifier>()
                    ).toMutableList()

            currentPlayer.inPlayWithDuration
                    .filter { it is CardRepeater && it.cardBeingRepeated is CardCostModifierForCardsInPlay }
                    .forEach {
                        val cardRepeater = it as CardRepeater
                        repeat(cardRepeater.timesRepeated) {
                            modifiers.add(cardRepeater.cardBeingRepeated as CardCostModifier)
                        }
                    }

            return modifiers
        }

    var previousPlayerId = ""
    val previousPlayer: Player?
        get() = playerMap[previousPlayerId]

    private var previousTurn: PlayerTurn? = null
    private var currentTurn: PlayerTurn? = null
    private val turnHistory = ArrayList<PlayerTurn>()
    val recentTurnHistory = LinkedList<PlayerTurn>()
    private var maxHistoryTurnSize: Int = 0
    val recentHistory: List<String>
        get() {
            val currentPlayerRecentLogs = currentTurn?.recentEvents ?: emptyList()
            return if (currentPlayerRecentLogs.isEmpty()) {
                previousTurn?.recentEvents ?: emptyList()
            } else {
                currentPlayerRecentLogs
            }
        }

    val lastTurnSummaries
        get() = players.mapNotNull {
            it.lastTurnSummary
        }

    var isShowPrizeCards: Boolean = false
    var prizeCards: MutableList<Card> = ArrayList(0)

    //artifacts
    var artifacts = mutableListOf<Artifact>()

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

        setupSupply()

        kingdomCards.forEach {
            cardMap[it.name] = it

            if (it is GameSetupModifier) {
                it.modifyGameSetup(this)
            }

            if (it is UsesHorses) {
                isIncludeHorse = true
            }

            if (it is UsesExileMat) {
                isShowExileCards = true
            }

            if (it.isDuration) {
                isShowDuration = true
            }

            if (it.isLooter) {
                isIncludeRuins = true
            }

            if (it is TavernCard) {
                isShowTavern = true
            }

            if (it is MultiTypePile) {
                it.otherCardsInPile.forEach { card -> cardMap[card.name] = card }
                multiTypePileMap[it.name] = it.createMultiTypePile(this).toMutableList()
            }

            if (it is ArtifactAction) {
                artifacts.addAll(it.artifacts)
            }
        }

        events.forEach { event ->
            if (event is GameSetupModifier) {
                event.modifyGameSetup(this)
            }
            if (event is UsesHorses) {
                isIncludeHorse = true
            }
            if (event is UsesExileMat) {
                isShowExileCards = true
            }
        }

        events.sortBy { it.cost }

        landmarks.forEach { landmark ->
            if (landmark is GameSetupModifier) {
                landmark.modifyGameSetup(this)
            }
        }

        projects.forEach { project ->
            if (project is GameSetupModifier) {
                project.modifyGameSetup(this)
            }
        }

        projects.sortBy { it.cost }

        ways.forEach { way ->
            if (way is GameSetupModifier) {
                way.modifyGameSetup(this)
            }
        }

        if (isIncludeHorse) {
            cardsNotInSupply.add(Horse())
            pileAmounts[Horse.NAME] = 30
        }

        if (isIncludeRuins) {
            createRuinsPile()
        }

        if (isIncludeSpoils) {
            cardsNotInSupply.add(Spoils())
            pileAmounts[Spoils.NAME] = 15
        }

        if (isIncludeShelters) {
            cardsNotInSupply.add(Hovel())
            cardsNotInSupply.add(Necropolis())
            cardsNotInSupply.add(OvergrownEstate())
        }

        cardsInSupply.forEach { cardMap[it.name] = it }
        cardsNotInSupply.forEach { cardMap[it.name] = it }

        if (isIncludeRuins) {
            ruinsPile.distinctBy { it.name }.forEach { cardMap[it.name] = it }
        }

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
            var numEachCard = card.pileSize
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
        startTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        players.shuffle()
        status = GameStatus.InProgress

        maxHistoryTurnSize = players.size + 1

        kingdomCards.filterIsInstance<GameStartedListener>().forEach { it.onGameStarted(this) }

        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun startTurnInNewThreadIfComputerVsHuman(refreshPreviousPlayerCardsBought: Boolean = false) {
        if (recentTurnHistory.size == maxHistoryTurnSize) {
            recentTurnHistory.removeFirst()
        }
        if (currentTurn != null) {
            previousTurn = currentTurn
            currentTurn!!.addInfoLog("")
        }
        currentTurn = PlayerTurn(currentPlayer)
        recentTurnHistory.add(currentTurn!!)
        turnHistory.add(currentTurn!!)

        refreshHistory()

        if (currentPlayer.isBot && currentPlayer.opponents.any { it is HumanPlayer }) {
            currentPlayer.opponents.filterIsInstance<HumanPlayer>().forEach { p ->
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

    fun refreshPlayers() {
        if (isShowVictoryPoints) {
            gameMessageService.refreshPlayers(this)
        }
    }

    fun refreshCoins() {
        gameMessageService.refreshCardsBought(this)
    }

    fun refreshDebt() {
        gameMessageService.refreshCardsBought(this)
    }

    fun refreshBuys() {
        gameMessageService.refreshCardsBought(this)
    }

    fun refreshCardsBought() {
        gameMessageService.refreshCardsBought(this)
        if (currentPlayer.isBot) {
            Thread.sleep(1000)
        }
    }

    fun refreshPlayerCardsBought(player: Player) {
        gameMessageService.refreshCardsBought(player)
    }

    fun refreshPreviousPlayerCardsBought(player: Player) {
        gameMessageService.refreshPreviousPlayerCardsBought(player)
    }

    fun refreshCardsPlayed() {
        gameMessageService.refreshCardsPlayed(this)
        if (currentPlayer.isBot) {
            Thread.sleep(1000)
        }
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
        if (player.isBot) {
            return
        }
        gameMessageService.showInfoMessage(player, message)
    }

    fun turnEnded(isAutoEnd: Boolean) {
        addInfoLog("End of turn $turn")

        if (isGameOver) {
            gameOver()
            return
        }

        treasureCardsPlayedInActionPhase.clear()

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

    fun gameOver() {
        addInfoLog("GAME OVER")
        status = GameStatus.Finished
        determineWinner()
        refreshGame()
    }

    fun isCardAvailableInSupply(card: Card): Boolean {
        return if (isMultiTypePileCard(card)) {
            multiTypePileMap[card.pileName]?.firstOrNull()?.name == card.name
        } else {
            numInPileMap.containsKey(card.pileName) && numInPileMap[card.pileName]!! > 0
        }
    }

    fun isCardNotInSupply(card: Card): Boolean {
        return cardsNotInSupply.any { it.name == card.name }
    }

    private fun isMultiTypePileCard(card: Card) = multiTypePileMap.containsKey(card.pileName)

    fun removeCardFromSupply(card: Card, refreshSupply: Boolean = true) {
        if (card.isRuins) {
            ruinsPile.removeAt(0)
        } else {
            pileAmounts[card.pileName] = pileAmounts[card.pileName]!!.minus(1)
        }

        if (isMultiTypePileCard(card)) {
            multiTypePileMap[card.pileName]!!.removeAt(0)
        }

        if (refreshSupply) {
            refreshSupply()
        }
    }

    fun returnCardToSupply(card: Card) {
        val cardToReturn = if (card is InheritanceEstate) Estate() else card

        if (cardToReturn.isRuins) {
            ruinsPile.add(0, cardToReturn)
        } else {
            pileAmounts[cardToReturn.pileName] = pileAmounts[cardToReturn.pileName]!!.plus(1)
        }

        if (isMultiTypePileCard(card)) {
            multiTypePileMap[card.pileName]!!.add(0, card)
        }

        refreshSupply()
    }

    fun exchangeCardInSupply(putBackInSupply: Card, takeFromSupply: Card) {
        returnCardToSupply(putBackInSupply)
        removeCardFromSupply(takeFromSupply)
    }

    private val nonEmptyPiles: Int
        get() = (cardsInSupply + kingdomCards).filter { numInPileMap[it.pileName]!! > 0 }.size

    val numEmptyPiles
        get() = (cardsInSupply + kingdomCards).size - nonEmptyPiles

    val emptyPileNames: Set<String>
        get() = numInPileMap.filterValues { it == 0 }.keys

    val availableCards
        get() = allCards.filter { isCardAvailableInSupply(it) && !isCardNotInSupply(it) }

    val availableCardsCopy
        get() = allCardsCopy.filter { isCardAvailableInSupply(it) && !isCardNotInSupply(it) }

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
            errorHistory.append(KingdomUtil.implode(currentTurn!!.allLogs, ";"))
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
        return cardMap[cardName]!!.copy(false)
    }

    fun getNewInstanceOfEvent(eventName: String): Event {
        return events.first { it.name == eventName }.copy(false) as Event
    }

    fun getNewInstanceOfLandmark(landmarkName: String): Landmark {
        return landmarks.first { it.name == landmarkName }.copy(false) as Landmark
    }

    fun getNewInstanceOfProject(projectName: String): Project {
        return projects.first { it.name == projectName }.copy(false) as Project
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
        val user = User()

        when {
            bigMoneyUltimate -> {
                user.username = "C$i (BMU)"
                addPlayer(user, true, true, 3)
            }
            difficulty == 1 -> {
                user.username = "C$i (easy)"
                addPlayer(user, true, false, 1)
            }
            difficulty == 2 -> {
                user.username = "C$i (medium)"
                addPlayer(user, true, false, 2)
            }
            difficulty == 3 -> {
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
                null
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
        addInfoLog(gameEndReason)
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
            val leastTurns = players.minBy { it.turns }!!.turns
            val marginOfVictory = players[0].finalVictoryPoints - players[1].finalVictoryPoints
            var winners = mutableListOf<Player>()
            for (player in players) {
                if (player.finalVictoryPoints == highScore) {
                    player.isWinner = true
                    player.marginOfVictory = marginOfVictory
                    winners.add(player)
                } else {
                    break
                }
            }

            if (winners.size > 1) {
                winners = winners.filter { it.turns == leastTurns }.toMutableList()
            }

            winnerString = if (winners.size == 1) {
                winners[0].username + " wins!"
            } else {
                val sb = StringBuilder()
                for (i in winners.indices) {
                    if (i != 0) {
                        sb.append(", ")
                    }
                    if (i == winners.lastIndex) {
                        sb.append("and ")
                    }
                    sb.append(winners[i].username)
                }
                sb.toString() + " tie for the win!"
            }

            for (computerPlayer in computerPlayers) {
                playerExitedGame(computerPlayer)
            }
        }
    }

    fun addEventLog(log: String) {
        currentTurn?.addEventLog(log)
        refreshHistory()
    }

    fun addInfoLog(log: String) {
        currentTurn?.addInfoLog(log)
        refreshHistory()
    }

    fun setupAmountForPile(cardName: String, amount: Int) {
        pileAmounts[cardName] = amount
    }

    fun getVictoryPointsOnSupplyPile(pileName: String): Int {
        return victoryPointsOnSupplyPile[pileName] ?: 0
    }

    fun addVictoryPointToSupplyPile(pileName: String) {
        addVictoryPointsToSupplyPile(pileName, 1)
    }

    fun addVictoryPointsToSupplyPile(pileName: String, victoryPoints: Int) {
        victoryPointsOnSupplyPile[pileName] = getVictoryPointsOnSupplyPile(pileName) + victoryPoints
        refreshSupply()
    }

    fun removeVictoryPointsFromSupplyPile(pileName: String, victoryPoints: Int) {
        addVictoryPointsToSupplyPile(pileName, victoryPoints * -1)
    }

    fun getDebtOnSupplyPile(pileName: String): Int {
        return debtOnSupplyPile[pileName] ?: 0
    }

    fun addDebtToSupplyPile(pileName: String, debt: Int) {
        debtOnSupplyPile[pileName] = getDebtOnSupplyPile(pileName) + debt
        refreshSupply()
    }

    fun clearDebtFromSupplyPile(pileName: String) {
        debtOnSupplyPile[pileName] = 0
        refreshSupply()
    }
}
