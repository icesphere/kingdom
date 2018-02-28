package com.kingdom.model

import com.kingdom.util.KingdomUtil

import java.util.*

class Player(user: User, game: Game) : Comparable<Player> {

    var userId: Int = 0
    var deck: MutableList<Card> = LinkedList()
    val hand = ArrayList<Card>()
    val discard = LinkedList<Card>()
    val durationCards: MutableList<Card> = ArrayList(0)
    val havenCards: MutableList<Card> = ArrayList(0)
    val islandCards: List<Card> = ArrayList(0)
    val nativeVillageCards: List<Card> = ArrayList(0)
    val treasureCards = ArrayList<Card>(0)
    val actionCards = ArrayList<Card>(0)
    private val victoryCards = ArrayList<Card>(0)
    val museumCards: List<Card> = ArrayList(0)
    val cityPlannerCards: List<Card> = ArrayList(0)
    var pirateShipCoins: Int = 0
        private set
    var coins: Int = 0
        get() = if (playTreasureCards) {
            field
        } else {
            field + coinsInHand
        }
        private set
    var coinsInHand: Int = 0
        private set
    var actions: Int = 0
        private set
    var buys: Int = 0
        private set
    private var moatCardsInHand: Int = 0
    private var hasLighthouse: Boolean = false
    private var secretChamberCardsInHand: Int = 0
    private var victoryCardsInHand: Int = 0
    private var copperCardsInHand: Int = 0
    var curseCardsInHand: Int = 0
        private set
    private var copperSmithsPlayed: Int = 0
    var estates: Int = 0
        private set
    var duchies: Int = 0
        private set
    var provinces: Int = 0
        private set
    var colonies: Int = 0
        private set
    var curses: Int = 0
        private set
    var gardens: Int = 0
        private set
    private var farmlands: Int = 0
    var vineyards: Int = 0
        private set
    var silkRoads: Int = 0
        private set
    var cathedrals: Int = 0
        private set
    var sinsRemoved: Int = 0
        private set
    var cursesRemoved: Int = 0
        private set
    var fairgrounds: Int = 0
        private set
    var greatHalls: Int = 0
        private set
    var harems: Int = 0
        private set
    var dukes: Int = 0
        private set
    var nobles: Int = 0
        private set
    var archbishops: Int = 0
        private set
    var islands: Int = 0
        private set
    var enchantedPalaces: Int = 0
        private set
    var hedgeWizards: Int = 0
        private set
    var goldenTouches: Int = 0
        private set
    var username = ""
    var isShowCardAction: Boolean = false

    /*
    if (showCardAction && cardAction != null && !cardAction.isWaitingForPlayers()) {
            extraCardActions.add(cardAction);
        } else {
            this.cardAction = cardAction;
        }
     */

    var cardAction: CardAction? = null
        set(value) = if (isShowCardAction && value != null && !value.isWaitingForPlayers) {
            extraCardActions.add(value)
            field = field
        } else {
            field = value
        }
    var isShowInfoDialog: Boolean = false
    var infoDialog: InfoDialog? = null
    var chatColor: String? = null
    private var hasBoughtCard: Boolean = false
    var turns: Int = 0
        private set
    var isWinner: Boolean = false
    var marginOfVictory: Int = 0
    var numCards: Int = 0
        private set
    var numActions: Int = 0
        private set
    var numVictoryCards: Int = 0
        private set
    var numTreasureCards: Int = 0
        private set
    var numDifferentCards: Int = 0
        private set
    private var tacticianBonus: Boolean = false
    var potions: Int = 0
        get() = if (playTreasureCards) {
            field
        } else {
            field + potionsInHand
        }
    var potionsInHand: Int = 0
        private set
    private val playTreasureCards: Boolean
    val gender: String
    var victoryCoins: Int = 0
        private set
    private var watchtowerCardsInHand: Int = 0
    var isComputer: Boolean = false
    var isQuit: Boolean = false
    private val diamondMines: Int = 0
    var sins: Int = 0
    val edictCards: MutableList<Card> = ArrayList(0)
    val lastTurnEdictCards: MutableList<Card> = ArrayList(0)
    private var provinceCardsInHand: Int = 0
    private var baneCardsInHand: Int = 0
    private val baneCardId: Int
    private var horseTradersInHand: Int = 0
    private var bellTowersInHand: Int = 0
    var enchantedPalacesInHand: Int = 0
        private set
    val setAsideCards = ArrayList<Card>(0)
    var isPlayedCopper: Boolean = false
        private set
    var autoPlayCoins: Int = 0
        private set
    private var finalPointsCalculated = false
    private var finalVictoryPoints = 0
    private var finalCards: List<Card>? = null
    var isPlayedTinker: Boolean = false
    val tinkerCards: MutableList<Card> = ArrayList(0)
    val extraCardActions: Queue<CardAction> = LinkedList()
    val isUsingLeaders: Boolean
    val leaders: List<Card> = ArrayList(3)
    var pointsFromLeaders: Int = 0
        private set
    private var numCopper: Int = 0
    private var numSilver: Int = 0
    private var numGold: Int = 0
    private var traderCardsInHand: Int = 0
    var isFoolsGoldPlayed: Boolean = false
    var foolsGoldInHand: Int = 0
        private set
    var fruitTokens: Int = 0
        private set
    var cattleTokens: Int = 0
        private set

    private var enableVictoryCardDiscount: Boolean = false
    private var enableActionCardDiscount: Boolean = false
    private var enableTreasureCardDiscount: Boolean = false

    private var varroActivated: Boolean = false
    private var varroPoints: Int = 0

    var victoryCardDiscountTurns = 0
    var actionCardDiscountTurns = 0
    var treasureCardDiscountTurns = 0
    var buyBonusTurns = 0
    var cardBonusTurns = 0

    private var leaderDiscount: Int = 0

    var isMobile: Boolean = false

    val groupedHand: List<Card>
        get() {
            KingdomUtil.groupCards(hand)
            return hand
        }

    val victoryPoints: Int
        get() = getVictoryPoints(false)

    val isInfoDialogSet: Boolean
        get() = infoDialog != null

    val durationCardsString: String
        get() = KingdomUtil.groupCards(durationCards, true)

    val islandCardsString: String
        get() = KingdomUtil.groupCards(islandCards, true)

    val museumCardsString: String
        get() = KingdomUtil.groupCards(museumCards, true)

    val cityPlannerCardsString: String
        get() = KingdomUtil.groupCards(cityPlannerCards, true)

    val philosophersStoneCoins: Int
        get() {
            val numCards = deck.size + discard.size
            return Math.floor((numCards / 5).toDouble()).toInt()
        }

    val pronoun: String
        get() {
            if (gender == User.MALE) {
                return "his"
            } else if (gender == User.FEMALE) {
                return "her"
            } else if (gender == User.COMPUTER) {
                return "its"
            }
            return "his/her"
        }

    val allCards: MutableList<Card>
        get() {
            val allCards = ArrayList<Card>()
            allCards.addAll(hand)
            allCards.addAll(deck)
            allCards.addAll(discard)
            allCards.addAll(havenCards)
            allCards.addAll(islandCards)
            allCards.addAll(museumCards)
            allCards.addAll(cityPlannerCards)
            allCards.addAll(nativeVillageCards)
            allCards.addAll(durationCards)
            allCards.addAll(tinkerCards)
            return allCards
        }

    val currentHand: String
        get() = KingdomUtil.groupCards(hand, true)

    val activatedLeaders: List<Card>
        get() {
            val activatedLeaders = ArrayList<Card>()
            for (leader in leaders) {
                if (leader.isActivated) {
                    activatedLeaders.add(leader)
                }
            }
            return activatedLeaders
        }

    val activatedLeaderCardsString: String
        get() = KingdomUtil.groupCards(activatedLeaders, true, false)

    init {
        actions = 1
        buys = 1
        playTreasureCards = game.isPlayTreasureCards
        baneCardId = game.baneCardId
        isUsingLeaders = game.isUsingLeaders
        userId = user.userId
        gender = user.gender
        username = user.username
        isMobile = user.isMobile
        if (game.isIdenticalStartingHands && game.players.size > 0) {
            val firstPlayer = game.players[0]
            deck.addAll(firstPlayer.deck)
            for (card in firstPlayer.hand) {
                addCardToHand(card)
            }
        } else {
            if (game.cardMap.isNotEmpty()) {
                for (i in 0..6) {
                    deck.add(game.copperCard)
                }
                for (i in 0..2) {
                    deck.add(game.estateCard)
                }
                Collections.shuffle(deck)
                for (i in 0..4) {
                    drawCardAndAddToHand()
                }
            }
        }
    }

    override fun compareTo(other: Player): Int {
        return when {
            victoryPoints == other.victoryPoints -> when {
                this.turns == other.turns -> 0
                this.turns < other.turns -> -1
                else -> 1
            }
            victoryPoints > other.victoryPoints -> -1
            else -> 1
        }
    }

    fun addCardToDiscard(card: Card) {
        discard.add(card)
    }

    fun addCoins(coins: Int) {
        if (playTreasureCards) {
            this.coins += coins
        } else {
            //compensate for getter
            this.coins += coins - coinsInHand
        }
    }

    fun subtractCoins(coins: Int) {
        addCoins(coins * -1)
    }

    fun addActions(actions: Int) {
        this.actions += actions
    }

    fun addBuys(buys: Int) {
        this.buys += buys
    }

    fun hasMoat(): Boolean {
        return moatCardsInHand > 0
    }

    fun hasProvinceInHand(): Boolean {
        return provinceCardsInHand > 0
    }

    fun hasBaneCardInHand(): Boolean {
        return baneCardsInHand > 0
    }

    fun hasHorseTradersInHand(): Boolean {
        return horseTradersInHand > 0
    }

    fun hasBellTowerInHand(): Boolean {
        return bellTowersInHand > 0
    }

    fun hasEnchantedPalaceInHand(): Boolean {
        return enchantedPalacesInHand > 0
    }

    fun hasLighthouse(): Boolean {
        return hasLighthouse
    }

    fun setHasLighthouse(hasLighthouse: Boolean) {
        this.hasLighthouse = hasLighthouse
    }

    fun hasSecretChamber(): Boolean {
        return secretChamberCardsInHand > 0
    }

    fun getVictoryCards(): List<Card> {
        return victoryCards
    }

    fun hasVictoryCard(): Boolean {
        return victoryCardsInHand > 0
    }

    fun removeTopDeckCard(): Card? {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck()
        }
        return deck.removeAt(0)
    }

    fun addCardToTopOfDeck(card: Card) {
        deck.add(0, card)
    }

    fun shuffleDeck() {
        Collections.shuffle(deck)
    }

    fun shuffleDiscardIntoDeck() {
        deck.addAll(discard)
        Collections.shuffle(deck)
        discard.clear()
    }

    fun lookAtTopDeckCard(): Card? {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck()
        }
        return deck[0]
    }

    fun lookAtTopDeckCards(numCards: Int): List<Card> {
        val cards = ArrayList<Card>()
        while (cards.size < numCards) {
            val card = removeTopDeckCard()
            if (card == null) {
                break
            } else {
                cards.add(card)
            }
        }
        if (!cards.isEmpty()) {
            deck.addAll(0, cards)
        }
        return cards
    }

    fun lookAtBottomDeckCard(): Card? {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck()
        }
        return deck[deck.size - 1]
    }

    fun addCardToHand(card: Card) {
        hand.add(card)
        if (card.isTreasure) {
            treasureCards.add(card)
            coinsInHand += card.addCoins
            if (card.cardId == Card.COPPER_ID) {
                coinsInHand += copperSmithsPlayed
                copperCardsInHand++
            }
            if (card.isAutoPlayTreasure) {
                autoPlayCoins += card.addCoins
            }
        }
        if (card.isAction) {
            actionCards.add(card)
        }
        if (card.cardId == Card.CURSE_ID) {
            curseCardsInHand++
        }
        if (card.isVictory) {
            victoryCardsInHand++
            victoryCards.add(card)
        }

        if (card.cardId == baneCardId) {
            baneCardsInHand++
        }

        when(card.name) {
            "Moat" -> moatCardsInHand++
            "Secret Chamber" -> secretChamberCardsInHand++
            "Watchtower" -> watchtowerCardsInHand++
            "Province" -> provinceCardsInHand++
            "Horse Traders" -> horseTradersInHand++
            "Bell Tower" -> bellTowersInHand++
            "Enchanted Palace" -> enchantedPalacesInHand++
            "Fool's Gold" -> foolsGoldInHand++
            "Trader" -> traderCardsInHand++
        }

        if (card.cardId == Card.POTION_ID) {
            potionsInHand++
        }
    }

    private fun cardRemoved(card: Card) {
        if (card.isTreasure) {
            treasureCards.remove(card)
            if (card.name != "Philosopher's Stone" && card.name != "Bank") {
                coinsInHand -= card.addCoins
                if (card.isAutoPlayTreasure) {
                    autoPlayCoins -= card.addCoins
                }
            }
            if (card.cardId == Card.COPPER_ID) {
                coinsInHand -= copperSmithsPlayed
                copperCardsInHand--
            }
        }
        if (card.isAction) {
            actionCards.remove(card)
        }
        if (card.cardId == Card.CURSE_ID) {
            curseCardsInHand--
        }
        if (card.isVictory) {
            victoryCardsInHand--
            victoryCards.remove(card)
        }

        if (card.cardId == baneCardId) {
            baneCardsInHand--
        }

        when(card.name) {
            "Moat" -> moatCardsInHand--
            "Secret Chamber" -> secretChamberCardsInHand--
            "Watchtower" -> watchtowerCardsInHand--
            "Province" -> provinceCardsInHand--
            "Horse Traders" -> horseTradersInHand--
            "Bell Tower" -> bellTowersInHand--
            "Enchanted Palace" -> enchantedPalacesInHand--
            "Fool's Gold" -> foolsGoldInHand--
            "Trader" -> traderCardsInHand--
        }

        if (card.isPotion) {
            potionsInHand--
        }
    }

    fun removeCardFromHand(card: Card) {
        cardRemoved(card)
        hand.remove(card)
    }

    fun treasureCardPlayed(card: Card, removeFromHand: Boolean) {
        if (card.cardId == Card.COPPER_ID) {
            addCoins(copperSmithsPlayed)
            isPlayedCopper = true
        }
        if (removeFromHand) {
            cardRemoved(card)
            hand.remove(card)
        }
    }

    fun getCardFromHandById(cardId: Int): Card? {
        return hand.firstOrNull { it.cardId == cardId }
    }

    fun discardCardFromHand(card: Card) {
        cardRemoved(card)
        discard.add(card)
        hand.remove(card)
    }

    fun discardCardFromHand(cardId: Int) {
        val card = getCardFromHandById(cardId)
        if (card != null) {
            discardCardFromHand(card)
        }
    }

    fun discardHand() {
        for (card in hand) {
            cardRemoved(card)
        }
        discard.addAll(hand)
        hand.clear()
    }

    fun drawCardAndAddToHand() {
        val card = removeTopDeckCard()
        if (card != null) {
            addCardToHand(card)
        }
    }

    fun drawCards(numCards: Int) {
        var cardsDrawn = 0
        while (cardsDrawn < numCards) {
            drawCardAndAddToHand()
            cardsDrawn++
        }
    }

    fun endTurn(cardsToDraw: Int) {
        coins = 0
        coinsInHand = 0
        autoPlayCoins = 0
        potions = 0
        potionsInHand = 0
        actions = 1
        buys = 1
        treasureCards.clear()
        actionCards.clear()
        victoryCards.clear()
        moatCardsInHand = 0
        provinceCardsInHand = 0
        baneCardsInHand = 0
        horseTradersInHand = 0
        bellTowersInHand = 0
        enchantedPalacesInHand = 0
        watchtowerCardsInHand = 0
        secretChamberCardsInHand = 0
        victoryCardsInHand = 0
        copperCardsInHand = 0
        curseCardsInHand = 0
        copperSmithsPlayed = 0
        traderCardsInHand = 0
        discard.addAll(hand)
        hand.clear()
        hasBoughtCard = false
        isPlayedCopper = false
        isFoolsGoldPlayed = false
        foolsGoldInHand = 0

        if (isUsingLeaders) {
            adjustLeaderBonuses()
        }

        drawCards(cardsToDraw)

        isShowCardAction = false
        cardAction = null
        isShowInfoDialog = false
        infoDialog = null
        hasLighthouse = false
        if (cardsToDraw == 5) {
            turns++
        }
    }

    private fun adjustLeaderBonuses() {
        if (victoryCardDiscountTurns > 0) {
            victoryCardDiscountTurns--
        }
        if (actionCardDiscountTurns > 0) {
            actionCardDiscountTurns--
        }
        if (treasureCardDiscountTurns > 0) {
            treasureCardDiscountTurns--
        }
        if (enableVictoryCardDiscount) {
            victoryCardDiscountTurns = 2
            enableVictoryCardDiscount = false
        }
        if (enableActionCardDiscount) {
            actionCardDiscountTurns = 2
            enableActionCardDiscount = false
        }
        if (enableTreasureCardDiscount) {
            treasureCardDiscountTurns = 2
            enableTreasureCardDiscount = false
        }
    }

    fun getFinalVictoryPoints(): Int {
        return getVictoryPoints(true)
    }

    fun getVictoryPoints(gameOver: Boolean): Int {
        if (finalPointsCalculated) {
            return finalVictoryPoints
        }
        var curseCard: Card? = null
        var victoryPoints = 0
        gardens = 0
        farmlands = 0
        vineyards = 0
        silkRoads = 0
        cathedrals = 0
        sinsRemoved = 0
        cursesRemoved = 0
        fairgrounds = 0
        dukes = 0
        greatHalls = 0
        harems = 0
        nobles = 0
        archbishops = 0
        islands = 0
        estates = 0
        duchies = 0
        provinces = 0
        colonies = 0
        curses = 0
        numActions = 0
        numVictoryCards = 0
        numTreasureCards = 0
        enchantedPalaces = 0
        hedgeWizards = 0
        goldenTouches = 0
        pointsFromLeaders = 0
        numCopper = 0
        numSilver = 0
        numGold = 0
        val allCards = allCards
        numCards = allCards.size
        val cardNames = HashSet<String>()
        for (card in allCards) {
            cardNames.add(card.name)
            if (card.isVictory || card.isCurse) {
                victoryPoints += card.victoryPoints

                when(card.name) {
                    "Gardens" -> gardens++
                    "Farmland" -> farmlands++
                    "Vineyard" -> vineyards++
                    "Silk Road" -> silkRoads++
                    "Fairgrounds" -> fairgrounds++
                    "Duke" -> dukes++
                    "Great Hall" -> greatHalls++
                    "Harem" -> harems++
                    "Nobles" -> nobles++
                    "Archbishop" -> archbishops++
                    "Island" -> islands++
                    "Cathedral" -> cathedrals++
                    "Enchanted Palace" -> enchantedPalaces++
                    "Hedge Wizard" -> hedgeWizards++
                    "Golden Touch" -> goldenTouches++
                }

                when(card.cardId) {
                    Card.ESTATE_ID -> estates++
                    Card.DUCHY_ID -> duchies++
                    Card.PROVINCE_ID -> provinces++
                    Card.COLONY_ID -> colonies++
                    Card.CURSE_ID -> {
                        curses++
                        if (curseCard == null) {
                            curseCard = card
                        }
                    }
                }
            }
            if (card.isAction) {
                numActions++
            }
            if (card.isVictory) {
                numVictoryCards++
            }
            if (card.isTreasure) {
                numTreasureCards++
                if (card.isCopper) {
                    numCopper++
                } else if (card.isSilver) {
                    numSilver++
                } else if (card.isGold) {
                    numGold++
                }
            }
        }
        if (vineyards > 0) {
            victoryPoints += (vineyards * Math.floor((numActions / 3).toDouble())).toInt()
        }
        if (silkRoads > 0) {
            victoryPoints += (silkRoads * Math.floor((numVictoryCards / 4).toDouble())).toInt()
        }
        if (dukes > 0) {
            victoryPoints += dukes * duchies
        }
        if (cathedrals > 0) {
            var cathedralsUsed = 0
            if (sins > 0) {
                if (cathedrals > sins) {
                    cathedralsUsed = sins
                    sinsRemoved = sins
                } else {
                    cathedralsUsed = cathedrals
                    sinsRemoved = cathedrals
                }
                if (gameOver) {
                    sins -= sinsRemoved
                } else {
                    victoryPoints += sinsRemoved
                }
            }
            while (cathedralsUsed < cathedrals && curses > 0) {
                cathedralsUsed++
                cursesRemoved++
                numCards--
                victoryPoints++
                curses--
                if (gameOver) {
                    allCards.remove(curseCard)
                }
            }
        }
        if (sins > 0) {
            victoryPoints -= sins
        }
        if (gardens > 0) {
            victoryPoints += (gardens * Math.floor((numCards / 10).toDouble())).toInt()
        }
        numDifferentCards = cardNames.size
        if (fairgrounds > 0) {
            victoryPoints += (fairgrounds.toDouble() * 2.0 * Math.floor((numDifferentCards / 5).toDouble())).toInt()
        }
        victoryPoints += victoryCoins

        if (isUsingLeaders) {
            for (leader in leaders) {
                if (leader.isActivated) {
                    pointsFromLeaders += getLeaderPoints(leader)
                }
            }
            victoryPoints += pointsFromLeaders
        }

        if (gameOver) {
            finalPointsCalculated = true
            finalVictoryPoints = victoryPoints
        }

        finalCards = allCards

        return victoryPoints
    }

    private fun getLeaderPoints(leader: Card): Int {
        var points = leader.victoryPoints

        when(leader.name) {
            "Hatshepsut" -> if (numGold == 0) {
                points += 6
            }
            "Hypatia" -> points += Math.floor((numVictoryCards / 2).toDouble()).toInt()
            "Justinian" -> {
                var least = numActions
                if (numVictoryCards < least) {
                    least = numVictoryCards
                }
                if (numTreasureCards < least) {
                    least = numTreasureCards
                }
                points += least
            }
            "Midas" -> points += Math.floor((numTreasureCards / 4).toDouble()).toInt()
            "Pericles" -> points += Math.floor((numActions / 2).toDouble()).toInt()
            "Solomon" -> {
                var solomonPoints = Math.floor((100 / numCards).toDouble()).toInt()
                if (solomonPoints > 10) {
                    solomonPoints = 10
                }
                points += solomonPoints
            }
            "Varro" -> points += varroPoints
        }

        return points
    }

    fun addPotions(potions: Int) {
        this.potions += potions
    }

    fun addPirateShipCoin() {
        pirateShipCoins++
    }

    fun addFruitTokens(tokens: Int) {
        fruitTokens += tokens
    }

    fun addCattleTokens(tokens: Int) {
        cattleTokens += tokens
    }

    fun putCardFromHandOnTopOfDeck(card: Card) {
        deck.add(0, card)
        cardRemoved(card)
        hand.remove(card)
    }

    fun hasBoughtCard(): Boolean {
        return hasBoughtCard
    }

    fun setHasBoughtCard(hasBoughtCard: Boolean) {
        this.hasBoughtCard = hasBoughtCard
    }

    fun copperSmithPlayed() {
        copperSmithsPlayed++
        if (!playTreasureCards) {
            addCoins(copperCardsInHand)
        }
    }

    fun hasTacticianBonus(): Boolean {
        return tacticianBonus
    }

    fun setTacticianBonus(tacticianBonus: Boolean) {
        this.tacticianBonus = tacticianBonus
    }

    fun addVictoryCoins(victoryCoins: Int) {
        this.victoryCoins += victoryCoins
    }

    fun addSins(sins: Int) {
        this.sins += sins
        if (this.sins < 0) {
            this.sins = 0
        } else if (this.sins > 20) {
            this.sins = 20
        }
    }

    fun hasWatchtower(): Boolean {
        return watchtowerCardsInHand > 0
    }

    fun getFinalCards(): String {
        return KingdomUtil.groupCards(finalCards, true)
    }

    fun setAsideCardFromHand(card: Card) {
        removeCardFromHand(card)
        setAsideCards.add(card)
    }

    fun setEnableVictoryCardDiscount(enableVictoryCardDiscount: Boolean) {
        this.enableVictoryCardDiscount = enableVictoryCardDiscount
    }

    fun setEnableActionCardDiscount(enableActionCardDiscount: Boolean) {
        this.enableActionCardDiscount = enableActionCardDiscount
    }

    fun setEnableTreasureCardDiscount(enableTreasureCardDiscount: Boolean) {
        this.enableTreasureCardDiscount = enableTreasureCardDiscount
    }

    fun setLeaderDiscount(leaderDiscount: Int) {
        this.leaderDiscount = leaderDiscount
    }

    fun getCardDiscount(card: Card): Int {
        var discount = 0
        if (card.isVictory && victoryCardDiscountTurns > 0) {
            discount += 2
        }
        if (card.isAction && actionCardDiscountTurns > 0) {
            discount += 2
        }
        if (card.isTreasure && treasureCardDiscountTurns > 0) {
            discount += 2
        }
        if (card.isLeader && leaderDiscount > 0) {
            discount += leaderDiscount
        }
        return discount
    }

    fun leaderActivated(leader: Card) {
        if (leader.name == "Varro") {
            varroActivated = true
        } else if (varroActivated) {
            varroPoints += 2
        }
    }

    fun getLeaderCard(cardId: Int): Card? {
        for (leader in leaders) {
            if (leader.cardId == cardId) {
                return leader
            }
        }
        return null
    }

    fun hasTrader(): Boolean {
        return traderCardsInHand > 0
    }

    fun hasFoolsGoldInHand(): Boolean {
        return foolsGoldInHand > 0
    }
}
