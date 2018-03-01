package com.kingdom.model

import com.kingdom.model.computer.*
import com.kingdom.service.GameManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.CardCostComparator
import com.kingdom.util.DurationHandler
import com.kingdom.util.KingdomUtil
import com.kingdom.util.cardaction.CardActionHandler
import com.kingdom.util.cardaction.NextActionHandler
import com.kingdom.util.specialaction.*

import java.util.*

class Game(val gameId: Int) {

    var status: Int = 0
        set(value) {
            updateLastActivity()
            field = value
        }
    var numPlayers: Int = 0
    var numComputerPlayers: Int = 0
    var numEasyComputerPlayers: Int = 0
    var numMediumComputerPlayers: Int = 0
    var numHardComputerPlayers: Int = 0
    var numBMUComputerPlayers: Int = 0
    var isAllComputerOpponents: Boolean = false
        private set
    val players: MutableList<Player> = ArrayList(6)
    val playerMap: MutableMap<Int, Player> = HashMap(6)
    var kingdomCards: MutableList<Card> = ArrayList()
        set(value) {
            if (value.size == 11) {
                baneCardId = value[10].cardId
            } else {
                baneCardId = 0
            }
            field = value
        }
    val kingdomCardMap = HashMap<String, Card>(10)
    val supplyCards = ArrayList<Card>()
    val cardMap = HashMap<Int, Card>()
    val supplyMap = HashMap<Int, Card>()
    val supply = HashMap<Int, Int>()
    val embargoTokens = HashMap<Int, Int>()
    val trollTokens = HashMap<Int, Int>()
    var currentPlayerIndex = 0
        private set
    var currentPlayerId = -1
        private set
    val trashedCards = ArrayList<Card>()
    val cardsPlayed = LinkedList<Card>()
    val cardsBought = ArrayList<Card>()
    val needsRefresh: MutableMap<Int, Refresh> = HashMap(6)
    private var finishGameOnNextEndTurn: Boolean = false
    val repeatedActions: Deque<RepeatedAction> = ArrayDeque(3)
    val golemActions: Deque<Card> = ArrayDeque(0)
    val trashedTreasureCards = ArrayList<Card>(6)
    private val turnHistory = ArrayList<PlayerTurn>()
    val recentTurnHistory = LinkedList<PlayerTurn>()
    private var currentTurn: PlayerTurn? = null
    val setAsideCards = ArrayList<Card>()
    private val playersExited = HashSet<Int>(6)
    val chats = ArrayList<ChatMessage>()
    private val colors = ArrayList<String>(6)
    val masqueradeCards = HashMap<Int, Card>(0)
    private var currentColorIndex = 0
    var costDiscount = 0
        get() = if (trackHighway && highwayCardsInPlay > 0) {
            field + highwayCardsInPlay
        } else field
    var numActionsCardsPlayed = 0
        private set
    var actionCardsInPlay = 0
        private set
    private var lighthousePlayed: Boolean = false
    var isShowGardens: Boolean = false
        private set
    var isShowFarmlands: Boolean = false
        private set
    var isShowVictoryCoins: Boolean = false
        private set
    var isShowVineyard: Boolean = false
        private set
    var isShowSilkRoads: Boolean = false
        private set
    var isShowCathedral: Boolean = false
        private set
    var isShowFairgrounds: Boolean = false
        private set
    var isShowGreatHall: Boolean = false
        private set
    var isShowHarem: Boolean = false
        private set
    var isShowDuke: Boolean = false
        private set
    var isShowNobles: Boolean = false
        private set
    var isShowArchbishops: Boolean = false
        private set
    var isShowDuration: Boolean = false
        private set
    var isShowEmbargoTokens: Boolean = false
        private set
    var isShowTrollTokens: Boolean = false
        private set
    var isShowIslandCards: Boolean = false
        private set
    var isShowMuseumCards: Boolean = false
        private set
    var isShowCityPlannerCards: Boolean = false
        private set
    var isShowNativeVillage: Boolean = false
        private set
    var isShowPirateShipCoins: Boolean = false
        private set
    var isShowFruitTokens: Boolean = false
        private set
    var isShowCattleTokens: Boolean = false
        private set
    var isShowHedgeWizard: Boolean = false
        private set
    var isShowGoldenTouch: Boolean = false
        private set
    var isShowSins: Boolean = false
        private set
    val durationCardsPlayed = ArrayList<Card>(0)
    private var outpostTurn: Boolean = false
    private var outpostCardPlayed: Boolean = false
    val smugglersCards = ArrayList<Card>(0)
    private val smugglersCardsGained = ArrayList<Card>(0)
    private var trackSmugglersCards: Boolean = false
    private var boughtVictoryCard: Boolean = false
    private var trackTreasuryCards: Boolean = false
    private var trackAlchemistCards: Boolean = false
    private var trackHerbalistCards: Boolean = false
    private var playedTreasuryCard: Boolean = false
    private var playedAlchemistCard: Boolean = false
    private var playedHerbalistCard: Boolean = false
    private var checkSecretChamber: Boolean = false
    private var checkHorseTraders: Boolean = false
    private var checkBellTower: Boolean = false
    var isCheckEnchantedPalace: Boolean = false
        private set
    var attackCard: Card? = null
        private set
    var throneRoomCard: Card? = null
        private set
    var kingsCourtCard: Card? = null
        private set
    var blackMarketCards: MutableList<Card> = ArrayList(0)
    var decks: List<Deck> = ArrayList(5)
    var isUsePotions: Boolean = false
        private set
    private var potionsPlayed: Int = 0
    val treasureCardsPlayed = ArrayList<Card>(5)
    val blackMarketTreasureCardsPlayed = ArrayList<Card>(5)
    val blackMarketTreasureQueue: Queue<Card> = LinkedList()
    private val processingClick = HashMap<Int, Boolean>(2)
    var isPlayTreasureCards = false
    var isIncludePlatinumCards: Boolean = false
    var isIncludeColonyCards: Boolean = false
    var copiedPlayedCard: Boolean = false
    var goonsCardsPlayed: Int = 0
        private set
    private var hoardCardsPlayed: Int = 0
    private var talismanCardsPlayed: Int = 0
    var actionCardDiscount: Int = 0
        private set
    val contrabandCards = HashSet<Card>(0)
    var isTrackContrabandCards: Boolean = false
        private set
    var isTrackBankCards: Boolean = false
        private set
    private var refreshPeddler: Boolean = false
    private var royalSealCardPlayed: Boolean = false
    val tradeRouteTokenMap = HashMap<Int, Boolean>(0)
    var isTrackTradeRouteTokens: Boolean = false
        private set
    var tradeRouteTokensOnMat: Int = 0
        private set
    private var checkWatchtower: Boolean = false
    private var checkTinker: Boolean = false
    var blackMarketCardsToBuy: MutableList<Card> = ArrayList(0)
    val computerPlayers = HashMap<Int, ComputerPlayer>(0)
    val costMap = HashMap<Int, MutableList<Card>>()
    val potionCostMap = HashMap<Int, MutableList<Card>>()
    var creationTime = Date()
        private set
    var randomizingOptions: RandomizingOptions? = null
    private var gameManager: GameManager? = null
    private var custom: Boolean = false
    var lastActivity: Date? = null
        private set
    private var determinedWinner = false
    private var savedGameHistory = false
    var gameEndReason = ""
    var winnerString = ""
        private set
    private val emptyPiles = ArrayList<Card>(3)
    private var trackActionCardsPlayed: Boolean = false
    private val actionCardsPlayed = ArrayList<Card>(3)
    private var historyEntriesAddedThisTurn = 0
    private var twoCostKingdomCards = 0
    var logId: Int = 0
        private set
    var isAlwaysIncludeColonyAndPlatinum: Boolean = false
    var isNeverIncludeColonyAndPlatinum: Boolean = false
    var title = ""
    var isPrivateGame = false
    var password = ""
    val edictCards = HashSet<Card>(0)
    private var trackEdictCards: Boolean = false
    var isAnnotatedGame: Boolean = false
    var creatorId: Int = 0
    var creatorName = ""
    var isTestGame: Boolean = false
    var prizeCards: MutableList<Card> = ArrayList(0)
    var isShowPrizeCards: Boolean = false
        private set
    var isGainTournamentBonus: Boolean = false
    private var princessCardPlayed: Boolean = false
    var baneCardId: Int = 0
        private set
    var horseTradersCard: Card? = null
        private set
    private var maxHistoryTurnSize: Int = 0
    var isAbandonedGame: Boolean = false
    var isEndingTurn: Boolean = false
    var isShowVictoryPoints: Boolean = false
    var isIdenticalStartingHands: Boolean = false
    val playersWaitingForBellTowerBonus = ArrayList<Player>(0)
    var incompleteCard: IncompleteCard? = null
    protected var nextActionQueue: Queue<String> = LinkedList()
    private val enchantedPalaceRevealed = ArrayList<Int>()
    val playersWithCardActions: MutableSet<Int> = HashSet(0)
    private var checkWalledVillage: Boolean = false
    private var playedWalledVillage: Boolean = false
    private var repeated: Boolean = false
    var mobile: Boolean = false
    var previousPlayerId = 0
        private set
    val previousPlayerCardsPlayed = ArrayList<Card>()
    val previousPlayerCardsBought = ArrayList<Card>()
    var isUsingLeaders: Boolean = false
    var availableLeaders: MutableList<Card> = ArrayList(0)
    var isCheckQuest: Boolean = false
        private set
    private var checkTrader: Boolean = false
    var crossroadsPlayed: Int = 0
        private set
    private val cardsWithGainCardActions = HashMap<Int, Card>(0)
    private var trackHighway: Boolean = false
    private var highwayCardsInPlay: Int = 0
    private var trackGoons: Boolean = false
    var isCheckDuchess: Boolean = false
        private set
    private var checkScheme: Boolean = false
    private var schemeCardsPlayed: Int = 0
    private var checkTunnel: Boolean = false
    private var checkHaggler: Boolean = false
    private var hagglerCardsInPlay: Int = 0
    private var checkFoolsGold: Boolean = false
    var foolsGoldCard: Card? = null
        private set
    private var checkNobleBrigand: Boolean = false
    private var trackLaborer: Boolean = false
    private var laborerCardsInPlay: Int = 0
    var isRecentGame: Boolean = false
    var isRecommendedSet: Boolean = false
    var isRandomizerReplacementCardNotFound: Boolean = false
    private var trackGoodwill: Boolean = false
    private var goodwillCardsInPlay: Int = 0
    var fruitTokensPlayed: Int = 0
        private set
    private var checkPlantation: Boolean = false

    private val setupLeadersCardAction: CardAction
        get() {
            val cardAction = CardAction(CardAction.TYPE_SETUP_LEADERS)
            cardAction.numCards = 3
            cardAction.cards = availableLeaders
            cardAction.deck = Deck.Leaders
            cardAction.buttonValue = "Done"
            cardAction.cardName = "Setup Leaders"
            val instructions = "Choose 3 Leader cards and then click Done"
            cardAction.instructions = instructions
            return cardAction
        }

    val groupedCardsPlayed: List<Card>
        get() {
            KingdomUtil.groupCards(cardsPlayed)
            return cardsPlayed
        }

    val groupedCardsBought: List<Card>
        get() {
            KingdomUtil.groupCards(cardsBought)
            return cardsBought
        }

    val nextPlayerIndex: Int
        get() = if (currentPlayerIndex == players.size - 1) {
            0
        } else currentPlayerIndex + 1

    val previousPlayer: Player?
        get() = playerMap[previousPlayerId]

    val currentPlayer: Player?
        get() = playerMap[currentPlayerId]

    val nextColor: String
        get() {
            val color = colors[currentColorIndex]
            if (currentColorIndex == colors.size - 1) {
                currentColorIndex = 0
            } else {
                currentColorIndex++
            }
            return color
        }

    val numEmptyPiles: Int
        get() = emptyPiles.size

    val gameTime: String
        get() = KingdomUtil.getTimeAgo(creationTime)

    val lastActivityString: String
        get() = KingdomUtil.getTimeAgo(lastActivity!!)

    val estateCard: Card
        get() = cardMap[Card.ESTATE_ID]!!

    val duchyCard: Card
        get() = cardMap[Card.DUCHY_ID]!!

    val provinceCard: Card
        get() = cardMap[Card.PROVINCE_ID]!!

    val colonyCard: Card
        get() = cardMap[Card.COLONY_ID]!!

    val copperCard: Card
        get() = cardMap[Card.COPPER_ID]!!

    val silverCard: Card
        get() = cardMap[Card.SILVER_ID]!!

    val goldCard: Card
        get() = cardMap[Card.GOLD_ID]!!

    val platinumCard: Card
        get() = cardMap[Card.PLATINUM_ID]!!

    val curseCard: Card
        get() = cardMap[Card.CURSE_ID]!!

    val availableTreasureCardsInSupply: List<Card>
        get() {
            val cards = ArrayList<Card>()
            for (card in supplyMap.values) {
                val numInSupply = getNumInSupply(card.cardId)
                if (numInSupply > 0 && card.isTreasure) {
                    cards.add(card)
                }
            }
            return cards
        }

    val isBuyPhase: Boolean
        get() {
            val currentPlayer = currentPlayer
            return currentPlayer!!.hasBoughtCard() || !treasureCardsPlayed.isEmpty()
        }

    val playerList: String
        get() {
            val playerNames = ArrayList<String>()
            for (player in players) {
                playerNames.add(player.username)
            }
            return KingdomUtil.implode(playerNames, ", ")
        }

    val cardList: String
        get() {
            val cardNames = ArrayList<String>()
            for (card in kingdomCardMap.values) {
                cardNames.add(card.name)
            }
            return KingdomUtil.implode(cardNames, ", ")
        }

    val prizeCardsString: String
        get() = if (prizeCards.isEmpty()) {
            "None"
        } else {
            KingdomUtil.getCardNames(prizeCards)
        }

    val nextAction: String?
        get() = nextActionQueue.peek()

    val numProvincesLeft: Int
        get() = getNumInSupply(Card.PROVINCE_ID)

    val numColoniesLeft: Int
        get() = if (!isIncludeColonyCards) {
            0
        } else getNumInSupply(Card.COLONY_ID)

    val cardWithUnfinishedGainCardActions: Card
        get() {
            val iterator = cardsWithGainCardActions.values.iterator()
            return iterator.next()
        }

    init {
        setPlayerColors()
    }

    private fun setPlayerColors() {
        colors.add("red")
        colors.add("#001090") //dark blue
        colors.add("green")
        colors.add("#0E80DF") //light blue
        colors.add("purple")
        colors.add("#EF7C00") //dark orange
    }

    fun init() {
        sortKingdomCards()
        creationTime = Date()
        updateLastActivity()
        populateCardMaps()
        setupTokens()
        if (numComputerPlayers > 0) {
            addComputerPlayers()
        }
    }

    private fun setupTokens() {
        if (isShowEmbargoTokens) {
            for (cardId in cardMap.keys) {
                embargoTokens[cardId] = 0
            }
        }
        if (isShowTrollTokens) {
            for (cardId in cardMap.keys) {
                trollTokens[cardId] = 0
            }
        }
        if (isTrackTradeRouteTokens || !blackMarketCards.isEmpty()) {
            for (card in supplyMap.values) {
                tradeRouteTokenMap[card.cardId] = card.isVictory
            }
        }
    }

    private fun setupSupply() {
        for (card in kingdomCards) {
            var numEachCard = 10
            if (card.isVictory) {
                if (numPlayers == 2) {
                    numEachCard = 8
                } else {
                    numEachCard = 12
                }
            }
            supply[card.cardId] = numEachCard
        }
        if (numPlayers > 4) {
            supply[Card.COPPER_ID] = 120
        } else {
            supply[Card.COPPER_ID] = 60
        }
        if (numPlayers > 4) {
            supply[Card.SILVER_ID] = 80
        } else {
            supply[Card.SILVER_ID] = 40
        }
        if (numPlayers > 4) {
            supply[Card.GOLD_ID] = 60
        } else {
            supply[Card.GOLD_ID] = 30
        }
        if (isIncludePlatinumCards) {
            supply[Card.PLATINUM_ID] = 12
        }
        if (numPlayers == 2) {
            supply[Card.ESTATE_ID] = 8
            supply[Card.DUCHY_ID] = 8
            supply[Card.PROVINCE_ID] = 8
            if (isIncludeColonyCards) {
                supply[Card.COLONY_ID] = 8
            }
            supply[Card.CURSE_ID] = 10
        } else if (numPlayers > 4) {
            supply[Card.ESTATE_ID] = 12
            supply[Card.DUCHY_ID] = 12
            if (isIncludeColonyCards) {
                supply[Card.COLONY_ID] = 12
            }
            if (numPlayers == 5) {
                supply[Card.PROVINCE_ID] = 15
                supply[Card.CURSE_ID] = 40
            } else {
                supply[Card.PROVINCE_ID] = 18
                supply[Card.CURSE_ID] = 50
            }
        } else {
            supply[Card.ESTATE_ID] = 12
            supply[Card.DUCHY_ID] = 12
            supply[Card.PROVINCE_ID] = 12
            if (isIncludeColonyCards) {
                supply[Card.COLONY_ID] = 12
            }
            if (numPlayers == 3) {
                supply[Card.CURSE_ID] = 20
            } else {
                supply[Card.CURSE_ID] = 30
            }
        }
        if (isUsePotions) {
            supply[Card.POTION_ID] = 16
        }
    }

    private fun populateCardMaps() {
        for (card in kingdomCards) {
            checkCardName(card, false)
            supplyMap[card.cardId] = card
            cardMap[card.cardId] = card
            if (card.cost == 2) {
                twoCostKingdomCards++
            }
            kingdomCardMap[card.name] = card
        }
        for (card in blackMarketCards) {
            cardMap[card.cardId] = card
            if (card.costIncludesPotion) {
                isUsePotions = true
            }
            if (card.name == "Embargo") {
                isShowEmbargoTokens = true
            } else if (card.name == "Bridge Troll") {
                isShowTrollTokens = true
            }
        }
        if (isShowPrizeCards || !blackMarketCards.isEmpty()) {
            for (card in prizeCards) {
                cardMap[card.cardId] = card
            }
        }
        if (isUsingLeaders) {
            for (card in availableLeaders) {
                cardMap[card.cardId] = card
            }
        }
        setupSupply()
        supplyCards.add(Card.copperCard)
        supplyCards.add(Card.silverCard)
        supplyCards.add(Card.goldCard)
        if (isIncludePlatinumCards) {
            supplyCards.add(Card.platinumCard)
        }
        if (isUsePotions) {
            supplyCards.add(Card.potionCard)
        }
        supplyCards.add(Card.estateCard)
        supplyCards.add(Card.duchyCard)
        supplyCards.add(Card.provinceCard)
        if (isIncludeColonyCards) {
            supplyCards.add(Card.colonyCard)
        }
        supplyCards.add(Card.curseCard)
        for (supplyCard in supplyCards) {
            cardMap[supplyCard.cardId] = supplyCard
            supplyMap[supplyCard.cardId] = supplyCard
        }
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
        for (card in supplyMap.values) {
            if (card.costIncludesPotion) {
                var cards: MutableList<Card>? = potionCostMap[card.cost]
                if (cards == null) {
                    cards = ArrayList()
                }
                cards.add(card)
                potionCostMap[card.cost] = cards
            } else {
                var cards: MutableList<Card>? = costMap[card.cost]
                if (cards == null) {
                    cards = ArrayList()
                }
                cards.add(card)
                costMap[card.cost] = cards
            }
        }
    }

    private fun sortKingdomCards() {
        val ccc = CardCostComparator()
        if (baneCardId != 0) {
            val baneCard = kingdomCards[10]
            val otherCards = kingdomCards.subList(0, 10)
            Collections.sort(otherCards, ccc)
            otherCards.add(5, baneCard)
            kingdomCards = otherCards
        } else {
            Collections.sort(kingdomCards, ccc)
        }
    }

    fun addComputerPlayer(i: Int, bigMoneyUltimate: Boolean, difficulty: Int) {
        val userId = i * -1
        val user = User()
        user.gender = User.COMPUTER
        if (bigMoneyUltimate) {
            user.userId = userId - 40
            user.username = "C$i (BMU)"
            addPlayer(user, true, true, 3)
        } else if (difficulty == 1) {
            user.userId = userId - 10
            user.username = "C$i (easy)"
            addPlayer(user, true, false, 1)
        } else if (difficulty == 2) {
            user.userId = userId - 20
            user.username = "C$i (medium)"
            addPlayer(user, true, false, 2)
        } else if (difficulty == 3) {
            user.userId = userId - 30
            user.username = "C$i (hard)"
            addPlayer(user, true, false, 3)
        }
    }

    @JvmOverloads
    fun addPlayer(user: User, computer: Boolean = false, bigMoneyUltimate: Boolean = false, difficulty: Int = 0) {
        val player = Player(user, this)
        player.isComputer = computer
        player.chatColor = nextColor
        players.add(player)
        playerMap[player.userId] = player
        needsRefresh[player.userId] = Refresh()
        if (computer) {
            when {
                bigMoneyUltimate -> computerPlayers[player.userId] = BigMoneyComputerPlayer(player, this)
                difficulty == 1 -> computerPlayers[player.userId] = EasyComputerPlayer(player, this)
                difficulty == 2 -> computerPlayers[player.userId] = MediumComputerPlayer(player, this)
                else -> computerPlayers[player.userId] = HardComputerPlayer(player, this)
            }
        }
        if (isUsingLeaders) {
            setPlayerCardAction(player, setupLeadersCardAction)
        }
        if (!repeated && players.size == numPlayers) {
            start()
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

    fun numTrollTokens(cardId: Int): Int {
        return trollTokens[cardId]!!
    }

    fun canBuyCard(player: Player, card: Card): Boolean {
        val cost = getCardCost(card, player, true)
        val numInSupply = supply[card.cardId]
        return player.coins >= cost && (!card.costIncludesPotion || player.potions > 0) && (!isTrackContrabandCards || !contrabandCards.contains(card)) && numInSupply != null && numInSupply > 0
    }

    fun canBuyCardNotInSupply(player: Player, card: Card): Boolean {
        val cost = getCardCost(card, player, true)
        return player.coins >= cost && (!card.costIncludesPotion || player.potions > 0) && (!isTrackContrabandCards || !contrabandCards.contains(card))
    }

    fun getCardCost(card: Card): Int {
        return getCardCost(card, currentPlayer!!, isBuyPhase)
    }

    fun getCardCostBuyPhase(card: Card): Int {
        return getCardCost(card, currentPlayer!!, true)
    }

    private fun getCardCost(card: Card, player: Player, buyPhase: Boolean): Int {
        var cost = card.cost - costDiscount
        if (buyPhase) {
            cost -= player.getCardDiscount(card)
        }
        if (card.isAction) {
            cost -= actionCardDiscount
        }
        if (buyPhase && card.name == "Peddler") {
            cost -= 2 * actionCardsInPlay
        }
        if (buyPhase && isShowTrollTokens) {
            cost += trollTokens[card.cardId]!!
        }
        if (checkPlantation && card.name == "Plantation" && fruitTokensPlayed > 0) {
            cost -= fruitTokensPlayed
        }
        if (cost < 0) {
            cost = 0
        }
        return cost
    }

    fun removePlayedCard(card: Card) {
        cardsPlayed.remove(card)
        if (trackActionCardsPlayed && card.isAction) {
            actionCardsPlayed.remove(card)
        }
        if (trackGoons && card.name == "Goons") {
            goonsCardsPlayed--
        } else if (checkHaggler && card.name == "Haggler") {
            hagglerCardsInPlay--
        } else if (trackHighway && card.name == "Highway") {
            highwayCardsInPlay--
        } else if (trackLaborer && card.name == "Laborer") {
            laborerCardsInPlay--
        } else if (trackGoodwill && card.name == "Goodwill") {
            goodwillCardsInPlay--
        }
        actionCardsInPlay--
    }

    @JvmOverloads
    fun reset(repeatingGame: Boolean = false) {
        if (!repeatingGame) {
            status = Game.STATUS_NO_GAMES
            for (player in players) {
                LoggedInUsers.instance.gameReset(player.userId)
            }
            LoggedInUsers.instance.refreshLobbyPlayers()
            numPlayers = 0
            numComputerPlayers = 0
            numEasyComputerPlayers = 0
            numMediumComputerPlayers = 0
            numHardComputerPlayers = 0
            numBMUComputerPlayers = 0
            isAllComputerOpponents = false
            isUsePotions = false
            isPlayTreasureCards = false
            isIncludePlatinumCards = false
            isIncludeColonyCards = false
            supplyCards.clear()
            kingdomCards.clear()
            kingdomCardMap.clear()
            cardMap.clear()
            supplyMap.clear()
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
            isShowTrollTokens = false
            isShowIslandCards = false
            isShowMuseumCards = false
            isShowCityPlannerCards = false
            isShowNativeVillage = false
            isShowPirateShipCoins = false
            isShowFruitTokens = false
            isShowCattleTokens = false
            isShowSins = false
            trackSmugglersCards = false
            trackTreasuryCards = false
            trackAlchemistCards = false
            trackHerbalistCards = false
            checkSecretChamber = false
            checkHorseTraders = false
            checkBellTower = false
            isCheckEnchantedPalace = false
            isTrackContrabandCards = false
            isTrackBankCards = false
            refreshPeddler = false
            isTrackTradeRouteTokens = false
            trackActionCardsPlayed = false
            isAlwaysIncludeColonyAndPlatinum = false
            isNeverIncludeColonyAndPlatinum = false
            trackEdictCards = false
            isAnnotatedGame = false
            isRecommendedSet = false
            isTestGame = false
            isShowPrizeCards = false
            horseTradersCard = null
            checkWalledVillage = false
            isShowHedgeWizard = false
            isShowGoldenTouch = false
            isIdenticalStartingHands = false
            baneCardId = 0
            creatorId = 0
            creatorName = ""
            title = ""
            isPrivateGame = false
            password = ""
            twoCostKingdomCards = 0
            custom = false
            costMap.clear()
            potionCostMap.clear()
            checkWatchtower = false
            checkTinker = false
            mobile = false
            isUsingLeaders = false
            availableLeaders.clear()
            isCheckQuest = false
            checkTrader = false
            trackHighway = false
            trackGoons = false
            trackLaborer = false
            isCheckDuchess = false
            checkScheme = false
            schemeCardsPlayed = 0
            checkTunnel = false
            checkHaggler = false
            checkFoolsGold = false
            foolsGoldCard = null
            checkNobleBrigand = false
            trackGoodwill = false
            checkPlantation = false
        }
        players.clear()
        playerMap.clear()
        computerPlayers.clear()
        supply.clear()
        embargoTokens.clear()
        trollTokens.clear()
        trashedCards.clear()
        cardsPlayed.clear()
        cardsBought.clear()
        previousPlayerId = 0
        previousPlayerCardsPlayed.clear()
        previousPlayerCardsBought.clear()
        emptyPiles.clear()
        finishGameOnNextEndTurn = false
        needsRefresh.clear()
        repeatedActions.clear()
        golemActions.clear()
        trashedTreasureCards.clear()
        setAsideCards.clear()
        recentTurnHistory.clear()
        turnHistory.clear()
        currentTurn = null
        chats.clear()
        masqueradeCards.clear()
        currentPlayerIndex = 0
        currentPlayerId = -1
        currentColorIndex = 0
        playersExited.clear()
        costDiscount = 0
        numActionsCardsPlayed = 0
        actionCardsInPlay = 0
        durationCardsPlayed.clear()
        lighthousePlayed = false
        outpostTurn = false
        outpostCardPlayed = false
        smugglersCards.clear()
        boughtVictoryCard = false
        playedTreasuryCard = false
        playedAlchemistCard = false
        playedHerbalistCard = false
        attackCard = null
        potionsPlayed = 0
        treasureCardsPlayed.clear()
        processingClick.clear()
        copiedPlayedCard = false
        goonsCardsPlayed = 0
        hagglerCardsInPlay = 0
        hoardCardsPlayed = 0
        talismanCardsPlayed = 0
        actionCardDiscount = 0
        contrabandCards.clear()
        royalSealCardPlayed = false
        tradeRouteTokenMap.clear()
        tradeRouteTokensOnMat = 0
        determinedWinner = false
        savedGameHistory = false
        gameEndReason = ""
        winnerString = ""
        actionCardsPlayed.clear()
        historyEntriesAddedThisTurn = 0
        logId = 0
        edictCards.clear()
        prizeCards.clear()
        isGainTournamentBonus = false
        princessCardPlayed = false
        isAbandonedGame = false
        playersWaitingForBellTowerBonus.clear()
        incompleteCard = null
        enchantedPalaceRevealed.clear()
        playersWithCardActions.clear()
        playedWalledVillage = false
        repeated = false
        cardsWithGainCardActions.clear()
        highwayCardsInPlay = 0
        laborerCardsInPlay = 0
        isRecentGame = false
        isRandomizerReplacementCardNotFound = false
        goodwillCardsInPlay = 0
        fruitTokensPlayed = 0
        LoggedInUsers.instance.refreshLobbyGameRooms()
    }

    private fun start() {
        Collections.shuffle(players)
        currentPlayerId = players[currentPlayerIndex].userId
        status = Game.STATUS_GAME_IN_PROGRESS
        refreshAllPlayersPlayers()
        refreshAllPlayersGameStatus()
        refreshAllPlayersSupply()
        refreshAllPlayersPlayingArea()
        refreshAllPlayersHandArea()
        refreshAllPlayersTitle()

        if (mobile) {
            maxHistoryTurnSize = players.size
        } else {
            maxHistoryTurnSize = players.size * 2
        }

        startPlayerTurn(currentPlayer!!)

        if (players[currentPlayerIndex].isComputer) {
            Thread(
                    Runnable {
                        if (previousPlayerId != 0) {
                            try {
                                Thread.sleep(2200)
                            } catch (e: Exception) {
                                val error = GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e))
                                logError(error)
                            }

                        }
                        computerPlayers[currentPlayerId]!!.doNextAction()
                    }
            ).start()
        }
    }

    private fun checkCardName(card: Card, boughtFromBlackMarket: Boolean) {
        when(card.name) {
            "Gardens" -> isShowGardens = true
            "Farmlands" -> isShowFarmlands = true
            "Vineyard" -> isShowVineyard = true
            "Silk Road" -> isShowSilkRoads = true
            "Great Hall" -> isShowGreatHall = true
            "Harem" -> isShowHarem = true
            "Duke" -> isShowDuke = true
            "Nobles" -> isShowNobles = true
            "Archbishop" -> isShowArchbishops = true
            "Embargo" -> isShowEmbargoTokens = true
            "Island" -> isShowIslandCards = true
            "Native Village" -> isShowNativeVillage = true
            "Pirate Ship" -> isShowPirateShipCoins = true
            "Smugglers" -> trackSmugglersCards = true
            "Treasury" -> trackTreasuryCards = true
            "Secret Chamber" -> checkSecretChamber = true
            "Throne Room" -> throneRoomCard = card
            "King's Court" -> kingsCourtCard = card
            "Alchemist" -> trackAlchemistCards = true
            "Herbalist" -> trackHerbalistCards = true
            "Contraband" -> isTrackContrabandCards = true
            "Bank" -> isTrackBankCards = true
            "Peddler" -> refreshPeddler = true
            "Trade Route" -> isTrackTradeRouteTokens = true
            "Watchtower" -> checkWatchtower = true
            "Edict" -> trackEdictCards = true
            "Fairgrounds" -> isShowFairgrounds = true
            "Tournament" -> isShowPrizeCards = true
            "Museum" -> {
                isShowPrizeCards = true
                isShowMuseumCards = true
            }
            "City Planner" -> isShowCityPlannerCards = true
            "Horse Traders" -> {
                checkHorseTraders = true
                horseTradersCard = card
            }
            "Bell Tower" -> checkBellTower = true
            "Cathedral" -> isShowCathedral = true
            "Enchanted Palace" -> isCheckEnchantedPalace = true
            "Hedge Wizard" -> isShowHedgeWizard = true
            "Golden Touch" -> isShowGoldenTouch = true
            "Tinker" -> checkTinker = true
            "Bridge Troll" -> isShowTrollTokens = true
            "Walled Village" -> {
                checkWalledVillage = true
                trackActionCardsPlayed = true
            }
            "Quest" -> isCheckQuest = true
            "Trader" -> checkTrader = true
            "Highway" -> trackHighway = true
            "Goons" -> trackGoons = true
            "Duchess" -> if (!boughtFromBlackMarket) isCheckDuchess = true
            "Scheme" -> {
                checkScheme = true
                trackActionCardsPlayed = true
            }
            "Tunnel" -> checkTunnel = true
            "Haggler" -> checkHaggler = true
            "Fool's Gold" -> {
                checkFoolsGold = true
                foolsGoldCard = card
            }
            "Noble Brigand" -> checkNobleBrigand = true
            "Laborer" -> trackLaborer = true
            "Goodwill" -> trackGoodwill = true
            "Plantation" -> checkPlantation = true
        }
        if (card.isDuration) {
            isShowDuration = true
        }
        if (card.costIncludesPotion) {
            isUsePotions = true
        }
        if (card.addVictoryCoins > 0 || card.name == "Goons") {
            isShowVictoryCoins = true
        }
        if (card.isSalvation) {
            isShowSins = true
        }
        if (card.fruitTokens > 0 || card.name == "Orchard" || card.name == "Goodwill") {
            isShowFruitTokens = true
        }
        if (card.cattleTokens > 0 || card.name == "Rancher") {
            isShowCattleTokens = true
        }
    }

    fun cardClicked(player: Player, clickType: String, cardId: Int) {
        val card = cardMap[cardId]!!
        cardClicked(player, clickType, card)
    }

    @JvmOverloads
    fun cardClicked(player: Player, clickType: String, card: Card, confirm: Boolean = true) {
        if (allowClick(player)) {
            val coinsBefore = player.coins
            val potionsBefore = player.potions
            updateLastActivity()
            try {
                var actionPlayed = false
                var cardBought = false
                var treasurePlayed = false
                var leaderActivated = false
                if (clickType == "hand") {
                    if (card.isAction) {
                        if (player.hasBoughtCard()) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play an action after you have bought a card."))
                        } else if (isPlayTreasureCards && treasureCardsPlayed.size > 0) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play an action after you have played a treasure card."))
                        } else if (player.actions > 0) {
                            actionPlayed = true
                            playActionCard(player, card)
                        } else {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any actions left."))
                        }
                    } else if (isPlayTreasureCards && card.isTreasure) {
                        if (player.hasBoughtCard()) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play a treasure card after you have bought a card."))
                        } else {
                            treasurePlayed = true
                            playTreasureCard(player, card, true, true, confirm, true, false)
                        }
                    }
                } else if (clickType == "supply") {
                    cardBought = buyCard(player, card, confirm)
                } else if (clickType == "leader") {
                    leaderActivated = activateLeader(player, card.cardId)
                }

                if ((cardBought || leaderActivated) && !player.isComputer && player.buys == 0 && !player.isShowCardAction && player.extraCardActions.isEmpty() && !hasUnfinishedGainCardActions()) {
                    endPlayerTurn(player, false)
                } else {
                    if (actionPlayed || cardBought || treasurePlayed || leaderActivated) {
                        refreshAllPlayersPlayingArea()
                        refreshHandArea(player)
                    }
                    if (coinsBefore != player.coins || potionsBefore != player.potions) {
                        refreshSupply(player)
                    }
                }
            } finally {
                processingClick.remove(player.userId)
            }
        }
    }

    private fun activateLeader(player: Player, cardId: Int): Boolean {
        val card = player.getLeaderCard(cardId) ?: return false
        val cost = getCardCost(card, player, true)
        if (player.coins >= cost && player.buys > 0 && !card.isActivated && player.turns > 1) {
            if (card.isVictory) {
                boughtVictoryCard = true
            }
            if (!isPlayTreasureCards && !player.hasBoughtCard()) {
                playAllTreasureCards(player, false)
            }
            player.setHasBoughtCard(true)
            addHistory(player.username, " activated the leader ", KingdomUtil.getCardWithBackgroundColor(card))
            player.addCoins(cost * -1)
            player.addBuys(-1)
            card.isActivated = true
            player.leaderActivated(card)
            SpecialActionHandler.handleSpecialAction(this, card)
            refreshAllPlayersPlayers()
            return true
        } else {
            if (player.buys == 0) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any more buys."))
            } else if (player.turns < 2) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't activate a leader until your third turn."))
            } else if (player.coins < cost) {
                if (player.isComputer) {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins. Card: " + card.name + " Coins: " + player.coins))
                } else {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins to activate the leader."))
                }
            } else if (card.isActivated) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("Leader has already been activated."))
            }
        }
        return false
    }

    private fun buyCard(player: Player, card: Card, confirm: Boolean): Boolean {
        var cardBought = false
        if (isTrackContrabandCards && contrabandCards.contains(card)) {
            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("This card was banned by Contraband this turn."))
            return false
        } else if (!repeatedActions.isEmpty()) {
            val error = GameError(GameError.COMPUTER_ERROR, player.username + " could not buy a card because a repeated action was not completed for: " + repeatedActions.first.card.name)
            logError(error, false)
            //todo return false instead of clearing
            repeatedActions.clear()
        } else if (hasIncompleteCard()) {
            val error = GameError(GameError.COMPUTER_ERROR, player.username + " could not buy a card because there was an incomplete action for: " + incompleteCard!!.cardName)
            logError(error, false)
            //todo return false instead of removing
            removeIncompleteCard()
        }
        val cost = getCardCostBuyPhase(card)
        if (supply[card.cardId] == null) {
            println("supply card null")
        }
        val numInSupply = getNumInSupply(card)
        var missingPotion = false
        if (card.costIncludesPotion && player.potions == 0) {
            missingPotion = true
        }
        if (player.coins >= cost && player.buys > 0 && numInSupply > 0 && !missingPotion) {
            if (confirm && !player.isComputer && !player.hasBoughtCard() && (!isPlayTreasureCards || treasureCardsPlayed.isEmpty())) {
                if (isPlayTreasureCards && !player.treasureCards.isEmpty()) {
                    val confirmBuyCardAction = CardAction(CardAction.TYPE_YES_NO)
                    confirmBuyCardAction.cardName = "Confirm Buy"
                    confirmBuyCardAction.cards.add(card)
                    confirmBuyCardAction.instructions = "You haven't played any treasure cards, are you sure you want to buy this card?"
                    setPlayerCardAction(player, confirmBuyCardAction)
                    return false
                } else if (player.actions > 0 && player.actionCards.size > 0) {
                    var confirmBuy = true
                    if (player.actionCards.size == 1) {
                        val actionCard = player.actionCards[0]
                        if (actionCard.name == "Throne Room" || actionCard.name == "King's Court" || actionCard.name == "Monk" || actionCard.name == "Chapel") {
                            confirmBuy = false
                        }
                    }
                    if (confirmBuy) {
                        val confirmBuyCardAction = CardAction(CardAction.TYPE_YES_NO)
                        confirmBuyCardAction.cardName = "Confirm Buy"
                        confirmBuyCardAction.cards.add(card)
                        confirmBuyCardAction.instructions = "You still have actions remaining, are you sure you want to buy this card?"
                        setPlayerCardAction(player, confirmBuyCardAction)
                        return false
                    }
                }
            }
            if (card.name == "Grand Market") {
                for (treasureCard in treasureCardsPlayed) {
                    if (treasureCard.cardId == Card.COPPER_ID) {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't buy this card when you have a Copper in play."))
                        return false
                    }
                }
            }
            if (!isPlayTreasureCards && !player.hasBoughtCard()) {
                playAllTreasureCards(player, false)
            }
            cardBought = true
            if (card.isVictory) {
                boughtVictoryCard = true
            }
            player.setHasBoughtCard(true)
            addHistory(player.username, " bought ", KingdomUtil.getArticleWithCardName(card))
            player.addCoins(cost * -1)
            player.addBuys(-1)
            if (card.costIncludesPotion) {
                if (!isPlayTreasureCards) {
                    potionsPlayed++
                }
                player.addPotions(-1)
            }
            if (goonsCardsPlayed > 0) {
                player.addVictoryCoins(goonsCardsPlayed)
                refreshAllPlayersPlayers()
                addHistory(player.username, " gained ", KingdomUtil.getPlural(goonsCardsPlayed, "Victory Coin"), " from ", KingdomUtil.getWordWithBackgroundColor("Goons", Card.ACTION_COLOR))
            }
            cardsBought.add(card)
            if (card.name == "Mint") {
                for (treasureCard in treasureCardsPlayed) {
                    cardsPlayed.remove(treasureCard)
                    trashedCards.add(treasureCard)
                }
                treasureCardsPlayed.clear()
                hoardCardsPlayed = 0
                talismanCardsPlayed = 0
                addHistory("The Mint trashed all the treasure cards played by ", player.username)
            }
            playerGainedCard(player, card, "discard", true, true)
            if (card.isVictory && hoardCardsPlayed > 0) {
                var goldsToGain = hoardCardsPlayed
                val goldsInSupply = getNumInSupply(Card.GOLD_ID)
                if (goldsInSupply < goldsToGain) {
                    goldsToGain = goldsInSupply
                }
                if (goldsToGain > 0) {
                    for (i in 0 until goldsToGain) {
                        playerGainedCard(player, goldCard)
                    }
                }
            }
            if (!card.isVictory && talismanCardsPlayed > 0 && cost <= 4) {
                var cardsGained = 0
                while (isCardInSupply(card) && cardsGained < talismanCardsPlayed) {
                    cardsGained++
                    playerGainedCard(player, card)
                }
            }
            if (trackGoodwill && goodwillCardsInPlay > 0) {
                player.addFruitTokens(goodwillCardsInPlay)
                addHistory(player.username, " gained ", KingdomUtil.getPlural(goodwillCardsInPlay, "fruit token"))
            }
            if (isShowEmbargoTokens) {
                val numEmbargoTokens = embargoTokens[card.cardId]!!
                if (numEmbargoTokens > 0) {
                    var curseCardsGained = 0
                    for (i in 0 until numEmbargoTokens) {
                        val cursesInSupply = getNumInSupply(Card.CURSE_ID)
                        if (cursesInSupply > 0) {
                            playerGainedCard(player, curseCard)
                            curseCardsGained++
                        }
                    }
                    if (curseCardsGained > 0) {
                        refreshDiscard(player)
                    }
                }
            }
        } else {
            if (player.buys == 0) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any more buys."))
            } else if (player.coins < cost) {
                if (isPlayTreasureCards && player.coinsInHand > 0) {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You need to play your treasure cards."))
                } else {
                    if (player.isComputer) {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins. Card: " + card.name + " Coins: " + player.coins))
                    } else {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins."))
                    }
                }
            } else if (missingPotion) {
                if (player.isComputer) {
                    val error = GameError(GameError.COMPUTER_ERROR, player.username + " needs a potion to buy " + card.name)
                    logError(error, false)
                } else {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You need a potion to buy this card."))
                }
            }
        }
        return cardBought
    }

    fun boughtBlackMarketCard(card: Card) {
        checkCardName(card, true)
        val cost = getCardCost(card)
        currentPlayer!!.addCoins(cost * -1)
        refreshAllPlayersHandArea()
    }

    private fun addCardBonuses(player: Player, card: Card) {
        if (card.addActions != 0) {
            player.addActions(card.addActions)
        }
        if (card.addBuys != 0) {
            player.addBuys(card.addBuys)
        }
        if (card.addCoins != 0) {
            player.addCoins(card.addCoins)
        }
        if (card.addCards != 0) {
            player.drawCards(card.addCards)
        }
        if (card.addVictoryCoins != 0) {
            player.addVictoryCoins(card.addVictoryCoins)
            refreshAllPlayersPlayers()
        }
        if (card.sins != 0) {
            player.addSins(card.sins)
            refreshAllPlayersPlayers()
        }
        if (card.isPotion) {
            player.addPotions(1)
        }
        if (card.fruitTokens != 0) {
            player.addFruitTokens(card.fruitTokens)
        }
        if (card.cattleTokens != 0) {
            player.addCattleTokens(card.cattleTokens)
        }
    }

    private fun actionCardPlayed(player: Player, card: Card, repeatedAction: Boolean = false) {
        addCardBonuses(player, card)
        if (card.name == "Coppersmith") {
            player.copperSmithPlayed()
        } else if (trackGoons && card.name == "Goons" && !repeatedAction) {
            goonsCardsPlayed++
        } else if (checkHaggler && card.name == "Haggler" && !repeatedAction) {
            hagglerCardsInPlay++
        } else if (checkScheme && card.name == "Scheme") {
            schemeCardsPlayed++
        } else if (card.name == "Crossroads") {
            crossroadsPlayed++
        } else if (trackHighway && card.name == "Highway" && !repeatedAction) {
            highwayCardsInPlay++
            refreshAllPlayersSupply()
        } else if (trackLaborer && card.name == "Laborer" && !repeatedAction) {
            laborerCardsInPlay++
            if (laborerCardsInPlay >= 2) {
                player.drawCards(1)
                refreshHand(player)
                refreshAllPlayersCardsBought()
                addHistory(player.username, " gained +1 card")
            }
        }
        if (trackEdictCards && edictCards.contains(card)) {
            player.addSins(1)
            refreshAllPlayersPlayers()
            addHistory(player.username, " gained 1 sin from an ", KingdomUtil.getWordWithBackgroundColor("Edict", Card.ACTION_DURATION_COLOR))
        }
        if (trackTreasuryCards && card.name == "Treasury") {
            playedTreasuryCard = true
        }
        if (trackAlchemistCards && card.name == "Alchemist") {
            playedAlchemistCard = true
        }
        if (trackHerbalistCards && card.name == "Herbalist") {
            playedHerbalistCard = true
        }
        if (checkWalledVillage && card.name == "Walled Village") {
            playedWalledVillage = true
        }
        if (card.isDuration) {
            if (card.name == "Lighthouse") {
                lighthousePlayed = true
            } else if (card.name == "Outpost") {
                outpostCardPlayed = true
            }
        }
        if ((checkSecretChamber || checkHorseTraders || checkBellTower || isCheckEnchantedPalace) && card.isAttack) {
            attackCard = card
            if (checkHorseTraders) {
                addNextAction("check horse traders")
            }
            if (checkSecretChamber) {
                addNextAction("check secret chamber")
            }
            if (checkBellTower) {
                addNextAction("check bell tower")
            }
            if (isCheckEnchantedPalace) {
                enchantedPalaceRevealed.clear()
                addNextAction("check enchanted palace")
            }
            addNextAction("finish attack")
            NextActionHandler.handleAction(this, "reaction")

            if (hasIncompleteCard()) {
                incompleteCard!!.actionFinished(player)
            }
        } else if (card.isSpecialCard) {
            SpecialActionHandler.handleSpecialAction(this, card, repeatedAction)
        }
    }

    private fun playActionCard(player: Player, card: Card) {
        val cardCopy: Card
        if (isCheckQuest && !card.isCopied && card.name == "Quest") {
            cardCopy = Card(card)
            copiedPlayedCard = true
        } else {
            cardCopy = card
        }
        numActionsCardsPlayed++
        if (trackActionCardsPlayed) {
            actionCardsPlayed.add(card)
        }
        actionCardsInPlay++
        addHistory(player.username, " played ", KingdomUtil.getArticleWithCardName(card))
        cardsPlayed.add(cardCopy)
        player.removeCardFromHand(card)
        player.addActions(-1)
        if (card.isDuration) {
            durationCardsPlayed.add(cardCopy)
        }
        actionCardPlayed(player, cardCopy)
        refreshAllPlayersPlayingArea()
        if (refreshPeddler) {
            refreshAllPlayersSupply()
        }
    }

    @JvmOverloads
    fun playTreasureCard(player: Player, card: Card, removeFromHand: Boolean, playSpecialAction: Boolean, confirmPlay: Boolean = true, showHistory: Boolean = true, blackMarketTreasure: Boolean = false) {
        var confirm = confirmPlay
        if (confirm && !player.isComputer && player.actions > 0 && player.actionCards.size > 0 && !isBuyPhase) {
            if (player.actionCards.size == 1) {
                val actionCard = player.actionCards[0]
                if (actionCard.name == "Throne Room" || actionCard.name == "King's Court" || actionCard.name == "Monk" || actionCard.name == "Chapel") {
                    confirm = false
                }
            }
            if (confirm) {
                val confirmPlayTreasureCard = CardAction(CardAction.TYPE_YES_NO)
                confirmPlayTreasureCard.cards.add(card)
                confirmPlayTreasureCard.cardName = "Confirm Play Treasure Card"
                confirmPlayTreasureCard.instructions = "You still have actions remaining, are you sure you want to play this Treasure card?"
                setPlayerCardAction(player, confirmPlayTreasureCard)
                return
            }
        }

        val cardCopy: Card
        when(card.name) {
            "Philosopher's Stone" -> {
                cardCopy = Card(card)
                copiedPlayedCard = true
                cardCopy.addCoins = player.philosophersStoneCoins
            }
            "Bank" -> {
                cardCopy = Card(card)
                copiedPlayedCard = true
                cardCopy.addCoins = treasureCardsPlayed.size + 1
            }
            "Storybook" -> {
                cardCopy = Card(card)
                copiedPlayedCard = true
            }
            else -> cardCopy = card
        }

        when(card.name) {
            "Hoard" -> hoardCardsPlayed++
            "Talisman" -> talismanCardsPlayed++
            "Royal Seal" -> royalSealCardPlayed = true
            "Goodwill" -> goodwillCardsInPlay++
        }

        if (blackMarketTreasure) {
            blackMarketTreasureCardsPlayed.add(cardCopy)
        } else {
            treasureCardsPlayed.add(cardCopy)
        }

        cardsPlayed.add(cardCopy)

        if (cardCopy.isPotion) {
            potionsPlayed++
        }

        addCardBonuses(player, cardCopy)

        player.treasureCardPlayed(cardCopy, removeFromHand)

        if (showHistory) {
            addHistory(player.username, " played ", KingdomUtil.getArticleWithCardName(cardCopy))
        }

        if (playSpecialAction && cardCopy.special != "") {
            TreasureCardsSpecialActionHandler.handleSpecialAction(this, cardCopy)
            if (hasIncompleteCard()) {
                incompleteCard!!.actionFinished(player)
            }
        }

        refreshAllPlayersPlayingArea()
    }

    @JvmOverloads
    fun playAllTreasureCards(player: Player, confirmPlay: Boolean = true) {
        var confirm = confirmPlay

        updateLastActivity()

        if (player.hasBoughtCard()) {
            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play a treasure card after you have bought a card."))
        } else {
            if (!confirm || allowClick(player)) {
                try {
                    if (confirm && !player.isComputer && player.actions > 0 && player.actionCards.size > 0 && !isBuyPhase) {
                        if (player.actionCards.size == 1) {
                            val actionCard = player.actionCards[0]
                            if (actionCard.name == "Throne Room" || actionCard.name == "King's Court" || actionCard.name == "Monk" || actionCard.name == "Chapel") {
                                confirm = false
                            }
                        }
                        if (confirm) {
                            val confirmPlayTreasureCards = CardAction(CardAction.TYPE_YES_NO)
                            confirmPlayTreasureCards.cardName = "Confirm Play Treasure Cards"
                            confirmPlayTreasureCards.instructions = "You still have actions remaining, are you sure you want to play your Treasure cards?"
                            setPlayerCardAction(player, confirmPlayTreasureCards)
                            return
                        }
                    }
                    if (player.userId == currentPlayerId && !player.treasureCards.isEmpty()) {
                        val treasureCardsPlayed = ArrayList<Card>()
                        val cards = ArrayList(player.treasureCards)

                        for (card in cards) {
                            if (card.isAutoPlayTreasure) {
                                treasureCardsPlayed.add(card)
                            }
                        }

                        if (!treasureCardsPlayed.isEmpty()) {
                            addHistory(player.username, " played ", KingdomUtil.groupCards(treasureCardsPlayed, true))
                            for (card in treasureCardsPlayed) {
                                playTreasureCard(player, card, true, true, false, false, false)
                            }
                        }

                        refreshHandArea(player)
                        refreshSupply(player)
                    }
                } finally {
                    processingClick.remove(player.userId)
                }
            }
        }
    }

    fun playRepeatedAction(player: Player, firstAction: Boolean) {
        val repeatedAction = repeatedActions.pop()
        val card = repeatedAction.card
        numActionsCardsPlayed++
        if (repeatedAction.isFirstAction) {
            actionCardsInPlay++
            cardsPlayed.add(card)
            player.removeCardFromHand(card)
            if (card.isDuration) {
                durationCardsPlayed.add(card)
            }
        }
        actionCardPlayed(player, card, !firstAction)
        if (!card.hasSpecial() && !repeatedActions.isEmpty()) {
            playRepeatedAction(player, false)
        }
        refreshAllPlayersPlayingArea()
        if (refreshPeddler) {
            refreshAllPlayersSupply()
        }
    }

    fun playGolemActionCard(player: Player?) {
        val card = golemActions.pop()
        addHistory(player!!.username, "'s Golem played ", KingdomUtil.getArticleWithCardName(card))
        cardsPlayed.add(card)
        numActionsCardsPlayed++
        if (trackActionCardsPlayed) {
            actionCardsPlayed.add(card)
        }
        actionCardsInPlay++
        if (card.isDuration) {
            durationCardsPlayed.add(card)
        }
        actionCardPlayed(player, card)
        if (!card.hasSpecial() && !golemActions.isEmpty()) {
            playGolemActionCard(player)
        }
        refreshAllPlayersPlayingArea()
    }

    fun takeFromSupply(cardId: Int): Boolean {
        if (supply[cardId] != null) {
            val numInSupply = getNumInSupply(cardId) - 1
            if (numInSupply < 0) {
                return false
            }
            supply[cardId] = numInSupply
            if (numInSupply == 0) {
                val card = supplyMap[cardId]!!
                emptyPiles.add(card)
                if (card.cost == 2 && kingdomCards.contains(card)) {
                    twoCostKingdomCards--
                }
                if (numPlayers < 5 && emptyPiles.size == 3 || emptyPiles.size == 4 || cardId == Card.PROVINCE_ID || cardId == Card.COLONY_ID) {
                    finishGameOnNextEndTurn = true
                    if (cardId == Card.PROVINCE_ID) {
                        gameEndReason = "Province pile gone"
                    } else if (cardId == Card.COLONY_ID) {
                        gameEndReason = "Colony pile gone"
                    } else {
                        gameEndReason = emptyPiles.size.toString() + " piles empty (" + KingdomUtil.getCardNames(emptyPiles) + ")"
                    }
                }
            }
            refreshAllPlayersSupply()
            return true
        }
        return false
    }

    fun buyingCardWillEndGame(cardId: Int): Boolean {
        if (supply[cardId] != null) {
            val numInSupply = getNumInSupply(cardId) - 1
            if (numInSupply < 0) {
                return false
            }
            if (numInSupply == 0) {
                val numEmptyPiles = emptyPiles.size + 1
                if (numPlayers < 5 && numEmptyPiles == 3 || numEmptyPiles == 4 || cardId == Card.PROVINCE_ID || cardId == Card.COLONY_ID) {
                    return true
                }
            }
        }
        return false
    }

    fun addEmbargoToken(cardId: Int) {
        val numTokens = embargoTokens[cardId]!! + 1
        embargoTokens[cardId] = numTokens
        refreshAllPlayersSupply()
    }

    fun addTrollToken(cardId: Int) {
        val numTokens = trollTokens[cardId]!! + 1
        trollTokens[cardId] = numTokens
        refreshAllPlayersSupply()
    }

    fun addToSupply(cardId: Int) {
        if (supply[cardId] == null) {
            val error = GameError(GameError.COMPUTER_ERROR, "Supply does not have an entry for cardId: " + cardId)
            logError(error, false)
        }
        val numInSupply = getNumInSupply(cardId) + 1
        supply[cardId] = numInSupply
        if (numInSupply == 1) {
            val card = supplyMap[cardId]!!
            emptyPiles.remove(card)
            if (card.cost == 2 && kingdomCards.contains(card)) {
                twoCostKingdomCards++
            }
        }
        refreshAllPlayersSupply()
    }

    private fun addSmugglersCard(card: Card) {
        if (card.cost <= 6) {
            smugglersCardsGained.add(card)
        }
    }

    private fun checkTradeRouteToken(card: Card) {
        val hasTradeRouteToken = tradeRouteTokenMap[card.cardId]
        if (hasTradeRouteToken != null && hasTradeRouteToken) {
            tradeRouteTokensOnMat++
            addHistory("Trade Route Mat now has ", KingdomUtil.getPlural(tradeRouteTokensOnMat, "Token"))
            tradeRouteTokenMap[card.cardId] = false
        }
    }

    fun isCurrentPlayer(player: Player): Boolean {
        return player.userId == currentPlayerId
    }

    fun playerLostCard(player: Player, card: Card) {
        if (player.userId == currentPlayerId && card.addCoins != 0) {
            refreshAllPlayersCardsBought()
        }
        if (victoryPointsNeedRefresh(card)) {
            refreshAllPlayersPlayers()
        }
    }

    @JvmOverloads
    fun playerGainedCard(player: Player, card: Card, takeFromSupply: Boolean = true) {
        playerGainedCard(player, card, "discard", takeFromSupply, false)
    }

    @JvmOverloads
    fun playerGainedCardToHand(player: Player, card: Card, takeFromSupply: Boolean = true) {
        playerGainedCard(player, card, "hand", takeFromSupply, false)
    }

    @JvmOverloads
    fun playerGainedCardToTopOfDeck(player: Player, card: Card, takeFromSupply: Boolean = true) {
        playerGainedCard(player, card, "deck", takeFromSupply, false)
    }

    fun victoryPointsNeedRefresh(card: Card?): Boolean {
        if (card == null) {
            val error = GameError(GameError.GAME_ERROR, "victoryPointsNeedRefresh - card was null")
            logError(error, false)
            return false
        }
        return card.isVictory || card.isCurse || isShowGardens || isShowFarmlands || isShowFairgrounds || isShowVineyard && card.isAction || isShowCathedral && card.isSalvation
    }

    fun playerGainedCard(player: Player, card: Card, cardDestination: String, takeFromSupply: Boolean, gainedFromBuy: Boolean) {
        var destination = cardDestination
        if (card.isCopied && !card.isCardNotGained) {
            if (!card.gainCardActions.isEmpty()) {
                waitIfNotCurrentPlayer(player)
                setPlayerGainCardAction(player, card)
            } else {
                gainCardFinished(player, cardMap[card.cardId]!!)
            }
        } else {
            val cardCopy: Card
            if (card.isCopied) {
                cardCopy = card
            } else {
                cardCopy = Card(card)
            }
            if (gainedFromBuy) {
                cardCopy.isGainedFromBuy = true
                if (checkNobleBrigand && card.name == "Noble Brigand") {
                    BuySpecialActionHandler.setNobleBrigandCardAction(this, player)
                }
                val buyCardActions = ArrayList<CardAction>(0)
                val buyCardAction = BuySpecialActionHandler.getCardAction(this, player, cardCopy)
                if (buyCardAction != null) {
                    buyCardActions.add(buyCardAction)
                }
                if (checkHaggler) {
                    var numTimesToGainHagglerBonus = hagglerCardsInPlay
                    while (numTimesToGainHagglerBonus > 0) {
                        val hagglerCardAction = BuySpecialActionHandler.getHagglerCardAction(this, cardCopy)
                        if (hagglerCardAction != null) {
                            numTimesToGainHagglerBonus--
                            buyCardActions.add(hagglerCardAction)
                        } else {
                            break
                        }
                    }
                }
                if (!buyCardActions.isEmpty()) {
                    cardCopy.isCardNotGained = true
                    buyCardActions[buyCardActions.size - 1].isGainCardAfterBuyAction = true
                    for (cardAction in buyCardActions) {
                        setPlayerCardAction(player, cardAction)
                    }
                    return
                }
            }
            if (checkTrader && !card.isTraderProcessed && player.hasTrader() && (!card.isSilver || destination != "discard" || !takeFromSupply)) {
                cardCopy.isTraderProcessed = true
                cardCopy.isCardNotGained = true
                val cardAction = GainCardsReactionHandler.getCardAction("Trader", this, player, cardCopy, destination)
                if (cardAction != null) {
                    waitIfNotCurrentPlayer(player)
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            if (trackSmugglersCards && player.userId == currentPlayerId) {
                addSmugglersCard(card)
            }
            if (isTrackTradeRouteTokens) {
                checkTradeRouteToken(card)
            }
            if (takeFromSupply) {
                takeFromSupply(card.cardId)
                refreshAllPlayersSupply()
            }
            if (card.name == "Nomad Camp") {
                destination = "deck"
            }
            cardCopy.isCardNotGained = false
            addCardToDestination(player, card, destination)
            if (!gainedFromBuy && !card.isGainedFromBuy) {
                addGainedCardToDestinationHistory(player, card, destination)
            }
            if (player.isComputer) {
                val computerPlayer = computerPlayers[player.userId]!!
                computerPlayer.gainedCard(card)
            }
            if (victoryPointsNeedRefresh(card)) {
                refreshAllPlayersPlayers()
            }
            cardCopy.destination = destination
            setGainedCardActions(player, cardCopy, destination)
            if (!cardCopy.gainCardActions.isEmpty()) {
                waitIfNotCurrentPlayer(player)
                setPlayerGainCardAction(player, cardCopy)
            } else {
                gainCardFinished(player, card)
            }
            if (checkFoolsGold && card.isProvince) {
                val foolsGoldCardAction = GainCardsSpecialActionHandler.foolsGoldCardAction
                for (p in players) {
                    if (!isCurrentPlayer(p) && p.hasFoolsGoldInHand()) {
                        waitIfNotCurrentPlayer(p)
                        for (i in 0 until p.foolsGoldInHand) {
                            setPlayerCardAction(p, foolsGoldCardAction)
                        }
                    }
                }
            }
        }
    }

    private fun addGainedCardToDestinationHistory(player: Player, card: Card, destination: String) {
        when (destination) {
            "hand" -> addHistory(player.username, " gained ", KingdomUtil.getArticleWithCardName(card), " into ", player.pronoun, " hand")
            "deck" -> addHistory(player.username, " gained ", KingdomUtil.getArticleWithCardName(card), " on top of ", player.pronoun, " deck")
            "discard" -> addHistory(player.username, " gained ", KingdomUtil.getArticleWithCardName(card))
        }
    }

    private fun addCardToDestination(player: Player, card: Card, destination: String) {
        when (destination) {
            "hand" -> {
                if (player.userId == currentPlayerId && card.addCoins != 0) {
                    refreshAllPlayersCardsBought()
                }
                player.addCardToHand(card)
            }
            "deck" -> player.addCardToTopOfDeck(card)
            "discard" -> player.addCardToDiscard(card)
        }
    }

    fun gainCardFinished(player: Player, card: Card) {
        if (!player.isShowCardAction && hasUnfinishedGainCardActions()) {
            if (!card.gainCardActions.isEmpty()) {
                setPlayerGainCardAction(player, card)
            } else {
                setPlayerGainCardAction(player, cardWithUnfinishedGainCardActions)
            }
        }
    }

    fun moveGainedCard(player: Player, card: Card, destination: String) {
        removeGainedCard(player, card)
        addCardToDestination(player, cardMap[card.cardId]!!, destination)
        gainCardFinished(player, card)
    }

    private fun removeGainedCard(player: Player, gainedCard: Card) {
        when(gainedCard.destination) {
            "discard" -> player.discard.removeLastOccurrence(gainedCard)
            "deck" -> player.deck.remove(gainedCard)
            "tinker" -> player.tinkerCards.remove(gainedCard)
            "hand" -> player.hand.remove(gainedCard)
        }
    }

    private fun waitIfNotCurrentPlayer(player: Player) {
        if (!isCurrentPlayer(player)) {
            playersWithCardActions.add(player.userId)
            if (!hasIncompleteCard() && !currentPlayer!!.isShowCardAction) {
                setPlayerCardAction(currentPlayer!!, CardAction.waitingForPlayersCardAction)
            }
        }
    }

    fun setPlayerGainCardAction(player: Player, card: Card) {
        val firstReaction = card.gainCardActions.values.iterator().next()
        if (card.gainCardActions.size == 1) {
            card.gainCardActions.clear()
            cardsWithGainCardActions.remove(card.cardId)
            setPlayerCardAction(player, firstReaction)
        } else {
            cardsWithGainCardActions[card.cardId] = card
            val cardAction = CardAction(CardAction.TYPE_CHOICES)
            cardAction.deck = Deck.Reaction
            cardAction.cardName = "Choose Reaction"
            cardAction.associatedCard = firstReaction.associatedCard
            cardAction.cards.add(firstReaction.associatedCard!!)
            cardAction.destination = firstReaction.destination
            cardAction.instructions = "Choose which card you want to process first to react to gaining this card."
            for (action in card.gainCardActions.keys) {
                cardAction.choices.add(CardActionChoice(action, action))
            }
            setPlayerCardAction(player, cardAction)
        }
    }

    private fun setGainedCardActions(player: Player, cardCopy: Card, destination: String) {
        val gainCardActions = getGainCardActions(player, cardCopy, destination)
        cardCopy.gainCardActions = gainCardActions
    }

    private fun getGainCardActions(player: Player, cardCopy: Card, destination: String): MutableMap<String, CardAction> {
        val gainCardActions = HashMap<String, CardAction>()
        if (royalSealCardPlayed && player.userId == currentPlayerId && destination != "deck") {
            val cardAction = GainCardsReactionHandler.getCardAction("Royal Seal", this, player, cardCopy, destination)
            if (cardAction != null) {
                gainCardActions["Royal Seal"] = cardAction
            }
        }
        if (checkWatchtower && player.hasWatchtower()) {
            val cardAction = GainCardsReactionHandler.getCardAction("Watchtower", this, player, cardCopy, destination)
            if (cardAction != null) {
                gainCardActions["Watchtower"] = cardAction
            }
        }
        if (checkTinker && player.isPlayedTinker) {
            val cardAction = GainCardsReactionHandler.getCardAction("Tinker", this, player, cardCopy, destination)
            if (cardAction != null) {
                gainCardActions["Tinker"] = cardAction
            }
        }
        val cardActionForCard = GainCardsSpecialActionHandler.getCardAction(this, player, cardCopy)
        if (cardActionForCard != null) {
            gainCardActions[cardCopy.name] = cardActionForCard
        }
        return gainCardActions
    }

    @JvmOverloads
    fun endPlayerTurn(player: Player, confirm: Boolean = true) {
        if (allowEndTurn()) {
            endTurn(player, confirm)
            isEndingTurn = false
        }
    }

    @Synchronized
    fun allowEndTurn(): Boolean {
        if (!isEndingTurn) {
            isEndingTurn = true
            return true
        }
        return false
    }

    private fun endTurn(player: Player, confirm: Boolean) {
        updateLastActivity()
        if (player.userId == currentPlayerId) {
            if (hasIncompleteCard()) {
                val error = GameError(GameError.COMPUTER_ERROR, player.username + " could not end turn because there was an incomplete action for: " + incompleteCard!!.cardName)
                logError(error, false)
                removeIncompleteCard()
            }
            var coins = player.coins
            if (isPlayTreasureCards) {
                coins += player.coinsInHand
            }
            if (confirm && !player.isComputer && player.buys > 0 && (coins > 2 || coins == 2 && twoCostKingdomCards > 0)) {
                val confirmEndTurn = CardAction(CardAction.TYPE_YES_NO)
                confirmEndTurn.cardName = "Confirm End Turn"
                confirmEndTurn.instructions = "You still have buys remaining, are you sure you want to end your turn?"
                setPlayerCardAction(player, confirmEndTurn)
                return
            }
            if (playedWalledVillage && actionCardsInPlay <= 2) {
                playedWalledVillage = false
                val walledVillagesPlayed = ArrayList<Card>(1)
                for (card in actionCardsPlayed) {
                    if (card.name == "Walled Village") {
                        walledVillagesPlayed.add(card)
                    }
                }
                if (walledVillagesPlayed.size == 1) {
                    val cardAction = CardAction(CardAction.TYPE_YES_NO)
                    cardAction.deck = Deck.Promo
                    cardAction.cardName = "Walled Village"
                    cardAction.instructions = "Do you want your Walled Village to go on top of your deck?"
                    cardAction.cards.addAll(walledVillagesPlayed)
                    setPlayerCardAction(player, cardAction)
                    return
                } else if (walledVillagesPlayed.size > 1) {
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    cardAction.deck = Deck.Promo
                    cardAction.cardName = "Walled Village"
                    for (card in walledVillagesPlayed) {
                        card.isAutoSelect = true
                        cardAction.cards.add(card)
                    }
                    cardAction.numCards = cardAction.cards.size
                    cardAction.instructions = "Both Walled Villages played have been selected. Click on any that you do not want to go on top of your deck and then click Done."
                    cardAction.buttonValue = "Done"
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            if (playedTreasuryCard) {
                playedTreasuryCard = false
                if (!boughtVictoryCard) {
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    cardAction.deck = Deck.Seaside
                    cardAction.cardName = "Treasury"
                    for (card in cardsPlayed) {
                        if (card.name == "Treasury") {
                            card.isAutoSelect = true
                            cardAction.cards.add(card)
                        }
                    }
                    cardAction.numCards = cardAction.cards.size
                    cardAction.instructions = "All Treasuries played have been selected. Click on any that you do not want to go on top of your deck and then click Done."
                    cardAction.buttonValue = "Done"
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            if (playedAlchemistCard) {
                playedAlchemistCard = false
                val hasPotion: Boolean
                if (isPlayTreasureCards) {
                    hasPotion = potionsPlayed > 0
                } else {
                    hasPotion = player.potions > 0 || potionsPlayed > 0
                }
                if (hasPotion) {
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = "Alchemist"
                    for (card in cardsPlayed) {
                        if (card.name == "Alchemist") {
                            card.isAutoSelect = true
                            cardAction.cards.add(card)
                        }
                    }
                    cardAction.numCards = cardAction.cards.size
                    cardAction.instructions = "All Alchemists played have been selected. Click on any that you do not want to go on top of your deck and then click Done."
                    cardAction.buttonValue = "Done"
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            if (playedHerbalistCard) {
                playedHerbalistCard = false
                val treasureCards = HashSet(treasureCardsPlayed)
                if (treasureCards.size > 0) {
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    cardAction.deck = Deck.Alchemy
                    cardAction.cardName = "Herbalist"
                    var herbalistCardsPlayed = 0
                    for (card in cardsPlayed) {
                        if (card.name == "Herbalist") {
                            herbalistCardsPlayed++
                        }
                    }
                    var numCards = herbalistCardsPlayed
                    if (treasureCards.size < herbalistCardsPlayed) {
                        numCards = treasureCards.size
                    }
                    cardAction.numCards = numCards
                    cardAction.cards.addAll(treasureCards)
                    cardAction.instructions = "Select up to " + KingdomUtil.getPlural(numCards, "Treasure card") + " to go back on top of your deck and then click Done"
                    cardAction.buttonValue = "Done"
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            if (checkScheme && schemeCardsPlayed > 0) {
                if (!actionCardsPlayed.isEmpty()) {
                    var numCards = schemeCardsPlayed
                    if (actionCardsPlayed.size < schemeCardsPlayed) {
                        numCards = actionCardsPlayed.size
                    }
                    schemeCardsPlayed = 0
                    val cardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    cardAction.deck = Deck.Hinterlands
                    cardAction.cardName = "Scheme"
                    cardAction.numCards = numCards
                    cardAction.cards.addAll(actionCardsPlayed)
                    cardAction.instructions = "Select up to " + KingdomUtil.getPlural(numCards, "Action card") + " to go back on top of your deck and then click Done"
                    cardAction.buttonValue = "Done"
                    setPlayerCardAction(player, cardAction)
                    return
                }
            }
            val takeOutpostTurn = outpostCardPlayed && !outpostTurn
            if (trackSmugglersCards && !takeOutpostTurn) {
                smugglersCards.clear()
                smugglersCards.addAll(smugglersCardsGained)
                smugglersCardsGained.clear()
            }
            if (isShowDuration) {
                for (card in durationCardsPlayed) {
                    cardsPlayed.remove(card)
                }
            }
            if (copiedPlayedCard) {
                for (card in cardsPlayed) {
                    if (card.isCopied) {
                        player.addCardToDiscard(cardMap[card.cardId]!!)
                        if (card.name == "Storybook") {
                            player.discard.addAll(card.associatedCards)
                        }
                    } else {
                        player.addCardToDiscard(card)
                    }
                }
            } else {
                player.discard.addAll(cardsPlayed)
            }
            player.discard.addAll(player.durationCards)
            if (trackEdictCards) {
                edictCards.removeAll(player.lastTurnEdictCards)
                player.lastTurnEdictCards.clear()
                player.lastTurnEdictCards.addAll(player.edictCards)
                player.edictCards.clear()
            }
            player.durationCards.clear()
            player.durationCards.addAll(durationCardsPlayed)
            if (takeOutpostTurn) {
                player.endTurn(3)
            } else {
                player.endTurn(5)
            }
            if (lighthousePlayed) {
                player.setHasLighthouse(true)
                lighthousePlayed = false
            }
            refreshHandArea(player)
            refreshSupply(player)
            if (!takeOutpostTurn) {
                previousPlayerId = currentPlayerId
                refreshEndTurn(currentPlayerId)
                currentPlayerIndex = nextPlayerIndex
                currentPlayerId = players[currentPlayerIndex].userId
            }
            if (historyEntriesAddedThisTurn == 0) {
                addHistory(player.username, " ended ", player.pronoun, " turn without doing anything")
            }
            if (finishGameOnNextEndTurn) {
                determineWinner()
                status = STATUS_GAME_FINISHED
                refreshAllPlayersGameStatus()
                refreshAllPlayersTitle()
                return
            }

            if (hasNextAction()) {
                for (nextAction in nextActionQueue) {
                    val error = GameError(GameError.COMPUTER_ERROR, player.username + " game has next action on end turn: " + nextAction)
                    logError(error, false)
                }
                nextActionQueue.clear()
            }

            resetTurnVariables()

            prepareNextPlayerTurn(takeOutpostTurn)

            processingClick.remove(player.userId)
        }
    }

    private fun resetTurnVariables() {
        isGainTournamentBonus = false
        historyEntriesAddedThisTurn = 0
        goonsCardsPlayed = 0
        hagglerCardsInPlay = 0
        schemeCardsPlayed = 0
        hoardCardsPlayed = 0
        talismanCardsPlayed = 0
        royalSealCardPlayed = false
        copiedPlayedCard = false
        boughtVictoryCard = false
        previousPlayerCardsPlayed.clear()
        previousPlayerCardsBought.clear()
        previousPlayerCardsPlayed.addAll(cardsPlayed)
        previousPlayerCardsBought.addAll(cardsBought)
        cardsPlayed.clear()
        cardsBought.clear()
        repeatedActions.clear()
        golemActions.clear()
        trashedTreasureCards.clear()
        durationCardsPlayed.clear()
        treasureCardsPlayed.clear()
        contrabandCards.clear()
        potionsPlayed = 0
        princessCardPlayed = false
        numActionsCardsPlayed = 0
        playersWithCardActions.clear()
        if (trackActionCardsPlayed) {
            actionCardsPlayed.clear()
        }
        actionCardsInPlay = 0
        crossroadsPlayed = 0
        highwayCardsInPlay = 0
        laborerCardsInPlay = 0
        goodwillCardsInPlay = 0
        fruitTokensPlayed = 0
        cardsWithGainCardActions.clear()
    }

    private fun prepareNextPlayerTurn(takeOutpostTurn: Boolean) {
        if (costDiscount > 0 || actionCardDiscount > 0) {
            refreshAllPlayersSupply()
            refreshAllPlayersHandArea()
        }
        costDiscount = 0
        actionCardDiscount = 0

        val nextPlayer = currentPlayer!!
        startPlayerTurn(nextPlayer)

        if (isShowDuration) {
            if (checkTinker) {
                nextPlayer.isPlayedTinker = false
                if (!nextPlayer.tinkerCards.isEmpty()) {
                    for (card in nextPlayer.tinkerCards) {
                        nextPlayer.addCardToHand(card)
                    }
                    addHistory(nextPlayer.username, " added ", KingdomUtil.groupCards(nextPlayer.tinkerCards, true), " from ", nextPlayer.pronoun, " ", KingdomUtil.getWordWithBackgroundColor("Tinker", Card.ACTION_DURATION_COLOR), " to ", nextPlayer.pronoun, " hand")
                    nextPlayer.tinkerCards.clear()
                }
            }
            DurationHandler.applyDurationCards(this, nextPlayer)
            actionCardsInPlay += nextPlayer.durationCards.size
        }
        if (isUsingLeaders && nextPlayer.cardBonusTurns > 0) {
            nextPlayer.drawCards(1)
            nextPlayer.cardBonusTurns = nextPlayer.cardBonusTurns - 1
            addHistory(nextPlayer.username, " gained +1 Card from ", nextPlayer.pronoun, " leader")
        }
        if (isUsingLeaders && nextPlayer.buyBonusTurns > 0) {
            nextPlayer.addBuys(1)
            nextPlayer.buyBonusTurns = nextPlayer.buyBonusTurns - 1
            addHistory(nextPlayer.username, " gained +1 Buy from ", nextPlayer.pronoun, " leader")
        }
        if (checkHorseTraders && !nextPlayer.setAsideCards.isEmpty()) {
            val numHorseTraders = nextPlayer.setAsideCards.size
            for (card in nextPlayer.setAsideCards) {
                nextPlayer.addCardToHand(card)
            }
            nextPlayer.setAsideCards.clear()
            addHistory(nextPlayer.username, " returned ", numHorseTraders.toString(), " ", KingdomUtil.getCardWithBackgroundColor(horseTradersCard!!), " to ", nextPlayer.pronoun, " hand, and got to draw ", KingdomUtil.getPlural(numHorseTraders, "Card"))
            nextPlayer.drawCards(numHorseTraders)
        }
        refreshHandArea(nextPlayer)
        refreshSupply(nextPlayer)
        playBeep(nextPlayer)
        setPlayerInfoDialog(nextPlayer, InfoDialog.yourTurnInfoDialog)
        refreshAllPlayersPlayingArea()
        refreshAllPlayersGameStatus()
        refreshAllPlayersTitle()
        if (refreshPeddler) {
            refreshAllPlayersSupply()
        }
        if (takeOutpostTurn) {
            addHistory(nextPlayer.username, " is taking an extra turn from the ", KingdomUtil.getWordWithBackgroundColor("Outpost", Card.ACTION_DURATION_COLOR), " Card")
            outpostCardPlayed = false
            outpostTurn = true
        } else {
            outpostCardPlayed = false
            outpostTurn = false
        }

        val nextPlayerUserId = nextPlayer.userId
        if (nextPlayer.isComputer) {
            if (takeOutpostTurn) {
                if (previousPlayerId != 0) {
                    try {
                        Thread.sleep(2500)
                    } catch (e: Exception) {
                        val error = GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e))
                        logError(error)
                    }

                }
                computerPlayers[nextPlayerUserId]!!.doNextAction()
            } else {
                Thread(
                        Runnable {
                            if (previousPlayerId != 0) {
                                try {
                                    if (previousPlayer!!.isComputer || !isAllComputerOpponents) {
                                        Thread.sleep(2700)
                                    } else {
                                        Thread.sleep(2200)
                                    }
                                } catch (e: Exception) {
                                    val error = GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e))
                                    logError(error)
                                }

                            }
                            computerPlayers[nextPlayerUserId]!!.doNextAction()
                        }
                ).start()
            }
        }
    }

    fun currentlyWinning(userId: Int): Boolean {
        val playersCopy = ArrayList(players)
        Collections.sort(playersCopy)
        val firstPlayer = playersCopy[0]
        if (firstPlayer.userId == userId) {
            return true
        }
        val highScore = firstPlayer.victoryPoints
        val leastTurns = firstPlayer.turns
        for (player in playersCopy) {
            if (player.victoryPoints == highScore && leastTurns == player.turns) {
                if (player.userId == userId) {
                    return true
                }
            } else {
                break
            }
        }
        return false
    }

    fun getLosingMargin(userId: Int): Int {
        val playersCopy = ArrayList(players)
        Collections.sort(playersCopy)
        if (playersCopy.isEmpty()) {
            val error = GameError(GameError.COMPUTER_ERROR, "Players Copy Empty, players size: " + players.size)
            logError(error, false)
            return 0
        }
        val firstPlayer = playersCopy[0]
        if (firstPlayer.userId == userId) {
            return 0
        }
        for (player in playersCopy) {
            if (player.userId == userId) {
                return firstPlayer.victoryPoints - player.victoryPoints
            }
        }
        return 0
    }

    @Synchronized
    private fun determineWinner() {
        if (!determinedWinner) {
            determinedWinner = true
            Collections.sort(players)
            val firstPlayer = players[0]
            val highScore = firstPlayer.victoryPoints
            val leastTurns = firstPlayer.turns
            val marginOfVictory = players[0].victoryPoints - players[1].victoryPoints
            val winners = ArrayList<String>()
            for (player in players) {
                if (player.victoryPoints == highScore && leastTurns == player.turns) {
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

            for (computerPlayer in computerPlayers.values) {
                computerPlayer.stopped = true
                playerExitedGame(computerPlayer.player)
            }
        }
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
            history.leaders = isUsingLeaders
            val cardNames = ArrayList<String>()
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
            gameManager!!.saveGameHistory(history)

            val sb = StringBuilder()
            for (playerTurn in turnHistory) {
                sb.append(KingdomUtil.implode(playerTurn.history, ";"))
            }

            val log = GameLog()
            log.gameId = history.gameId
            log.log = sb.toString()
            gameManager!!.saveGameLog(log)
            logId = log.logId

            for (player in players) {
                gameManager!!.saveGameUserHistory(history.gameId, player)
            }
        }
    }

    fun playerExitedGame(player: Player) {
        updateLastActivity()
        if (!player.isComputer) {
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
        determineWinner()
        winnerString = ""
        status = STATUS_GAME_FINISHED
        refreshAllPlayersGameStatus()
        refreshAllPlayersTitle()
        addGameChat(gameEndReason)
    }

    fun calculateNextPlayerIndex(playerIndex: Int): Int {
        return if (playerIndex == players.size - 1) {
            0
        } else playerIndex + 1
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

    fun refreshGameStatus(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshGameStatus = true
    }

    fun refreshTitle(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshTitle = true
    }

    fun refreshChat(userId: Int) {
        val refresh = needsRefresh[userId]!!
        refresh.isRefreshChat = true
    }

    fun closeCardActionDialog(player: Player) {
        player.isShowCardAction = false
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshCardAction = false
        refresh.isCloseCardActionDialog = true
    }

    fun closeLoadingDialog(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isCloseLoadingDialog = true
    }

    fun refreshAll(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isRefreshGameStatus = true
        if (player.isShowCardAction && player.cardAction != null) {
            refresh.isRefreshCardAction = true
        }
        refresh.isRefreshHandArea = true
        refresh.isRefreshPlayers = true
        refresh.isRefreshPlayingArea = true
        refresh.isRefreshSupply = true

        if (status == STATUS_GAME_IN_PROGRESS && !hasIncompleteCard() && !currentPlayer!!.isShowCardAction) {
            if (!repeatedActions.isEmpty()) {
                playRepeatedAction(currentPlayer!!, false)
            } else if (!golemActions.isEmpty()) {
                playGolemActionCard(currentPlayer)
            }
        }
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

    fun playBeep(player: Player) {
        val refresh = needsRefresh[player.userId]!!
        refresh.isPlayBeep = true
    }

    fun addHistory(vararg eventStrings: String) {
        val sb = StringBuffer()
        for (s in eventStrings) {
            sb.append(s)
        }
        currentTurn!!.addHistory(sb.toString())
        historyEntriesAddedThisTurn++
        refreshAllPlayersHistory()
    }

    fun addChat(player: Player, message: String) {
        updateLastActivity()
        chats.add(ChatMessage(player.username + ": " + message, player.chatColor!!))
        refreshAllPlayersChat()
    }

    fun addPrivateChat(sender: User, receiver: User, message: String) {
        updateLastActivity()
        chats.add(ChatMessage("Private chat from " + sender.username + ": " + message, "black", receiver.userId))
        refreshChat(receiver.userId)
    }

    fun addGameChat(message: String) {
        chats.add(ChatMessage(message, "black"))
        refreshAllPlayersChat()
    }

    fun incrementCostDiscount() {
        costDiscount++
        addHistory("All cards cost 1 less coin this turn")
    }

    fun incrementActionCardDiscount(discount: Int) {
        actionCardDiscount += discount
    }

    fun removeNextBlackMarketCard(): Card? {
        return if (blackMarketCards.isEmpty()) {
            null
        } else blackMarketCards.removeAt(0)
    }

    @Synchronized
    fun allowClick(player: Player): Boolean {
        if (player.isComputer) {
            return true
        }
        if (processingClick.containsKey(player.userId)) {
            return false
        } else {
            processingClick[player.userId] = true
            return true
        }
    }

    fun removeProcessingClick(player: Player) {
        processingClick.remove(player.userId)
    }

    fun setPlayerCardAction(player: Player, cardAction: CardAction?) {
        if (cardAction == null) {
            val error = GameError(GameError.GAME_ERROR, "setPlayerCardAction, cardAction is null for user: " + player.username)
            logError(error, false)
        } else {
            if (player.isShowCardAction && player.cardAction!!.isWaitingForPlayers) {
                closeCardActionDialog(player)
                closeLoadingDialog(player)
            }
            player.cardAction = cardAction
            player.isShowCardAction = true
            if (player.isComputer && status == STATUS_GAME_IN_PROGRESS) {
                computerPlayers[player.userId]!!.handleCardAction(cardAction)
            } else {
                refreshCardAction(player)
            }
        }
    }

    fun setPlayerInfoDialog(player: Player, infoDialog: InfoDialog) {
        if (!player.isComputer) {
            player.isShowInfoDialog = true
            player.infoDialog = infoDialog
            refreshInfoDialog(player)
        } else if (infoDialog.isError) {
            val error = GameError(GameError.COMPUTER_ERROR, infoDialog.message!!)
            logError(error)
            computerPlayers[player.userId]!!.error = true
        }
    }

    fun setGameManager(gameManager: GameManager) {
        this.gameManager = gameManager
    }

    fun cardActionSubmitted(player: Player, selectedCardIds: List<Int>, yesNoAnswer: String?, choice: String?, numberChosen: Int) {
        if (allowClick(player)) {
            updateLastActivity()
            try {
                val coinsBefore = player.coins
                CardActionHandler.handleSubmittedCardAction(this, player, selectedCardIds, yesNoAnswer, choice, numberChosen)
                if (coinsBefore != player.coins && player.userId == currentPlayerId) {
                    refreshSupply(player)
                }
            } finally {
                processingClick.remove(player.userId)
            }
        }
    }

    fun setCustom(custom: Boolean) {
        this.custom = custom
    }

    @JvmOverloads
    fun logError(error: GameError, showInChat: Boolean = true) {
        if (gameManager == null) {
            return
        }

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
        if (!playerNames.isEmpty()) {
            errorHistory.append("Players: ").append(KingdomUtil.implode(playerNames, ",")).append("; ")
        }
        if (!kingdomCards.isEmpty()) {
            errorHistory.append("Kingdom Cards: ").append(kingdomCardsString).append("; ")
        }
        if (currentPlayer != null) {
            errorHistory.append("Current Player Hand: ").append(KingdomUtil.getCardNames(currentPlayer!!.hand, false)).append("; ")
        }
        if (currentTurn != null) {
            errorHistory.append(KingdomUtil.implode(currentTurn!!.history, ";"))
        }

        error.history = errorHistory.toString()
        gameManager!!.logError(error)

        if (showInChat) {
            if (error.computerError) {
                addGameChat("The computer encountered an error. This error has been reported and will be fixed as soon as possible. If you would like to keep playing you can quit this game and start a new one with different cards.")
            } else {
                addGameChat("The game encountered an error. Try refreshing the page.")
            }
        }
    }

    fun hasIncompleteCard(): Boolean {
        return incompleteCard != null
    }

    fun removeIncompleteCard() {
        incompleteCard = null
    }

    fun updateLastActivity() {
        lastActivity = Date()
    }

    fun princessCardPlayed() {
        if (!princessCardPlayed) {
            princessCardPlayed = true
            costDiscount += 2
            addHistory("All cards cost 2 less coins this turn")
        }
    }

    private fun startPlayerTurn(player: Player) {
        if (recentTurnHistory.size == maxHistoryTurnSize) {
            recentTurnHistory.removeFirst()
        }
        if (currentTurn != null) {
            currentTurn!!.addHistory("")
        }
        currentTurn = PlayerTurn(player)
        recentTurnHistory.add(currentTurn!!)
        turnHistory.add(currentTurn!!)

        refreshAllPlayersHistory()
    }

    fun hasNextAction(): Boolean {
        return !nextActionQueue.isEmpty()
    }

    fun addNextAction(nextAction: String) {
        nextActionQueue.add(nextAction)
    }

    fun removeNextAction() {
        nextActionQueue.remove()
    }

    fun playerRevealedEnchantedPalace(userId: Int) {
        enchantedPalaceRevealed.add(userId)
    }

    fun revealedEnchantedPalace(userId: Int): Boolean {
        return enchantedPalaceRevealed.contains(userId)
    }

    fun repeat() {
        val playersCopy = ArrayList(players)
        val computerPlayerMapCopy = HashMap(computerPlayers)
        reset(true)
        repeated = true
        setupSupply()
        setupTokens()
        creationTime = Date()
        updateLastActivity()
        for (player in playersCopy) {
            val user = User()
            user.userId = player.userId
            user.gender = player.gender
            user.username = player.username
            if (player.isComputer) {
                val computerPlayer = computerPlayerMapCopy[player.userId]!!
                addPlayer(user, true, computerPlayer.isBigMoneyUltimate, computerPlayer.difficulty)
            } else {
                addPlayer(user)
            }
        }
        playersCopy.clear()
        computerPlayerMapCopy.clear()
        start()
    }

    fun finishedGainCardAction(player: Player, cardAction: CardAction) {
        val card = cardAction.associatedCard!!
        if (card.gainCardActions.isEmpty()) {
            cardsWithGainCardActions.remove(card.cardId)
        }
        if (!isCurrentPlayer(player) && !player.isShowCardAction && player.extraCardActions.isEmpty() && !hasUnfinishedGainCardActions()) {
            playersWithCardActions.remove(player.userId)
            if (playersWithCardActions.isEmpty() && currentPlayer!!.isShowCardAction && currentPlayer!!.cardAction!!.isWaitingForPlayers) {
                closeCardActionDialog(currentPlayer!!)
                closeLoadingDialog(currentPlayer!!)
            }
        }
    }

    fun finishTunnelCardAction(player: Player) {
        playersWithCardActions.remove(player.userId)
        if (playersWithCardActions.isEmpty() && currentPlayer!!.isShowCardAction && currentPlayer!!.cardAction!!.isWaitingForPlayers) {
            closeCardActionDialog(currentPlayer!!)
            closeLoadingDialog(currentPlayer!!)
        }
    }

    fun hasUnfinishedGainCardActions(): Boolean {
        return !cardsWithGainCardActions.isEmpty()
    }

    fun isCardInSupply(card: Card?): Boolean {
        return isCardInSupply(card!!.cardId)
    }

    fun isCardInSupply(cardId: Int): Boolean {
        return supply[cardId] != null && getNumInSupply(cardId) > 0
    }

    fun getNumInSupply(card: Card?): Int {
        return getNumInSupply(card!!.cardId)
    }

    fun getNumInSupply(cardId: Int): Int {
        return supply[cardId]!!
    }

    fun playerDiscardedCard(player: Player, card: Card) {
        if (checkTunnel && card.name == "Tunnel" && isCardInSupply(goldCard)) {
            waitIfNotCurrentPlayer(player)
            val cardAction = CardAction(CardAction.TYPE_YES_NO)
            cardAction.deck = Deck.Hinterlands
            cardAction.cardName = "Tunnel"
            cardAction.instructions = "Do you want to reveal your Tunnel to gain a Gold?"
            cardAction.cards.add(goldCard)
            cardAction.associatedCard = card
            setPlayerCardAction(player, cardAction)
        }
    }

    fun showUseFruitTokensCardAction(player: Player) {
        if (isCurrentPlayer(player)) {
            val cardAction = CardAction(CardAction.TYPE_CHOOSE_NUMBER_BETWEEN)
            cardAction.deck = Deck.Proletariat
            cardAction.cardName = "Use Fruit Tokens"
            cardAction.buttonValue = "Done"
            cardAction.startNumber = 0
            cardAction.endNumber = player.fruitTokens
            cardAction.instructions = "Click the number of Fruit Tokens you want to use."
            setPlayerCardAction(player, cardAction)
        }
    }

    fun showUseCattleTokensCardAction(player: Player) {
        if (isCurrentPlayer(player)) {
            val cardAction = CardAction(CardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN)
            cardAction.deck = Deck.Proletariat
            cardAction.cardName = "Use Cattle Tokens"
            cardAction.buttonValue = "Done"
            cardAction.startNumber = 0
            cardAction.endNumber = player.cattleTokens
            cardAction.instructions = "Click the number of Cattle Tokens you want to use."
            setPlayerCardAction(player, cardAction)
        }
    }

    fun addFruitTokensPlayed(fruitTokensPlayed: Int) {
        this.fruitTokensPlayed += fruitTokensPlayed
    }

    companion object {
        const val STATUS_NO_GAMES = 0
        const val STATUS_GAME_BEING_CONFIGURED = 1
        const val STATUS_GAME_WAITING_FOR_PLAYERS = 2
        const val STATUS_GAME_IN_PROGRESS = 3
        const val STATUS_GAME_FINISHED = 4
    }
}
