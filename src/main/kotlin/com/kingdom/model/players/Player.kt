package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.TurnSummary
import com.kingdom.model.User
import com.kingdom.model.cards.*
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.adventures.InheritanceEstate
import com.kingdom.model.cards.darkages.BandOfMisfits
import com.kingdom.model.cards.darkages.Spoils
import com.kingdom.model.cards.darkages.shelters.Hovel
import com.kingdom.model.cards.darkages.shelters.Necropolis
import com.kingdom.model.cards.darkages.shelters.OvergrownEstate
import com.kingdom.model.cards.empires.Overlord
import com.kingdom.model.cards.listeners.*
import com.kingdom.model.cards.renaissance.artifacts.*
import com.kingdom.model.cards.renaissance.projects.Citadel
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.util.KingdomUtil
import com.kingdom.util.groupedString
import java.util.*
import java.util.function.Function
import kotlin.collections.ArrayList

abstract class Player protected constructor(val user: User, val game: Game) {
    var deck: MutableList<Card> = ArrayList()
    val hand: MutableList<Card> = ArrayList()

    val deckCopy: List<Card>
        get() = deck.map { it.copy(true) }

    val handCopy: List<Card>
        get() = hand.map { it.copy(true) }

    protected val discard: MutableList<Card> = ArrayList()

    val cardsInDiscard: List<Card>
        get() = discard

    val cardsInDiscardCopy: List<Card>
        get() = cardsInDiscard.map { it.copy(true) }

    val cardsGained: MutableList<Card> = ArrayList()

    val cardsBought: MutableList<Card> = ArrayList()

    val cardsBoughtCopy: List<Card>
        get() = cardsBought.map { it.copy(true) }

    val cardsPlayed: MutableList<Card> = ArrayList()
    val inPlay: MutableList<Card> = ArrayList()

    val inPlayCopy: List<Card>
        get() = inPlay.map { it.copy(true) }

    val eventsBought = mutableListOf<Event>()

    val projectsBought = mutableListOf<Project>()

    val projectsString
        get() = projectsBought.joinToString(", ") { it.name }

    val inPlayWithDuration: List<Card>
        get() = inPlay + durationCards

    val numCards: Int
        get() = allCards.size

    val userId = user.userId
    val username = user.username
    val isMobile = user.isMobile

    protected var actionsQueue: MutableList<Action> = ArrayList()

    var currentAction: Action? = null

    var isYourTurn: Boolean = false
        protected set

    val isStartOfTurn: Boolean
        get() = isYourTurn && cardsPlayed.isEmpty() && !isCardsBought

    var isReturnToActionPhase: Boolean = false

    val isBuyPhase: Boolean
        get() = isYourTurn && (isTreasureCardsPlayedInBuyPhase || isCardsBought || isActionTakenInBuyPhase) && !isReturnToActionPhase

    private var isActionTakenInBuyPhase: Boolean = false

    var isPaidOffDebtThisTurn: Boolean = false

    private var coins: Int = 0

    private val coinsInHand: Int
        get() = hand.filter { it.isTreasure }.sumBy { it.addCoins }

    val availableCoins: Int
        get() = coins

    var coinsSpent: Int = 0

    var debt: Int = 0

    var buys: Int = 0
        private set

    var actions: Int = 0
        private set

    var coffers: Int = 0

    var villagers: Int = 0

    val opponents: List<Player> by lazy {
        game.players.filterNot { it.userId == userId }
    }

    val opponentsInOrder: List<Player> by lazy {

        val list = mutableListOf<Player>()

        var nextPlayer = playerToLeft

        while (nextPlayer.userId != userId) {
            list.add(nextPlayer)

            nextPlayer = game.getPlayerToLeft(nextPlayer)
        }

        list
    }

    var isNextCardToTopOfDeck: Boolean = false

    var isNextCardToHand: Boolean = false

    private var shuffles: Int = 0

    private var isFirstPlayer: Boolean = false

    var turns: Int = 0

    var turn: Int = 0

    private var coinsGainedThisTurn: Int = 0

    var lastTurnSummary: TurnSummary? = null

    var currentTurnSummary = TurnSummary(username)

    var isWaitingForComputer: Boolean = false

    lateinit var chatColor: String

    var isQuit: Boolean = false

    var victoryCoins: Int = 0
        private set

    val victoryPoints: Int
        get() = getVictoryPoints(false)

    private var finalPointsCalculated = false
    var finalVictoryPoints = 0
    private var finalCards: List<Card>? = null

    var isWinner: Boolean = false
    var marginOfVictory: Int = 0

    val groupedHand: List<Card>
        get() {
            val handCopy = hand.toMutableList()
            KingdomUtil.groupCards(handCopy)
            return handCopy
        }

    val currentHand: String
        get() = hand.groupedString

    val allCardsString: String
        get() = allCards.groupedString

    val isTreasureCardsPlayedInBuyPhase: Boolean
        get() = (cardsPlayed - game.treasureCardsPlayedInActionPhase).any { it.isTreasure && !it.isAction }

    val isCardsBought: Boolean
        get() = cardsBought.isNotEmpty() || eventsBought.isNotEmpty()

    var isTreasuresPlayable: Boolean = true

    val isOpponentHasAction: Boolean
        get() = opponents.any { it.currentAction != null }

    var numActionsPlayed = 0

    var durationCards = mutableListOf<Card>()

    val durationCardsString: String
        get() = durationCards.groupedString

    var nativeVillageCards = mutableListOf<Card>()

    var islandCards = mutableListOf<Card>()

    val islandCardsString: String
        get() = islandCards.groupedString

    var pirateShipCoins: Int = 0
        set(value) {
            field = value
            refreshPlayerHandArea()
        }

    var playedCrossroadsThisTurn: Boolean = false

    var finishEndTurnAfterResolvingActions: Boolean = false

    var cardsUnavailableToBuyThisTurn = mutableListOf<Card>()

    val cardsSetAsideUntilStartOfTurn = mutableSetOf<SetAsideUntilStartOfTurnCard>()

    val cardsSetAsideToReturnToSupplyAtStartOfCleanup = mutableListOf<Card>()

    val tavernCards = mutableListOf<Card>()

    val tavernCardsString: String
        get() = tavernCards.groupedString

    var isJourneyTokenFaceUp: Boolean = true

    var isMinusCoinTokenInFrontOfPlayer = false

    var isMinusCardTokenOnDeck = false

    var plusCardTokenSupplyPile: String? = null
    var plusActionTokenSupplyPile: String? = null
    var plusBuyTokenSupplyPile: String? = null
    var plusCoinTokenSupplyPile: String? = null

    var minusTwoCostTokenSupplyPile: String? = null

    var trashingTokenSupplyPile: String? = null

    val supplyPilesWithBonusTokens: List<String>
        get() {
            val piles = mutableListOf<String>()
            plusCardTokenSupplyPile?.let { piles.add(it) }
            plusActionTokenSupplyPile?.let { piles.add(it) }
            plusBuyTokenSupplyPile?.let { piles.add(it) }
            plusCoinTokenSupplyPile?.let { piles.add(it) }
            minusTwoCostTokenSupplyPile?.let { piles.add(it) }
            trashingTokenSupplyPile?.let { piles.add(it) }
            return piles
        }

    var cardToPutIntoHandAfterDrawingCardsAtEndOfTurn: Card? = null

    var numExtraCardsToDrawAtEndOfTurn: Int = 0

    var inheritanceActionCard: Card? = null

    val playerToLeft: Player
        get() = game.getPlayerToLeft(this)

    var nextActionEnchanted: Boolean = false

    var isMoneyDoubledThisTurn: Boolean = false

    @Suppress("MemberVisibilityCanBePrivate")
    val pointsFromLandmarks: Int
        get() = game.landmarks.filterIsInstance<VictoryPointsCalculator>()
                .sumBy { it.calculatePoints(this) }

    val currentTurnCardTrashedListeners = mutableListOf<AfterCardTrashedListener>()

    var isUsedHornThisTurn: Boolean = false

    var sinisterPlotTokens: Int = 0

    var ignoreAddActionsUntilEndOfTurn: Boolean = false

    init {
        if (game.isIdenticalStartingHands && game.players.size > 0) {
            val firstPlayer = game.players[0]
            deck.addAll(firstPlayer.deck)
            for (card in firstPlayer.hand) {
                addCardToHand(card)
            }
        } else {
            repeat(7) {
                deck.add(Copper())
            }

            if (game.isIncludeShelters) {
                deck.add(Hovel())
                deck.add(Necropolis())
                deck.add(OvergrownEstate())
            } else {
                repeat(3) {
                    deck.add(Estate())
                }
            }

            deck.shuffle()

            drawCards(5)
        }
    }

    fun drawCard() {
        drawCards(1)
    }

    fun drawCards(numCards: Int): List<Card> {
        if (numCards == 0) {
            return emptyList()
        }
        val cards = getCardsFromDeck(numCards)
        cards.forEach { addCardToHand(it) }
        return cards
    }

    private fun getCardsFromDeck(numCards: Int): List<Card> {
        if (numCards == 0) {
            return ArrayList()
        }

        if (isMinusCardTokenOnDeck) {
            addEventLogWithUsername(" used -1 Card token")
            isMinusCardTokenOnDeck = false
            return getCardsFromDeck(numCards - 1)
        }

        val cardsDrawn = ArrayList<Card>()
        var log = "drawing $numCards"
        log += if (numCards == 1) {
            " card"
        } else {
            " cards"
        }
        addInfoLogWithUsername(log)

        for (i in 0 until numCards) {
            if (deck.isEmpty()) {
                if (discard.isEmpty()) {
                    break
                }
                shuffleDiscardIntoDeck()
            }

            if (!deck.isEmpty()) {
                val cardToDraw = deck.removeAt(0)
                cardsDrawn.add(cardToDraw)
            }
        }

        return cardsDrawn
    }

    fun shuffleDiscardIntoDeck() {
        deck.addAll(discard)
        discard.clear()
        addInfoLogWithUsername("shuffling deck")
        deck.shuffle()
        shuffles++

        projectsBought.filterIsInstance<AfterShuffleListener>()
                .forEach { it.afterShuffle(this) }
    }

    fun shuffleHandIntoDeck() {
        deck.addAll(hand)
        hand.clear()
        addInfoLogWithUsername("shuffling hand into deck")
        deck.shuffle()
        shuffles++

        projectsBought.filterIsInstance<AfterShuffleListener>()
                .forEach { it.afterShuffle(this) }
    }

    fun cardRemovedFromPlay(card: Card, removedToLocation: CardLocation) {
        card.removedFromPlay(this)

        if (card.isVictory) {
            game.refreshPlayers()
        }

        if (removedToLocation != CardLocation.Tavern && (card.isCardActuallyBandOfMisfits || card.isCardActuallyOverlord)) {

            val actualCard: Card = if (card.isCardActuallyOverlord) Overlord() else BandOfMisfits()

            card.isCardActuallyBandOfMisfits = false
            card.isCardActuallyOverlord = false

            when {
                cardsInDiscard.contains(card) -> {
                    removeCardFromDiscard(card)
                    addCardToDiscard(actualCard)
                }
                hand.contains(card) -> {
                    removeCardFromHand(card)
                    addCardToHand(actualCard)
                }
                deck.contains(card) -> {
                    deck.replaceAll { deckCard ->
                        if (deckCard.id == card.id) {
                            actualCard
                        } else {
                            deckCard
                        }
                    }
                }
                islandCards.contains(card) -> {
                    islandCards.remove(card)
                    islandCards.add(actualCard)
                }
                nativeVillageCards.contains(card) -> {
                    nativeVillageCards.remove(card)
                    nativeVillageCards.add(actualCard)
                }
                game.trashedCards.contains(card) -> {
                    game.trashedCards.remove(card)
                    game.trashedCards.add(actualCard)
                }
            }
        }
    }

    fun addCoins(coins: Int, refresh: Boolean = true) {
        if (coins == 0) {
            return
        }

        if (isMinusCoinTokenInFrontOfPlayer) {
            addEventLogWithUsername(" used -\$1 token")
            isMinusCoinTokenInFrontOfPlayer = false
            this.coins += coins - 1
        } else {
            this.coins += coins
        }

        coinsGainedThisTurn += coins

        if (refresh) {
            refreshSupply()
            game.refreshCoins()
        }
    }

    fun addDebt(debt: Int, refresh: Boolean = true) {
        this.debt += debt

        if (refresh) {
            refreshSupply()
            game.refreshDebt()
        }
    }

    fun addVictoryCoins(victoryCoins: Int, showLog: Boolean = false) {
        if (victoryCoins == 0) {
            return
        }
        this.victoryCoins += victoryCoins

        if (showLog) {
            addEventLogWithUsername("gained +$victoryCoins VP")
        }

        refreshPlayerHandArea()
        game.refreshPlayers()
    }

    fun addActions(actions: Int, refresh: Boolean = true) {
        if (actions == 0 || !isYourTurn) {
            return
        }
        if (actions > 0 && ignoreAddActionsUntilEndOfTurn) {
            return
        }
        this.actions += actions
        if (refresh) {
            game.refreshPlayerCardsPlayed(this)
        }
    }

    fun addBuys(buys: Int, refresh: Boolean = true) {
        if (buys == 0 || !isYourTurn) {
            return
        }

        this.buys += buys

        if (refresh) {
            game.refreshBuys()
        }
    }

    fun endTurn(isAutoEnd: Boolean = false) {

        cardsPlayed.filterIsInstance<StartOfCleanupListener>()
                .forEach { it.onStartOfCleanup(this) }

        projectsBought.filterIsInstance<StartOfCleanupListener>()
                .forEach { it.onStartOfCleanup(this) }

        resolveActions()

        if (currentAction != null) {
            return
        }

        addInfoLogWithUsername("ending turn")

        val durationCardsToDiscard = durationCards.filterNot {
            (it is MultipleTurnDuration && it.keepAtEndOfTurn(this)
                    || (it is CardRepeater && it.cardBeingRepeated is MultipleTurnDuration && (it.cardBeingRepeated as MultipleTurnDuration).keepAtEndOfTurn(this)))
                    || (it is NextTurnRepeater && it.keepAtEndOfTurn(this))
        }

        durationCardsToDiscard.forEach {
            it.durationCardCopiedByCitadel = false
            addCardToDiscard(it, false, false)
        }

        durationCards.removeAll(durationCardsToDiscard)

        for (card in inPlay) {
            if ((card.isDuration && (card !is ConditionalDuration || card.isKeepAtEndOfTurn)) || (card is CardRepeater && card.cardBeingRepeated?.isDuration == true)) {
                durationCards.add(card)

                card.isSelected = false
                card.isHighlighted = false

                if (card is AfterCardAddedToDurationListenerForSelf) {
                    (card as AfterCardAddedToDurationListenerForSelf).afterCardAddedToDuration(this)
                }
            } else {
                addCardToDiscard(card, false, false)

                if (card is CardDiscardedFromPlayListener) {
                    card.onCardDiscarded(this)
                }

                if (card.addedAbilityCard is CardDiscardedFromPlayListener) {
                    (card.addedAbilityCard as CardDiscardedFromPlayListener).onCardDiscarded(this)
                }
            }
        }

        inPlay.clear()

        hand.forEach {
            addCardToDiscard(it, false, false)
        }

        hand.clear()

        durationCards.filterIsInstance<TurnEndedListenerForDurationCards>()
                .forEach { it.onTurnEnded(this) }

        eventsBought.filterIsInstance<TurnEndedListenerForEventsBought>()
                .forEach { it.onTurnEnded(this) }

        game.landmarks.filterIsInstance<TurnEndedListenerForLandmark>()
                .forEach { it.onTurnEnded(this) }

        cardsPlayed.filterIsInstance<TurnEndedListenerForCardsPlayedThisTurn>()
                .forEach { it.onTurnEnded(this) }

        resolveActions()

        if (currentAction != null) {
            finishEndTurnAfterResolvingActions = true
            return
        }

        finishEndTurn(isAutoEnd)
    }

    private fun finishEndTurn(isAutoEnd: Boolean = false) {

        finishEndTurnAfterResolvingActions = false

        ignoreAddActionsUntilEndOfTurn = false

        payOffDebt(false)

        cardsSetAsideToReturnToSupplyAtStartOfCleanup.forEach {
            game.returnCardToSupply(it)
        }

        cardsSetAsideToReturnToSupplyAtStartOfCleanup.clear()

        lastTurnSummary = currentTurnSummary

        currentTurnSummary = TurnSummary(username)

        game.refreshHistory()

        isActionTakenInBuyPhase = false

        isReturnToActionPhase = false

        isTreasuresPlayable = true

        turns++

        coins = 0
        coinsSpent = 0
        actions = 0
        buys = 0

        numActionsPlayed = 0

        nextActionEnchanted = false

        playedCrossroadsThisTurn = false

        isMoneyDoubledThisTurn = false

        isUsedHornThisTurn = false

        isNextCardToTopOfDeck = false
        isNextCardToHand = false

        coinsGainedThisTurn = 0

        isPaidOffDebtThisTurn = false

        currentTurnCardTrashedListeners.clear()

        cardsUnavailableToBuyThisTurn.clear()

        cardsGained.clear()
        cardsBought.clear()
        cardsPlayed.clear()

        eventsBought.clear()

        drawCards(5 + numExtraCardsToDrawAtEndOfTurn)

        numExtraCardsToDrawAtEndOfTurn = 0

        if (hasArtifact(Flag.NAME)) {
            drawCard()
            addEventLogWithUsername("Gained +1 Card from ${Flag().cardNameWithBackgroundColor}")
        }

        if (cardToPutIntoHandAfterDrawingCardsAtEndOfTurn != null) {
            addCardToHand(cardToPutIntoHandAfterDrawingCardsAtEndOfTurn!!)
            cardToPutIntoHandAfterDrawingCardsAtEndOfTurn = null
        }

        isYourTurn = false

        game.turnEnded(isAutoEnd)
    }

    abstract fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, info: Any? = null)
    abstract fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)
    abstract fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String = "", cardActionableExpression: ((card: Card) -> Boolean)? = null, info: Any? = null)
    abstract fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null, info: Any? = null)

    abstract fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    fun exchangeDiscardedCard(discardedCard: Card, exchangeToCard: Card) {
        removeCardFromDiscard(discardedCard)
        game.exchangeCardInSupply(discardedCard, exchangeToCard)
        addCardToDiscard(exchangeToCard)
        addEventLogWithUsername("Exchanged ${discardedCard.cardNameWithBackgroundColor} for ${exchangeToCard.cardNameWithBackgroundColor}")
    }

    fun trashCardFromDiscard(card: Card) {
        addEventLogWithUsername("trashed " + card.cardNameWithBackgroundColor + " from discard")

        discard.remove(card)

        cardTrashed(card)

        refreshPlayerHandArea()
    }

    fun trashCardFromHand(card: Card) {
        addEventLogWithUsername("trashed " + card.cardNameWithBackgroundColor + " from hand")

        hand.remove(card)

        cardTrashed(card)

        refreshPlayerHandArea()
    }

    fun trashHand() {
        if (hand.isEmpty()) {
            addEventLogWithUsername("trashed an empty hand")
            return
        }

        val handCopy = hand.toMutableList()

        hand.clear()

        handCopy.forEach { cardTrashed(it) }

        addEventLogWithUsername("trashed their hand: ${hand.groupedString}")

        refreshPlayerHandArea()
    }

    fun trashCardFromSupply(card: Card) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)
            addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor} from supply")

            cardTrashed(card, showLog = false, trashedCardFromSupply = true)
        }
    }

    fun cardTrashed(card: Card, showLog: Boolean = false, trashedCardFromSupply: Boolean = false) {
        val cardToTrash = if (card is InheritanceEstate) Estate() else card

        if (showLog) {
            addEventLogWithUsername("trashed ${cardToTrash.cardNameWithBackgroundColor}")
        }

        game.trashedCards.add(cardToTrash)

        if (!trashedCardFromSupply) {
            cardRemovedFromPlay(cardToTrash, CardLocation.Trash)
        }

        if (isYourTurn) {
            currentTurnSummary.trashedCards.add(cardToTrash)
        }

        if (cardToTrash is AfterCardTrashedListenerForSelf) {
            cardToTrash.afterCardTrashed(this)
        }

        if (cardToTrash.addedAbilityCard is AfterCardTrashedListenerForSelf) {
            (cardToTrash.addedAbilityCard as AfterCardTrashedListenerForSelf).afterCardTrashed(this)
        }

        if (!trashedCardFromSupply) {
            (hand.filterIsInstance<AfterOwnedCardTrashedListenerForCardsInHand>() +
                    hand.mapNotNull { it.addedAbilityCard }.filterIsInstance<AfterOwnedCardTrashedListenerForCardsInHand>())
                    .forEach { it.afterCardTrashed(cardToTrash, this) }
        }

        game.landmarks.filterIsInstance<AfterCardTrashedListener>()
                .forEach { it.afterCardTrashed(cardToTrash, this) }

        projectsBought.filterIsInstance<AfterCardTrashedListener>()
                .forEach { it.afterCardTrashed(cardToTrash, this) }

        currentTurnCardTrashedListeners.forEach { it.afterCardTrashed(cardToTrash, this) }
    }

    fun removeCardInPlay(card: Card, removedToLocation: CardLocation) {
        inPlay.remove(card)
        cardRemovedFromPlay(card, removedToLocation)
        game.refreshCardsPlayed()
    }

    fun removeDurationCardInPlay(card: Card, removedToLocation: CardLocation) {
        durationCards.remove(card)
        cardRemovedFromPlay(card, removedToLocation)
        game.refreshCardsPlayed()
    }

    fun trashCardInPlay(card: Card, showLog: Boolean = true) {
        if (showLog) {
            addEventLog("Trashed " + card.cardNameWithBackgroundColor + " from in play")
        }

        inPlay.remove(card)
        cardRemovedFromPlay(card, CardLocation.Trash)
        cardTrashed(card)
        game.refreshCardsPlayed()
    }

    fun gainCardToTopOfDeck(card: Card) {
        isNextCardToTopOfDeck = true
        cardGained(card)
        refreshPlayerHandArea()
    }

    fun gainCardToHand(card: Card) {
        isNextCardToHand = true
        cardGained(card)
        refreshPlayerHandArea()
    }

    fun addCardToTopOfDeck(card: Card, showLog: Boolean = true) {
        deck.add(0, card)
        if (showLog) {
            addEventLogWithUsername("added " + card.cardNameWithBackgroundColor + " to top of deck")
        }
        refreshPlayerHandArea()
    }

    fun cardGained(card: Card) {

        var gainCardHandled = false

        val cardToGain = if (card.isEstate && inheritanceActionCard != null) createInheritanceEstate() else if (card is InheritanceEstate) Estate() else card

        cardsGained.add(cardToGain)

        game.availableCards.filterIsInstance<CardGainedListenerForCardsAvailableInSupply>()
                .forEach {
                    it.onCardGained(cardToGain, this)
                }

        game.landmarks.filterIsInstance<CardGainedListenerForLandmark>()
                .forEach {
                    it.onCardGained(cardToGain, this)
                }

        val cardGainedListenersForCardsInHand = hand.filterIsInstance<CardGainedListenerForCardsInHand>() +
                hand.mapNotNull { it.addedAbilityCard }.filterIsInstance<CardGainedListenerForCardsInHand>()

        for (listener in cardGainedListenersForCardsInHand) {
            val handled = listener.onCardGained(cardToGain, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        val cardGainedListenersForCardsInPlay = inPlayWithDuration.filterIsInstance<CardGainedListenerForCardsInPlay>() +
                inPlayWithDuration.mapNotNull { it.addedAbilityCard }.filterIsInstance<CardGainedListenerForCardsInPlay>()

        for (listener in cardGainedListenersForCardsInPlay) {
            val handled = listener.onCardGained(cardToGain, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        val cardGainedListenersForEventsBought = eventsBought.filterIsInstance<CardGainedListenerForEventsBought>()
        for (listener in cardGainedListenersForEventsBought) {
            val handled = listener.onCardGained(cardToGain, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        if (gainCardHandled) {
            return
        }

        if (cardToGain is BeforeCardGainedListenerForSelf) {
            cardToGain.beforeCardGained(this)
        }

        if (cardToGain.addedAbilityCard is BeforeCardGainedListenerForSelf) {
            (cardToGain.addedAbilityCard as BeforeCardGainedListenerForSelf).beforeCardGained(this)
        }

        when {
            isNextCardToHand -> {
                isNextCardToHand = false
                addCardToHand(cardToGain)
            }
            isNextCardToTopOfDeck -> {
                isNextCardToTopOfDeck = false
                addCardToTopOfDeck(cardToGain)
            }
            else -> {
                addCardToDiscard(cardToGain)
            }
        }

        if (isYourTurn) {
            currentTurnSummary.cardsGained.add(cardToGain)
        }

        if (cardToGain is AfterCardGainedListenerForSelf) {
            cardToGain.afterCardGained(this)
        }

        if (cardToGain.addedAbilityCard is AfterCardGainedListenerForSelf) {
            (cardToGain.addedAbilityCard as AfterCardGainedListenerForSelf).afterCardGained(this)
        }

        (tavernCards.filterIsInstance<AfterCardGainedListenerForCardsInTavern>() +
                tavernCards.mapNotNull { it.addedAbilityCard }.filterIsInstance<AfterCardGainedListenerForCardsInTavern>())
                .forEach { it.afterCardGained(cardToGain, this) }

        (inPlayWithDuration.filterIsInstance<AfterCardGainedListenerForCardsInPlay>() +
                inPlayWithDuration.mapNotNull { it.addedAbilityCard }.filterIsInstance<AfterCardGainedListenerForCardsInPlay>())
                .forEach { it.afterCardGained(cardToGain, this) }

        game.landmarks.filterIsInstance<AfterCardGainedListener>()
                .forEach { it.afterCardGained(cardToGain, this) }

        projectsBought.filterIsInstance<AfterCardGainedListener>()
                .forEach { it.afterCardGained(cardToGain, this) }

        opponents.forEach { opponent ->
            opponent.projectsBought.filterIsInstance<AfterOtherPlayerCardGainedListenerForProjects>()
                    .forEach { it.afterCardGainedByOtherPlayer(cardToGain, opponent, this) }
        }

        if (cardToGain.isVictory || game.landmarks.any { it is VictoryPointsCalculator }) {
            game.refreshPlayers()
        }
    }

    abstract fun makeChoice(card: ChoiceActionCard, vararg choices: Choice)

    fun makeChoiceFromList(card: ChoiceActionCard, choices: List<Choice>) {
        makeChoice(card, *choices.toTypedArray())
    }

    abstract fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice)

    abstract fun makeChoiceWithInfo(card: ChoiceActionCard, text: String, info: Any, vararg choices: Choice)

    fun makeChoiceFromList(card: ChoiceActionCard, text: String, choices: List<Choice>) {
        makeChoice(card, text, *choices.toTypedArray())
    }

    fun makeChoiceFromListWithInfo(card: ChoiceActionCard, text: String, info: Any, choices: List<Choice>) {
        makeChoiceWithInfo(card, text, info, *choices.toTypedArray())
    }

    fun trashCardFromHand(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)? = null) {
        trashCardsFromHand(1, optional, cardActionableExpression)
    }

    abstract fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun trashCardFromSupply(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?)

    abstract fun gainCardFromTrash(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?)

    fun useLandmark(landmark: Landmark) {
        if (landmark.isLandmarkActionable(this)) {
            addEventLogWithUsername("used landmark: ${landmark.cardNameWithBackgroundColor}")

            landmark.cardPlayedSpecialAction(this)
        }
    }

    fun buyEvent(event: Event) {
        if (event.isEventActionable(this)) {
            addEventLogWithUsername("bought event: ${event.cardNameWithBackgroundColor}")
            coins -= event.cost
            debt += event.debtCost
            buys -= 1
            isReturnToActionPhase = false
            isTreasuresPlayable = false
            eventsBought.add(event)
            currentTurnSummary.eventsBought.add(event)
            game.refreshCardsBought()
            refreshSupply()
            event.cardPlayed(this)
        }
    }

    fun buyProject(project: Project) {
        if (project.isProjectActionable(this)) {
            addEventLogWithUsername("bought project: ${project.cardNameWithBackgroundColor}")
            coins -= project.cost
            debt += project.debtCost
            buys -= 1
            isReturnToActionPhase = false
            isTreasuresPlayable = false
            projectsBought.add(project)
            currentTurnSummary.projectsBought.add(project)
            game.refreshCardsBought()
            refreshSupply()
            project.cardPlayed(this)
        }
    }

    fun buyCard(card: Card) {
        if (debt == 0 && availableCoins >= this.getCardCostWithModifiers(card)) {
            addEventLogWithUsername("bought card: " + card.cardNameWithBackgroundColor)
            coins -= this.getCardCostWithModifiers(card)
            debt += card.debtCost

            val debtOnSupplyPile = game.getDebtOnSupplyPile(card.pileName)
            if (debtOnSupplyPile > 0) {
                debt += debtOnSupplyPile
                addEventLogWithUsername("gained $debtOnSupplyPile debt from ${card.pileName} pile")
                game.clearDebtFromSupplyPile(card.pileName)
            }

            buys -= 1
            isTreasuresPlayable = false

            if (!isBuyPhase) {
                handleBeforeBuyPhase()
            }

            cardsBought.add(card)
            currentTurnSummary.cardsBought.add(card)
            isReturnToActionPhase = false

            if (game.isShowEmbargoTokens) {
                val numEmbargoTokens = game.embargoTokens[card.name] ?: 0
                if (numEmbargoTokens > 0) {
                    for (i in 1..numEmbargoTokens) {
                        gainSupplyCard(Curse(), true)
                    }
                }
            }

            //if buys=0 then cards bought will be refreshed by previous player cards bought
            if (buys > 0) {
                game.removeCardFromSupply(card)
                game.refreshCardsBought()
            } else {
                game.removeCardFromSupply(card, false)
            }

            cardGained(card)

            if (trashingTokenSupplyPile == card.name) {
                optionallyTrashCardsFromHand(1, "")
            }

            if (card is AfterCardBoughtListenerForSelf) {
                card.afterCardBought(this)
            }

            if (card.addedAbilityCard is AfterCardBoughtListenerForSelf) {
                (card.addedAbilityCard as AfterCardBoughtListenerForSelf).afterCardBought(this)
            }

            (inPlayWithDuration.filterIsInstance<AfterCardBoughtListenerForCardsInPlay>() +
                    inPlayWithDuration.mapNotNull { it.addedAbilityCard }.filterIsInstance<AfterCardBoughtListenerForCardsInPlay>())
                    .forEach { it.afterCardBought(card, this) }

            (hand.filterIsInstance<AfterCardBoughtListenerForCardsInHand>() +
                    hand.mapNotNull { it.addedAbilityCard }.filterIsInstance<AfterCardBoughtListenerForCardsInHand>())
                    .forEach { it.afterCardBought(card, this) }

            game.landmarks.filterIsInstance<AfterCardBoughtListenerForLandmark>()
                    .forEach { it.afterCardBought(card, this) }
        }
    }

    val allCards: List<Card>
        get() {
            val cards = ArrayList<Card>()
            cards.addAll(hand)
            cards.addAll(deck)
            cards.addAll(discard)
            cards.addAll(inPlay)
            cards.addAll(nativeVillageCards)
            cards.addAll(islandCards)
            cards.addAll(durationCards)
            cards.addAll(tavernCards)
            inheritanceActionCard?.let { cards.add(it) }

            inPlayWithDuration.filterIsInstance<SetAsideCardsDuration>()
                    .forEach { it.setAsideCards?.let { setAsideCards -> cards.addAll(setAsideCards) } }

            return cards
        }

    val allCardsWithoutInPlay: List<Card>
        get() {
            val cards = ArrayList<Card>()
            cards.addAll(hand)
            cards.addAll(deck)
            cards.addAll(discard)

            return cards
        }

    fun playCard(card: Card, refresh: Boolean = true, repeatedAction: Boolean = false, showLog: Boolean = true) {

        if (showLog) {
            addEventLogWithUsername("played ${card.cardNameWithBackgroundColor}")
        }

        if (!isTreasureCardsPlayedInBuyPhase && card.isAction && card.isTreasure) {
            game.treasureCardsPlayedInActionPhase.add(card)
        } else if (card.isTreasure && !isBuyPhase) {
            handleBeforeBuyPhase()
        }

        cardsPlayed.add(card)

        currentTurnSummary.cardsPlayed.add(card)

        if (card.isTreasure) {
            isReturnToActionPhase = false
        }

        val listeners = inPlayWithDuration.filterIsInstance<CardPlayedListener>() +
                inPlayWithDuration.mapNotNull { it.addedAbilityCard }.filterIsInstance<CardPlayedListener>()

        listeners.forEach { it.onCardPlayed(card, this) }

        if (!repeatedAction) {

            inPlay.add(card)
            hand.remove(card)

            if (refresh) {
                refreshPlayerHandArea()
                game.refreshCardsPlayed()
            }
        }

        addTokenBonusesForPlayingCard(card, refresh)

        if (card.isAction) {
            numActionsPlayed++
        }

        game.availableCards.filterIsInstance<CardPlayedListenerForCardsAvailableInSupply>()
                .forEach { it.onCardPlayed(card, this) }

        projectsBought.filterIsInstance<CardPlayedListener>()
                .forEach { it.onCardPlayed(card, this) }

        val cardPlayedListenersForCardsInPlay = inPlayWithDuration.filterIsInstance<CardPlayedListenerForCardsInPlay>() +
                inPlayWithDuration.mapNotNull { it.addedAbilityCard }.filterIsInstance<CardPlayedListenerForCardsInPlay>()

        cardPlayedListenersForCardsInPlay.forEach {
            it.onCardPlayed(card, this)
        }

        card.cardPlayed(this, refresh)
    }

    private fun handleBeforeBuyPhase() {
        game.allCards.filterIsInstance<BeforeBuyPhaseListenerForCardsInSupply>()
                .forEach { it.beforeBuyPhase(this) }
    }

    fun addTokenBonusesForPlayingCard(card: Card, refresh: Boolean) {
        if (card.pileName == plusActionTokenSupplyPile) {
            addActions(1, refresh)
        }

        if (card.pileName == plusBuyTokenSupplyPile) {
            addBuys(1, refresh)
        }

        if (card.pileName == plusCoinTokenSupplyPile) {
            addCoins(1, refresh)
        }

        if (card.pileName == plusCardTokenSupplyPile) {
            drawCard()
        }
    }

    private fun countCardsByType(cards: List<Card>, typeMatcher: Function<Card, Boolean>): Int {
        return cards.filter({ typeMatcher.apply(it) }).count()
    }

    private val currentDeckNumber: Int
        get() = shuffles + 1

    abstract fun chooseSupplyCardToGain(cardActionableExpression: ((card: Card) -> Boolean)? = null, text: String? = null, destination: CardLocation = CardLocation.Discard, optional: Boolean = false)

    fun chooseSupplyCardToGainWithMaxCost(maxCost: Int, cardActionableExpression: ((card: Card) -> Boolean)? = null, text: String? = "Gain a free card from the supply costing up to $maxCost", destination: CardLocation = CardLocation.Discard) {
        chooseSupplyCardToGain({ c -> c.debtCost == 0 && getCardCostWithModifiers(c) <= maxCost && (cardActionableExpression == null || cardActionableExpression(c)) }, text, destination)
    }

    fun chooseSupplyCardToGainWithExactCost(cost: Int, text: String? = "Gain a free card from the supply costing $cost", destination: CardLocation = CardLocation.Discard, optional: Boolean = false) {
        chooseSupplyCardToGain({ c -> c.debtCost == 0 && getCardCostWithModifiers(c) == cost }, text, destination, optional)
    }

    fun chooseSupplyCardToGainToTopOfDeck(maxCost: Int) {
        chooseSupplyCardToGain({ c -> c.debtCost == 0 && getCardCostWithModifiers(c) <= maxCost }, "Gain a free card from the supply to the top of your deck costing up to $maxCost")
    }

    fun chooseSupplyCardToGainToHandWithMaxCost(maxCost: Int) {
        chooseSupplyCardToGainWithMaxCost(maxCost, null, "Gain a free card from the supply to your hand costing up to $maxCost", CardLocation.Hand)
    }

    fun chooseSupplyCardToGainToHandWithMaxCostAndType(maxCost: Int, cardType: CardType) {
        chooseSupplyCardToGainWithMaxCost(maxCost, { c -> c.type == cardType }, "Gain a free card from the supply to your hand costing up to $maxCost", CardLocation.Hand)
    }

    abstract fun chooseSupplyCardToGainForBenefit(text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    fun chooseSupplyCardToGainForBenefitWithMaxCost(maxCost: Int, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null) {
        chooseSupplyCardToGainForBenefit(text, freeCardFromSupplyForBenefitActionCard) { c -> c.debtCost == 0 && getCardCostWithModifiers(c) <= maxCost && (cardActionableExpression == null || cardActionableExpression(c)) }
    }

    fun chooseSupplyCardToGainForBenefitWithExactCost(exactCost: Int, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null) {
        chooseSupplyCardToGainForBenefit(text, freeCardFromSupplyForBenefitActionCard) { c -> c.debtCost == 0 && getCardCostWithModifiers(c) == exactCost && (cardActionableExpression == null || cardActionableExpression(c)) }
    }

    abstract fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int)

    abstract fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String, info: Any? = null)

    fun gainCardNotInSupply(card: Card) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)

            cardGained(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor}")
        }
    }

    fun gainRuins(): Card? {
        return if (game.ruinsPile.isNotEmpty()) {
            val card = game.ruinsPile.removeAt(0)
            cardGained(card)
            game.refreshSupply()
            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the Ruins pile")
            card
        } else {
            null
        }
    }

    fun gainSpoils() {
        gainCardNotInSupply(Spoils())
    }

    fun gainSupplyCard(card: Card, showLog: Boolean = false, destination: CardLocation = CardLocation.Discard) {

        //create copy of card so that it doesn't affect card chosen in case it came from somewhere other than the supply
        val supplyCard = game.getNewInstanceOfCard(card.name)

        if (game.isCardAvailableInSupply(supplyCard)) {

            game.removeCardFromSupply(supplyCard)

            var log = "gained ${supplyCard.cardNameWithBackgroundColor} from the supply"

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (destination) {
                CardLocation.Hand -> {
                    isNextCardToHand = true
                    log += " to their hand"
                }
                CardLocation.Deck -> {
                    isNextCardToTopOfDeck = true
                    log += " to the top of their deck"
                }
            }

            if (showLog) {
                addEventLogWithUsername(log)
            }

            cardGained(supplyCard)
        }
    }

    fun gainSupplyCardToHand(card: Card, showLog: Boolean) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)
            gainCardToHand(card)

            if (showLog) {
                addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} to their hand")
            }
        }
    }

    fun gainSupplyCardToTopOfDeck(card: Card, showLog: Boolean) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)
            gainCardToTopOfDeck(card)

            if (showLog) {
                addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} to the top of their deck")
            }
        }
    }

    fun cardPlayedThisTurn(typeMatcher: Function<Card, Boolean>): Boolean {
        return countCardsByType(cardsPlayed, typeMatcher) > 0
    }

    val handAndDeck: List<Card>
        get() {
            val cards = ArrayList(hand)
            cards.addAll(deck)
            return cards
        }

    fun addEventLog(log: String) {
        game.addEventLog(log)
    }

    fun addEventLogWithUsername(log: String) {
        game.addEventLog("$username $log")
    }

    fun addInfoLog(log: String) {
        game.addInfoLog(log)
    }

    fun addInfoLogWithUsername(log: String) {
        game.addInfoLog("$username $log")
    }

    fun addCardsToDiscard(cards: List<Card>, showLog: Boolean = false) {
        cards.forEach { addCardToDiscard(it, refresh = false) }
        if (showLog) {
            addEventLogWithUsername("discarded ${cards.groupedString}")
        }
        refreshPlayerHandArea()
    }

    fun addCardToDiscard(card: Card, refresh: Boolean = true, showLog: Boolean = false) {
        if (showLog) {
            addEventLogWithUsername("discarded ${card.cardNameWithBackgroundColor}")
        }

        card.isHighlighted = false
        card.isSelected = false

        discard.add(card)

        cardRemovedFromPlay(card, CardLocation.Discard)

        if (refresh) {
            refreshPlayerHandArea()
        }
    }

    fun discardCardFromHand() {
        discardCardsFromHand(1, false)
    }

    fun discardCardFromHand(card: Card, showLog: Boolean = true, refresh: Boolean = true) {
        hand.remove(card)

        addCardToDiscard(card, refresh)

        if (showLog) {
            addEventLogWithUsername(" discarded ${card.cardNameWithBackgroundColor} from hand")
        }
    }

    abstract fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean)

    abstract fun waitForOtherPlayersToResolveActions()

    abstract fun waitForOtherPlayersForResolveAttack(attackCard: Card, info: Any?)

    abstract fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler)

    fun resolveActions() {
        if (currentAction == null && !actionsQueue.isEmpty()) {
            val action = if (actionsQueue.size > 1 && actionsQueue.count { it is RepeatCardAction } < actionsQueue.size) {
                actionsQueue.removeAt(actionsQueue.indexOfFirst { it !is RepeatCardAction })
            } else {
                actionsQueue.removeAt(0)
            }

            processNextAction(action)
        }
    }

    @Suppress("CascadeIf")
    private fun processNextAction(action: Action) {
        if (action is SelfResolvingAction) {
            action.resolveAction(this)
            resolveActions()
        } else if (action.processAction(this)) {
            currentAction = action
            game.refreshPlayerCardAction(this)
            refreshPlayerHandArea()
            refreshSupply()
        } else {
            action.onNotUsed(this)
            resolveActions()
        }
    }

    fun isCardBuyable(card: Card): Boolean {
        return isYourTurn && canBuyCard(card)
    }

    fun addCardsToDeck(cards: List<Card>) {
        deck.addAll(cards)
        refreshPlayerHandArea()
    }

    fun addCardsToTopOfDeck(cards: List<Card>) {
        deck.addAll(0, cards)
        refreshPlayerHandArea()
    }

    fun addCardToDeck(card: Card) {
        deck.add(card)
        refreshPlayerHandArea()
    }

    fun addCardToHand(card: Card, showLog: Boolean = false) {
        hand.add(card)

        if (showLog) {
            addEventLogWithUsername("added " + card.cardNameWithBackgroundColor + " to hand")
        }

        refreshPlayerHandArea()
    }

    fun addCardsToHand(cards: List<Card>, showLog: Boolean = false) {
        if (cards.isEmpty()) {
            return
        }

        hand.addAll(cards)

        if (showLog) {
            addEventLogWithUsername("added ${cards.groupedString} to hand")
        }

        refreshPlayerHandArea()
    }

    fun setup() {
        deck.shuffle()
        if (isFirstPlayer) {
            drawCards(3)
        } else {
            drawCards(5)
        }
    }

    fun actionResult(action: Action, result: ActionResult) {
        if (result.selectedCard != null || result.choiceSelected != null
                || result.isDoNotUse || result.isDoneWithAction) {
            if (result.isDoNotUse || action.processActionResult(this, result)) {

                if (result.isDoNotUse) {
                    currentAction!!.onNotUsed(this)
                }
                currentAction = null

                resolveActions()

                for (opponent in opponentsInOrder) {
                    if (opponent.currentAction is WaitForOtherPlayersActions) {
                        if (opponent.currentAction!!.processActionResult(this, result)) {
                            opponent.currentAction = null
                            opponent.game.refreshPlayerCardAction(opponent)
                            opponent.resolveActions()
                        }
                    }
                }

                if (game.currentPlayer.isBot && !game.currentPlayer.isOpponentHasAction) {
                    val botPlayer = game.currentPlayer as BotPlayer
                    if (botPlayer.isWaitingForPlayers) {
                        botPlayer.isWaitingForPlayers = false
                    }
                }

                if (game.currentPlayer.isOpponentHasAction) {
                    game.currentPlayer.waitForOtherPlayersToResolveActions()
                }

                if (currentAction == null && finishEndTurnAfterResolvingActions) {
                    finishEndTurn()
                }

                if (currentAction == null && isYourTurn && buys == 0) {
                    endTurn(true)
                }
            }
        }
    }

    fun playAll() {
        val copyOfHand = ArrayList(hand)
        copyOfHand.forEach({ this.playCard(it) })
    }

    fun startTurn(refreshPreviousPlayerCardsBought: Boolean) {
        isWaitingForComputer = false
        actions = 1
        buys = 1
        isYourTurn = true
        turn++

        if (refreshPreviousPlayerCardsBought) {
            val previousPlayer = game.previousPlayer!!

            if (!previousPlayer.isBot) {
                game.refreshPlayerGame(previousPlayer)
            }

            game.humanPlayers.filterNot { it.userId == game.previousPlayerId }.forEach { player ->
                game.refreshPreviousPlayerCardsBought(player)
                Thread {
                    Thread.sleep(2000)
                    game.refreshPlayerGame(player)
                }.start()
            }
        } else {
            game.refreshGame()
        }

        if (game.currentPlayer.isBot) {
            Thread.sleep(2500)
        }

        addInfoLog("Deck: $currentDeckNumber")

        currentTurnSummary = TurnSummary(username)
        currentTurnSummary.gameTurn = game.turn

        cardsSetAsideUntilStartOfTurn.forEach {
            it.onStartOfTurn(this)
        }

        cardsSetAsideUntilStartOfTurn.clear()

        durationCards.forEach { card ->
            when {
                card is StartOfTurnDurationAction -> {
                    card.durationStartOfTurnAction(this)
                    if (card.durationCardCopiedByCitadel) {
                        addEventLog("${Citadel().cardNameWithBackgroundColor} is repeating duration start of turn action for ${card.cardNameWithBackgroundColor}")
                        card.durationStartOfTurnAction(this)
                    }
                }
                card.addedAbilityCard is StartOfTurnDurationAction -> {
                    val durationCard = card.addedAbilityCard as StartOfTurnDurationAction
                    durationCard.durationStartOfTurnAction(this)
                }
                card is CardRepeater && card.cardBeingRepeated is StartOfTurnDurationAction -> {
                    val durationCard = card.cardBeingRepeated as StartOfTurnDurationAction

                    repeat(card.timesRepeated) {
                        durationCard.durationStartOfTurnAction(this)
                    }
                }
            }
        }

        projectsBought.filterIsInstance<StartOfTurnProject>()
                .forEach { it.onStartOfTurn(this) }

        game.allCards.filterIsInstance<TurnStartedListenerForCardsInSupply>()
                .forEach { it.turnStarted(this) }

        tavernCards.filterIsInstance<StartOfTurnTavernCard>()
                .filter { (it as TavernCard).isTavernCardActionable(this) }
                .forEach { it.onStartOfTurn(this) }

        resolveActions()

        takeTurn()
    }

    abstract fun takeTurn()

    fun addRepeatCardAction(card: Card) {
        actionsQueue.add(RepeatCardAction(card))
    }

    abstract fun addCardFromDiscardToTopOfDeck(optional: Boolean, maxCost: Int? = null)

    abstract fun addCardFromDiscardToHand()

    abstract fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)? = null, chooseCardActionCard: ChooseCardActionCard? = null)

    val isBot: Boolean = this is BotPlayer

    override fun hashCode(): Int {
        return Objects.hash(userId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val otherPlayer = other as Player
        return this.userId == otherPlayer.userId
    }

    fun revealHand() {
        addEventLogWithUsername("revealed their hand: ${hand.groupedString}")

        hand.filterIsInstance<AfterCardRevealedListenerForSelf>()
                .forEach { it.afterCardRevealed(this) }
    }

    fun revealCardFromHand(card: Card) {
        addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor} from their hand")

        if (card is AfterCardRevealedListenerForSelf) {
            card.afterCardRevealed(this)
        }
    }

    fun revealTopCardOfDeck(): Card? {
        val card = revealTopCardsOfDeck(1).firstOrNull()

        if (card is AfterCardRevealedListenerForSelf) {
            card.afterCardRevealed(this)
        }

        return card
    }

    fun revealTopCardsOfDeck(cards: Int): List<Card> {
        val revealedCards = ArrayList<Card>()

        if (deck.size < cards) {
            shuffleDiscardIntoDeck()
        }

        if (deck.isEmpty()) {
            addEventLogWithUsername("$username had no cards to reveal")
        } else {
            for (i in 0 until cards) {
                if (deck.size < i + 1 && discard.isNotEmpty()) {
                    shuffleDiscardIntoDeck()
                }
                if (deck.size < i + 1) {
                    addInfoLog("No more cards to reveal")
                } else {
                    val card = deck[i]
                    addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor} from top of deck")
                    revealedCards.add(card)
                }
            }
        }

        revealedCards.filterIsInstance<AfterCardRevealedListenerForSelf>()
                .forEach { it.afterCardRevealed(this) }

        return revealedCards
    }

    val cardOnTopOfDeck: Card?
        get() {
            if (deck.isEmpty()) {
                shuffleDiscardIntoDeck()
            }

            if (deck.isEmpty()) {
                return null
            }

            return deck.first()
        }

    fun removeTopCardOfDeck(): Card? {
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck()
        }

        if (deck.isEmpty()) {
            return null
        }

        val card = deck.removeAt(0)

        refreshPlayerHandArea()

        return card
    }

    fun removeTopCardsOfDeck(numCardsToRemove: Int, revealCards: Boolean = false): List<Card> {
        val cards = mutableListOf<Card>()

        repeat(numCardsToRemove) {
            val topDeckCard = removeTopCardOfDeck()
            topDeckCard?.let { cards.add(it) }
        }

        if (cards.isNotEmpty() && revealCards) {
            showInfoMessage("Revealed ${cards.groupedString}")
            addEventLogWithUsername("revealed ${cards.groupedString} from top of deck")
        }

        if (revealCards) {
            cards.filterIsInstance<AfterCardRevealedListenerForSelf>()
                    .forEach { it.afterCardRevealed(this) }
        }

        return cards
    }

    fun removeCardFromDeck(card: Card) {
        deck.remove(card)
        refreshPlayerHandArea()
    }

    fun removeCardsFromDiscard(cards: List<Card>) {
        cards.forEach { discard.remove(it) }
        refreshPlayerHandArea()
    }

    fun removeCardFromDiscard(card: Card) {
        discard.remove(card)
        refreshPlayerHandArea()
    }

    fun removeCardFromHand(card: Card, refresh: Boolean = true) {
        hand.remove(card)

        if (refresh) {
            refreshPlayerHandArea()
        }
    }

    fun removeCardsFromHand(cards: List<Card>) {
        cards.forEach { removeCardFromHand(it, false) }

        refreshPlayerHandArea()
    }

    val cardOnTopOfDiscard: Card?
        get() {
            if (!discard.isEmpty()) {
                return discard.last()
            }
            return null
        }

    open val infoForGameLogName: String
        get() = username.replace("\\s".toRegex(), "_")

    fun getCardCostWithModifiers(card: Card): Int {
        var cost = card.cost

        game.cardCostModifiers.forEach { cost += it.getChangeToCardCost(card, this) }

        if (game.currentPlayer.minusTwoCostTokenSupplyPile == card.name) {
            cost -= 2
        }

        if (cost < 0) {
            cost = 0
        }

        return cost
    }

    fun discardTopCardOfDeck(): Card? {
        val topCardOfDeck = removeTopCardOfDeck()

        if (topCardOfDeck != null) {
            addCardToDiscard(topCardOfDeck, showLog = true)
        }

        return topCardOfDeck
    }

    fun getVictoryPoints(gameOver: Boolean): Int {
        if (finalPointsCalculated) {
            return finalVictoryPoints
        }

        var victoryPoints = 0

        val allCards = allCards

        val cardNames = HashSet<String>()

        for (card in allCards) {
            cardNames.add(card.name)

            if (card.isVictory) {
                victoryPoints += if (card is VictoryPointsCalculator) {
                    card.calculatePoints(this)
                } else {
                    card.victoryPoints
                }
            } else if (card.isCurse) {
                victoryPoints += card.victoryPoints
            }
        }

        victoryPoints += victoryCoins

        victoryPoints += pointsFromLandmarks

        if (gameOver) {
            finalPointsCalculated = true
            finalVictoryPoints = victoryPoints
        }

        finalCards = allCards

        return victoryPoints
    }

    abstract fun putCardsOnTopOfDeckInAnyOrder(cards: List<Card>)

    fun cardCountByName(cardName: String): Int {
        return allCards.count { it.name == cardName }
    }

    fun cardCountByExpression(expression: ((card: Card) -> Boolean)): Int {
        return allCards.count { expression(it) }
    }

    fun canBuyCard(card: Card): Boolean {
        if (debt > 0) {
            return false
        }

        if (cardsUnavailableToBuyThisTurn.any { it.name == card.name }) {
            return false
        }

        val cost = getCardCostWithModifiers(card)
        return game.isCardAvailableInSupply(card) && availableCoins >= cost
    }

    abstract fun chooseCardForOpponentToGain(cost: Int, text: String, destination: CardLocation, opponent: Player)

    abstract fun chooseCardFromHand(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseCardFromHandOptional(text: String, chooseCardActionCard: ChooseCardActionCardOptional, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseCardsFromHand(text: String, numToChoose: Int, optional: Boolean, chooseCardsActionCard: ChooseCardsActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null, info: Any? = null, allowDoNotUse: Boolean = true)

    abstract fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null, info: Any? = null, choosingEmptyPilesAllowed: Boolean = true)

    fun triggerAttack(attackCard: Card, info: Any? = null) {

        opponentsInOrder.forEach { opponent ->
            val handBeforeAttackListeners = opponent.hand.filterIsInstance<HandBeforeAttackListener>() +
                    opponent.hand.mapNotNull { it.addedAbilityCard }.filterIsInstance<HandBeforeAttackListener>()

            handBeforeAttackListeners.forEach { it.onBeforeAttack(attackCard, opponent, this) }

            val durationBeforeAttackListeners = opponent.durationCards.filterIsInstance<DurationBeforeAttackListener>() +
                    opponent.durationCards.mapNotNull { it.addedAbilityCard }.filterIsInstance<DurationBeforeAttackListener>()

            durationBeforeAttackListeners.forEach { it.onBeforeAttack(attackCard, opponent, this) }
        }

        if (isOpponentHasAction) {
            waitForOtherPlayersForResolveAttack(attackCard, info)
        } else {
            val attackResolver = attackCard as AttackCard
            val affectedOpponents = opponentsInOrder.filterNot { attackCard.playersExcludedFromCardEffects.contains(it) }
            attackResolver.resolveAttack(this, affectedOpponents, info)
            if (isOpponentHasAction) {
                waitForOtherPlayersToResolveActions()
            }
        }
    }

    fun playAllTreasureCards() {

        val numTreasures = hand.count { it.isTreasure }

        val treasureCardsToPlay = if (numTreasures == 1) hand.filter { it.isTreasure } else hand.filter { it.isTreasure && !it.isTreasureExcludedFromAutoPlay }

        if (treasureCardsToPlay.isEmpty()) {
            return
        }

        treasureCardsToPlay.sortedBy { it.cost }.forEach { card ->
            playCard(card, refresh = false, showLog = false)
        }

        addEventLogWithUsername("played ${treasureCardsToPlay.groupedString}")

        refreshPlayerHandArea()
        game.refreshCardsPlayed()
        game.refreshCardsBought()
        game.refreshSupply()
    }

    fun discardHand() {
        addEventLogWithUsername("discarded their hand: ${hand.groupedString}")
        hand.toMutableList().forEach { discardCardFromHand(it, false, false) }
        refreshPlayerHandArea()
    }

    abstract fun chooseCardAction(text: String,
                                  chooseCardActionCard: ChooseCardActionCard,
                                  cardsToSelectFrom: List<Card>,
                                  optional: Boolean,
                                  info: Any? = null)

    abstract fun chooseCardsAction(numCardsToChoose: Int,
                                   text: String,
                                   chooseCardsActionCard: ChooseCardsActionCard,
                                   cardsToSelectFrom: List<Card>,
                                   optional: Boolean,
                                   info: Any? = null)

    fun showInfoMessage(message: String) {
        game.showInfoMessage(this, message)
    }

    fun revealFromDeckUntilCardFoundAndDiscardOthers(cardExpression: ((Card) -> Boolean)): Card? {
        return revealFromDeckUntilCardsFoundAndDiscardOthers(cardExpression, 1).firstOrNull()
    }

    fun revealFromDeckUntilCardsFoundAndDiscardOthers(cardExpression: ((Card) -> Boolean), numToFind: Int): List<Card> {
        val revealedCards = mutableListOf<Card>()
        val cardsToDiscard = mutableListOf<Card>()

        val cardsFound = mutableListOf<Card>()

        while (true) {
            val card = removeTopCardOfDeck()
            if (card != null) {
                revealedCards.add(card)

                if (card is AfterCardRevealedListenerForSelf) {
                    card.afterCardRevealed(this)
                }

                if (cardExpression.invoke(card)) {
                    cardsFound.add(card)
                    if (cardsFound.size == numToFind) {
                        break
                    }
                } else {
                    cardsToDiscard.add(card)
                }
            } else {
                break
            }
        }

        if (revealedCards.isNotEmpty()) {
            showInfoMessage("Revealed ${revealedCards.groupedString}")
            addEventLogWithUsername("revealed ${revealedCards.groupedString} from their deck")
        }

        if (cardsToDiscard.isNotEmpty()) {
            addCardsToDiscard(cardsToDiscard)
            addEventLogWithUsername("discarded ${cardsToDiscard.groupedString}")
        }

        return cardsFound
    }

    fun putDeckIntoDiscard() {
        discard.addAll(deck)
        deck.clear()
        refreshPlayerHandArea()
        addEventLogWithUsername("added deck into discard pile")
    }

    fun useCoffers(numCoffersToUse: Int) {
        if (numCoffersToUse == 0) {
            return
        }
        addCoffers(numCoffersToUse * -1)
        addCoins(numCoffersToUse)
        addEventLogWithUsername("used $numCoffersToUse Coffers")
    }

    fun addCoffers(coffers: Int, refresh: Boolean = true) {
        if (coffers == 0) {
            return
        }
        this.coffers += coffers
        if (refresh) {
            refreshPlayerHandArea()
        }
    }

    fun useVillagers(numVillagersToUse: Int) {
        if (numVillagersToUse == 0) {
            return
        }
        addVillagers(numVillagersToUse * -1)
        addActions(numVillagersToUse)
        addEventLogWithUsername("used $numVillagersToUse Villagers")
    }

    fun addVillagers(villagers: Int, refresh: Boolean = true) {
        if (villagers == 0) {
            return
        }
        this.villagers += villagers
        if (refresh) {
            refreshPlayerHandArea()
        }
    }

    fun payOffDebt(refresh: Boolean = true) {
        val debtToPayOff = minOf(debt, availableCoins)

        if (debtToPayOff == 0) {
            return
        }

        isPaidOffDebtThisTurn = true

        isTreasuresPlayable = false

        addInfoLogWithUsername("paid off $debtToPayOff debt")

        this.debt -= debtToPayOff

        addCoins(debtToPayOff * -1, refresh)
    }

    fun refreshPlayerHandArea() {
        game.refreshPlayerHandArea(this)
    }

    fun refreshCardsBought() {
        game.refreshPlayerCardsBought(this)
    }

    fun refreshSupply() {
        game.refreshPlayerSupply(this)
    }

    fun refreshCardsPlayed() {
        game.refreshPlayerCardsPlayed(this)
    }

    fun moveCardInPlayToTavern(card: Card) {
        removeCardInPlay(card, CardLocation.Tavern)
        tavernCards.add(card)
        refreshPlayerHandArea()
        addEventLogWithUsername("moved ${card.cardNameWithBackgroundColor} to their Tavern mat")
    }

    fun moveCardInTavernToInPlay(card: Card) {
        tavernCards.remove(card)
        inPlay.add(card)
        game.refreshCardsPlayed()
        refreshPlayerHandArea()
        addEventLogWithUsername("called ${card.cardNameWithBackgroundColor} from their Tavern mat")
    }

    fun callTavernCard(card: Card) {
        moveCardInTavernToInPlay(card)
        (card as TavernCard).onTavernCardCalled(this)
    }

    fun replaceAllEstatesWithInheritanceEstates() {
        replaceEstatesWithInheritanceEstates(hand)
        replaceEstatesWithInheritanceEstates(deck)
        replaceEstatesWithInheritanceEstates(discard)
        replaceEstatesWithInheritanceEstates(inPlay)
        replaceEstatesWithInheritanceEstates(nativeVillageCards)
        replaceEstatesWithInheritanceEstates(islandCards)
        replaceEstatesWithInheritanceEstates(durationCards)
        replaceEstatesWithInheritanceEstates(tavernCards)

        refreshPlayerHandArea()
        game.refreshCardsBought()
    }

    private fun replaceEstatesWithInheritanceEstates(cards: MutableList<Card>) {
        cards.replaceAll { card ->
            if (card is Estate) {
                createInheritanceEstate()
            } else {
                card
            }
        }
    }

    private fun createInheritanceEstate(): Card {
        return InheritanceEstate(inheritanceActionCard!!, InheritanceEstate.calculateInheritanceEstateCardType(inheritanceActionCard!!))
    }

    fun doubleMoney() {
        addCoins(coins)
        isMoneyDoubledThisTurn = true
        addEventLogWithUsername("doubled their money")
    }

    fun takeAllVictoryPointsFromSupplyPile(card: Card) {
        takeVictoryPointsFromSupplyPile(card, game.getVictoryPointsOnSupplyPile(card.pileName))
    }

    fun takeVictoryPointsFromSupplyPile(card: Card, points: Int) {
        val victoryPointsOnSupplyPile = game.getVictoryPointsOnSupplyPile(card.pileName)
        if (victoryPointsOnSupplyPile == 0) {
            return
        }
        val pointsToTake = minOf(points, victoryPointsOnSupplyPile)
        addVictoryCoins(pointsToTake)
        addEventLogWithUsername("gained $pointsToTake VP from ${card.cardNameWithBackgroundColor} Supply pile")
        game.removeVictoryPointsFromSupplyPile(card.pileName, pointsToTake)
    }

    fun moveVictoryPointsOnSupplyPile(fromCard: Card, toCard: Card, victoryPoints: Int) {
        game.removeVictoryPointsFromSupplyPile(fromCard.pileName, victoryPoints)
        game.addVictoryPointsToSupplyPile(toCard.pileName, victoryPoints)
        addEventLogWithUsername("moved 1 VP from ${fromCard.cardNameWithBackgroundColor} to ${toCard.cardNameWithBackgroundColor}")
    }

    fun clearDiscard() {
        discard.clear()
    }

    fun removeCard(card: Card) {
        when {
            discard.contains(card) -> removeCardFromDiscard(card)
            hand.contains(card) -> removeCardFromHand(card)
            deck.contains(card) -> removeCardFromDeck(card)
        }
    }

    fun takeFlag() {
        takeArtifact(game.artifacts.first { it is Flag })
    }

    fun takeLantern() {
        takeArtifact(game.artifacts.first { it is Lantern })
    }

    fun takeHorn() {
        takeArtifact(game.artifacts.first { it is Horn })
    }

    fun takeKey() {
        takeArtifact(game.artifacts.first { it is Key })
    }

    fun takeTreasureChest() {
        takeArtifact(game.artifacts.first { it is TreasureChest })
    }

    private fun takeArtifact(artifact: Artifact) {
        if (!hasArtifact(artifact.name)) {
            artifact.owner = username
            addEventLogWithUsername("took the ${artifact.cardNameWithBackgroundColor}")
        }
    }

    fun hasArtifact(artifactName: String): Boolean {
        return game.artifacts.firstOrNull { it.name == artifactName }?.owner == username
    }

    fun actionTakenInBuyPhase() {
        handleBeforeBuyPhase()
        isActionTakenInBuyPhase = true
    }

    fun getVictoryPointsForAllCardsWithName(cardName: String): Int {
        return allCards.filter { it.name == cardName }
                .filterIsInstance<VictoryPointsCalculator>()
                .sumBy { it.calculatePoints(this) }
    }
}
