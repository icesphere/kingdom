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
import com.kingdom.model.cards.listeners.*
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

    val handCopy: List<Card>
        get() = hand.map { game.getNewInstanceOfCard(it.name) }

    protected val discard: MutableList<Card> = ArrayList()

    val cardsInDiscard: List<Card>
        get() = discard

    val cardsInDiscardCopy: List<Card>
        get() = cardsInDiscard.map { game.getNewInstanceOfCard(it.name) }

    private val cardsBought: MutableList<Card> = ArrayList()
    val played: MutableList<Card> = ArrayList()
    val inPlay: MutableList<Card> = ArrayList()

    val inPlayCopy: List<Card>
        get() = inPlay.map { game.getNewInstanceOfCard(it.name) }

    val eventsBought: MutableList<Event> = ArrayList()

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
        get() = isYourTurn && game.cardsPlayed.isEmpty() && !isCardsBought

    private var coins: Int = 0

    private var coinsInHand: Int = 0
        get() = hand.filter { it.isTreasure }.sumBy { it.addCoins }

    var availableCoins: Int = 0
        get() = if (game.isPlayTreasureCards) coins else coins + coinsInHand

    var coinsSpent: Int = 0

    var buys: Int = 0
        private set

    var actions: Int = 0
        private set

    var coffers: Int = 0

    val opponents: List<Player> by lazy {
        game.players.filterNot { it.userId == userId }
    }

    val opponentsInOrder: List<Player> by lazy {

        val list = mutableListOf<Player>()

        var nextPlayer = game.getPlayerToLeft(this)

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

    private var numCardsTrashedThisTurn: Int = 0

    private var coinsGainedThisTurn: Int = 0

    var lastTurnSummary: TurnSummary? = null

    private var currentTurnSummary = TurnSummary(username)

    var isWaitingForComputer: Boolean = false

    lateinit var chatColor: String

    var isQuit: Boolean = false

    var victoryCoins: Int = 0
        private set

    var victoryPoints: Int = 0

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

    val isPlayTreasureCards: Boolean = game.isPlayTreasureCards

    val isTreasureCardsPlayedInBuyPhase: Boolean
        get() = (game.cardsPlayed - game.treasureCardsPlayedInActionPhase).any { it.isTreasure }

    val isCardsBought: Boolean
        get() = cardsBought.isNotEmpty() || eventsBought.isNotEmpty()

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
    }

    fun cardRemovedFromPlay(card: Card) {
        card.removedFromPlay(this)

        if (card.isCardActuallyBandOfMisfits) {
            card.isCardActuallyBandOfMisfits = false

            val bandOfMisfits = BandOfMisfits()

            when {
                cardsInDiscard.contains(card) -> {
                    removeCardFromDiscard(card)
                    addCardToDiscard(bandOfMisfits)
                }
                hand.contains(card) -> {
                    removeCardFromHand(card)
                    addCardToHand(card)
                }
                deck.contains(card) -> {
                    deck.replaceAll { deckCard ->
                        if (deckCard.id == card.id) {
                            bandOfMisfits
                        } else {
                            deckCard
                        }
                    }
                }
                islandCards.contains(card) -> {
                    islandCards.remove(card)
                    islandCards.add(bandOfMisfits)
                }
                nativeVillageCards.contains(card) -> {
                    nativeVillageCards.remove(card)
                    nativeVillageCards.add(bandOfMisfits)
                }
                game.trashedCards.contains(card) -> {
                    game.trashedCards.remove(card)
                    game.trashedCards.add(bandOfMisfits)
                }
            }
        }
    }

    fun addCoins(coins: Int) {
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
        refreshSupply()
        refreshCardsBought()
    }

    fun addVictoryCoins(victoryCoins: Int) {
        if (victoryCoins == 0) {
            return
        }
        this.victoryCoins += victoryCoins
        refreshPlayerHandArea()
    }

    fun addActions(actions: Int, refresh: Boolean = true) {
        if (actions == 0) {
            return
        }
        this.actions += actions
        if (refresh) {
            game.refreshPlayerCardsPlayed(this)
        }
    }

    fun addBuys(buys: Int) {
        if (buys == 0) {
            return
        }
        this.buys += buys
        refreshCardsBought()
    }

    fun endTurn(isAutoEnd: Boolean = false) {

        resolveActions()

        if (currentAction != null) {
            return
        }

        addInfoLogWithUsername("ending turn")

        currentTurnSummary.cardsPlayed.addAll(played)

        lastTurnSummary = currentTurnSummary

        currentTurnSummary = TurnSummary(username)

        game.refreshHistory()

        turns++

        coins = 0
        coinsSpent = 0
        actions = 0
        buys = 0

        numActionsPlayed = 0

        playedCrossroadsThisTurn = false

        isNextCardToTopOfDeck = false
        isNextCardToHand = false

        numCardsTrashedThisTurn = 0
        coinsGainedThisTurn = 0

        cardsUnavailableToBuyThisTurn.clear()

        cardsBought.clear()
        played.clear()

        eventsBought.clear()

        val nonPermanentDurationCards = durationCards.filterNot { it is PermanentDuration || (it is CardRepeater && it.cardBeingRepeated is PermanentDuration) }

        nonPermanentDurationCards.forEach { addCardToDiscard(it, false, false) }

        durationCards.removeAll(nonPermanentDurationCards)

        for (card in inPlay) {
            if (card.isDuration || (card is CardRepeater && card.cardBeingRepeated?.isDuration == true)) {
                durationCards.add(card)

                card.isSelected = false
                card.isHighlighted = false
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

        resolveActions()

        if (currentAction != null) {
            finishEndTurnAfterResolvingActions = true
            return
        }

        finishEndTurn(isAutoEnd)
    }

    private fun finishEndTurn(isAutoEnd: Boolean = false) {
        finishEndTurnAfterResolvingActions = false

        drawCards(5 + numExtraCardsToDrawAtEndOfTurn)

        numExtraCardsToDrawAtEndOfTurn = 0

        if (cardToPutIntoHandAfterDrawingCardsAtEndOfTurn != null) {
            addCardToHand(cardToPutIntoHandAfterDrawingCardsAtEndOfTurn!!)
            cardToPutIntoHandAfterDrawingCardsAtEndOfTurn = null
        }

        isYourTurn = false

        game.turnEnded(isAutoEnd)
    }

    abstract fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, info: Any? = null)
    abstract fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)
    abstract fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String = "", cardActionableExpression: ((card: Card) -> Boolean)? = null)
    abstract fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)

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

        if (!game.isPlayTreasureCards) {
            refreshCardsBought()
            refreshSupply()
        }
    }

    fun trashHand() {
        if (hand.isEmpty()) {
            addEventLogWithUsername("trashed an empty hand")
            return
        }

        hand.forEach { cardTrashed(it) }

        addEventLogWithUsername("trashed their hand: ${hand.groupedString}")

        hand.clear()

        refreshPlayerHandArea()

        if (!game.isPlayTreasureCards) {
            refreshCardsBought()
            refreshSupply()
        }
    }

    fun cardTrashed(card: Card, showLog: Boolean = false) {
        val cardToTrash = if (card is InheritanceEstate) Estate() else card

        if (showLog) {
            addEventLogWithUsername("trashed ${cardToTrash.cardNameWithBackgroundColor}")
        }

        game.trashedCards.add(cardToTrash)

        cardRemovedFromPlay(cardToTrash)

        numCardsTrashedThisTurn++

        if (isYourTurn) {
            currentTurnSummary.trashedCards.add(cardToTrash)
        }

        if (cardToTrash is AfterCardTrashedListenerForSelf) {
            cardToTrash.afterCardTrashed(this)
        }

        if (cardToTrash.addedAbilityCard is AfterCardTrashedListenerForSelf) {
            (cardToTrash.addedAbilityCard as AfterCardTrashedListenerForSelf).afterCardTrashed(this)
        }

        val cardTrashedListenersForCardsInHand = hand.filter { it is AfterCardTrashedListenerForCardsInHand }.toMutableList()
        cardTrashedListenersForCardsInHand.addAll(hand.filter { it.addedAbilityCard is AfterCardTrashedListenerForCardsInHand }.map { it.addedAbilityCard!! })

        for (listener in cardTrashedListenersForCardsInHand) {
            (listener as AfterCardTrashedListenerForCardsInHand).afterCardTrashed(cardToTrash, this)
        }
    }

    fun removeCardInPlay(card: Card) {
        inPlay.remove(card)
        cardRemovedFromPlay(card)
        game.refreshCardsPlayed()
    }

    fun trashCardInPlay(card: Card, showLog: Boolean = true) {
        if (showLog) {
            addEventLog("Trashed " + card.cardNameWithBackgroundColor + " from in play")
        }

        inPlay.remove(card)
        cardRemovedFromPlay(card)
        game.cardsPlayed.remove(card)
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

        game.availableCards.filter { it is CardGainedListenerForCardsInSupply }
                .forEach {
                    (it as CardGainedListenerForCardsInSupply).onCardGained(cardToGain, this)
                }

        val cardGainedListenersForCardsInHand = hand.filter { it is CardGainedListenerForCardsInHand }.toMutableList()
        cardGainedListenersForCardsInHand.addAll(hand.filter { it.addedAbilityCard is CardGainedListenerForCardsInHand }.map { it.addedAbilityCard!! })

        for (listener in cardGainedListenersForCardsInHand) {
            val handled = (listener as CardGainedListenerForCardsInHand).onCardGained(cardToGain, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        val cardGainedListenersForCardsInPlay = inPlayWithDuration.filter { it is CardGainedListenerForCardsInPlay }.toMutableList()
        cardGainedListenersForCardsInPlay.addAll(inPlayWithDuration.filter { it.addedAbilityCard is CardGainedListenerForCardsInPlay }.map { it.addedAbilityCard!! })

        for (listener in cardGainedListenersForCardsInPlay) {
            val handled = (listener as CardGainedListenerForCardsInPlay).onCardGained(cardToGain, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        val cardGainedListenersForEventsBought = eventsBought.filter { it is CardGainedListenerForEventsBought }
        for (listener in cardGainedListenersForEventsBought) {
            val handled = (listener as CardGainedListenerForEventsBought).onCardGained(cardToGain, this)
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

        if (cardToGain is AfterCardGainedListenerForSelf) {
            cardToGain.afterCardGained(this)
        }

        if (cardToGain.addedAbilityCard is AfterCardGainedListenerForSelf) {
            (cardToGain.addedAbilityCard as AfterCardGainedListenerForSelf).afterCardGained(this)
        }

        val afterCardGainedListenersForCardsInTavern = tavernCards.filter { it is AfterCardGainedListenerForCardsInTavern }.toMutableList()
        afterCardGainedListenersForCardsInTavern.addAll(tavernCards.filter { it.addedAbilityCard is AfterCardGainedListenerForCardsInTavern }.map { it.addedAbilityCard!! })

        for (listener in afterCardGainedListenersForCardsInTavern) {
            (listener as AfterCardGainedListenerForCardsInTavern).afterCardGained(cardToGain, this)
        }

        if (isYourTurn) {
            currentTurnSummary.cardsGained.add(cardToGain)
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

    fun buyEvent(event: Event) {
        if (event.isEventActionable(this)) {
            addEventLogWithUsername("bought event: " + event.name)
            coins -= event.cost
            buys -= 1
            eventsBought.add(event)
            currentTurnSummary.eventsBought.add(event)
            refreshCardsBought()
            refreshSupply()
            event.cardPlayed(this)
        }
    }

    fun buyCard(card: Card) {
        if (availableCoins >= this.getCardCostWithModifiers(card)) {
            addEventLogWithUsername("bought card: " + card.cardNameWithBackgroundColor)
            coins -= this.getCardCostWithModifiers(card)
            buys -= 1
            cardsBought.add(card)
            game.cardsBought.add(card)
            currentTurnSummary.cardsBought.add(card)

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

            val cardBoughtListenersForCardsInPlay = inPlayWithDuration.filter { it is AfterCardBoughtListenerForCardsInPlay || it.addedAbilityCard is AfterCardBoughtListenerForCardsInPlay }.toMutableList()
            cardBoughtListenersForCardsInPlay.addAll(inPlayWithDuration.filter { it.addedAbilityCard is AfterCardBoughtListenerForCardsInPlay}.map { it.addedAbilityCard!! })

            for (listener in cardBoughtListenersForCardsInPlay) {
                (listener as AfterCardBoughtListenerForCardsInPlay).afterCardBought(card, this)
            }

            val cardBoughtListenersForCardsInHand = hand.filter { it is AfterCardBoughtListenerForCardsInHand || it.addedAbilityCard is AfterCardBoughtListenerForCardsInHand }.toMutableList()
            cardBoughtListenersForCardsInHand.addAll(hand.filter { it.addedAbilityCard is AfterCardBoughtListenerForCardsInHand }.map { it.addedAbilityCard!! })

            for (listener in cardBoughtListenersForCardsInHand) {
                (listener as AfterCardBoughtListenerForCardsInHand).afterCardBought(card, this)
            }
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
            inheritanceActionCard?.let{ cards.add(it) }

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
            game.addEventLog("Played card: ${card.cardNameWithBackgroundColor}")
        }

        if (!repeatedAction) {

            played.add(card)
            game.cardsPlayed.add(card)

            val listeners = inPlayWithDuration.filter { it is CardPlayedListener }.toMutableList()
            listeners.addAll(inPlayWithDuration.filter { it.addedAbilityCard is CardPlayedListener }.map { it.addedAbilityCard!! })

            listeners.forEach { (it as CardPlayedListener).onCardPlayed(card, this) }

            inPlay.add(card)
            hand.remove(card)

            if (refresh) {
                refreshPlayerHandArea()
                game.refreshCardsPlayed()
            }
        }

        when (card.name) {
            plusCardTokenSupplyPile -> drawCard()
            plusActionTokenSupplyPile -> addActions(1)
            plusBuyTokenSupplyPile -> addBuys(1)
            plusCoinTokenSupplyPile -> addCoins(1)
        }

        if (card.isAction) {
            numActionsPlayed++
        }

        game.availableCards.filter { it is CardPlayedListenerForCardsInSupply }
                .forEach {
                    (it as CardPlayedListenerForCardsInSupply).onCardPlayed(card, this)
                }

        val cardPlayedListenersForCardsInPlay = inPlayWithDuration.filter { it is CardPlayedListenerForCardsInPlay }.toMutableList()
        cardPlayedListenersForCardsInPlay.addAll(inPlayWithDuration.filter { it.addedAbilityCard is CardPlayedListenerForCardsInPlay }.map { it.addedAbilityCard!! })

        cardPlayedListenersForCardsInPlay.forEach {
            (it as CardPlayedListenerForCardsInPlay).onCardPlayed(card, this)
        }

        card.cardPlayed(this)
    }

    private fun countCardsByType(cards: List<Card>, typeMatcher: Function<Card, Boolean>): Int {
        return cards.filter({ typeMatcher.apply(it) }).count()
    }

    private val currentDeckNumber: Int
        get() = shuffles + 1

    abstract fun chooseSupplyCardToGain(maxCost: Int?, cardActionableExpression: ((card: Card) -> Boolean)? = null, text: String? = null)

    abstract fun chooseSupplyCardToGainWithExactCost(cost: Int)

    abstract fun chooseSupplyCardToGainForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseSupplyCardToGainToTopOfDeck(maxCost: Int?)

    abstract fun chooseSupplyCardToGainToTopOfDeckWithMaxCostAndType(maxCost: Int?, cardType: CardType)

    abstract fun chooseSupplyCardToGainToTopOfDeckWithExactCost(cost: Int)

    abstract fun chooseSupplyCardToGainToHandWithMaxCost(maxCost: Int?)

    abstract fun chooseSupplyCardToGainToHandWithMaxCostAndType(maxCost: Int?, cardType: CardType)

    abstract fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int)

    abstract fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String, info: Any? = null)

    fun gainCardNotInSupply(card: Card) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)

            cardGained(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor}")
        }
    }

    fun gainRuins() {
        if (game.ruinsPile.isNotEmpty()) {
            val card = game.ruinsPile.removeAt(0)
            cardGained(card)
            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the Ruins pile")
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
        return countCardsByType(played, typeMatcher) > 0
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

        cardRemovedFromPlay(card)

        if (refresh) {
            refreshPlayerHandArea()
        }
    }

    fun discardCardFromHand() {
        discardCardsFromHand(1, false)
    }

    fun discardCardFromHand(card: Card, showLog: Boolean = true) {
        hand.remove(card)

        addCardToDiscard(card)

        if (showLog) {
            addEventLogWithUsername(" discarded ${card.cardNameWithBackgroundColor} from hand")
        }

        if (!game.isPlayTreasureCards) {
            refreshCardsBought()
            refreshSupply()
        }
    }

    abstract fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean)

    abstract fun waitForOtherPlayersToResolveActions()

    abstract fun waitForOtherPlayersForResolveAttack(attackCard: Card)

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

        if (!game.isPlayTreasureCards && card.addCoins > 0) {
            refreshCardsBought()
            refreshSupply()
        }
    }

    fun addCardsToHand(cards: List<Card>, showLog: Boolean = false) {
        hand.addAll(cards)

        if (showLog) {
            addEventLogWithUsername("added ${cards.groupedString} to hand")
        }

        refreshPlayerHandArea()

        if (!game.isPlayTreasureCards && cards.any { it.addCoins > 0 }) {
            refreshCardsBought()
            refreshSupply()
        }
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

                if (currentAction == null && finishEndTurnAfterResolvingActions) {
                    finishEndTurn()
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
            if (card is StartOfTurnDurationAction) {
                card.durationStartOfTurnAction(this)
            } else if (card is CardRepeater && card.cardBeingRepeated is StartOfTurnDurationAction) {
                val durationCard = card.cardBeingRepeated as StartOfTurnDurationAction

                repeat(card.timesRepeated) {
                    durationCard.durationStartOfTurnAction(this)
                }
            }
        }

        resolveActions()

        takeTurn()
    }

    abstract fun takeTurn()

    fun addRepeatCardAction(card: Card) {
        actionsQueue.add(RepeatCardAction(card))
    }

    abstract fun addCardFromDiscardToTopOfDeck(maxCost: Int? = null)

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
    }

    fun revealCardFromHand(card: Card) {
        addEventLogWithUsername("revealed ${card.cardNameWithBackgroundColor} from their hand")
    }

    fun revealTopCardOfDeck(): Card? {
        val cards = revealTopCardsOfDeck(1)
        return cards.firstOrNull()
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
            addEventLogWithUsername("revealed ${cards.groupedString} from top of deck")
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

    fun removeCardFromHand(card: Card) {
        hand.remove(card)

        refreshPlayerHandArea()

        if (!game.isPlayTreasureCards && card.addCoins > 0) {
            refreshCardsBought()
            refreshSupply()
        }
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
        if (cardsUnavailableToBuyThisTurn.any { it.name == card.name }) {
            return false
        }

        val cost = getCardCostWithModifiers(card)
        return game.isCardAvailableInSupply(card) && availableCoins >= cost
    }

    abstract fun chooseCardForOpponentToGain(cost: Int, text: String, destination: CardLocation, opponent: Player)

    abstract fun chooseCardFromHand(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseCardFromHandOptional(text: String, chooseCardActionCard: ChooseCardActionCardOptional, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseCardsFromHand(text: String, numToChoose: Int, optional: Boolean, chooseCardsActionCard: ChooseCardsActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)? = null, info: Any? = null, choosingEmptyPilesAllowed: Boolean = true)

    fun triggerAttack(attackCard: Card) {

        opponentsInOrder.forEach { opponent ->
            val handBeforeAttackListeners = opponent.hand.filter { it is HandBeforeAttackListener }.toMutableList()
            handBeforeAttackListeners.addAll(opponent.hand.filter { it.addedAbilityCard is HandBeforeAttackListener }.map { it.addedAbilityCard!! })

            handBeforeAttackListeners.forEach { (it as HandBeforeAttackListener).onBeforeAttack(attackCard, opponent, this) }

            val durationBeforeAttackListeners = opponent.durationCards.filter { it is DurationBeforeAttackListener }.toMutableList()
            durationBeforeAttackListeners.addAll(opponent.durationCards.filter { it.addedAbilityCard is DurationBeforeAttackListener }.map { it.addedAbilityCard!! })

            durationBeforeAttackListeners.forEach { (it as DurationBeforeAttackListener).onBeforeAttack(attackCard, opponent, this) }
        }

        if (isOpponentHasAction) {
            waitForOtherPlayersForResolveAttack(attackCard)
        } else {
            val attackResolver = attackCard as AttackCard
            val affectedOpponents = opponentsInOrder.filterNot { attackCard.playersExcludedFromCardEffects.contains(it) }
            attackResolver.resolveAttack(this, affectedOpponents)
            if (isOpponentHasAction) {
                waitForOtherPlayersToResolveActions()
            }
        }
    }

    fun playAllTreasureCards() {
        val treasureCardsToPlay = hand.filter { it.isTreasure && !it.isTreasureExcludedFromAutoPlay }

        if (treasureCardsToPlay.isEmpty()) {
            return
        }

        treasureCardsToPlay.sortedBy { it.cost }.forEach { card ->
            playCard(card, refresh = false, showLog = false)
        }

        addEventLogWithUsername("played ${treasureCardsToPlay.groupedString}")

        refreshPlayerHandArea()
        game.refreshCardsPlayed()
    }

    fun discardHand() {
        addEventLogWithUsername("discarded their hand: ${hand.groupedString}")
        hand.toMutableList().forEach { discardCardFromHand(it, false) }
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

    fun addCoffers(coffers: Int) {
        if (coffers == 0) {
            return
        }
        this.coffers += coffers
        refreshPlayerHandArea()
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
        removeCardInPlay(card)
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
        refreshCardsBought()
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
}
