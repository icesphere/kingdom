package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.TurnSummary
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.*
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

abstract class Player protected constructor(val user: User, val game: Game) {
    var deck: MutableList<Card> = ArrayList()
    val hand: MutableList<Card> = ArrayList()

    protected val discard: MutableList<Card> = ArrayList()

    val cardsInDiscard: List<Card>
        get() = discard

    val cardsInDiscardCopy: List<Card>
        get() = cardsInDiscard.map { game.getNewInstanceOfCard(it.name) }

    val bought: MutableList<Card> = ArrayList()
    val played: MutableList<Card> = ArrayList()
    val inPlay: MutableList<Card> = ArrayList()

    val numCards: Int
        get() = allCards.size

    val userId = user.userId
    val username = user.username
    val isMobile = user.isMobile

    protected var actionsQueue: MutableList<Action> = ArrayList()

    var currentAction: Action? = null

    var isYourTurn: Boolean = false
        protected set

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

    var coinTokens: Int = 0

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

    val isTreasureCardsPlayed: Boolean
        get() = game.cardsPlayed.any { it.isTreasure }

    val isCardsBought: Boolean
        get() = game.cardsBought.isNotEmpty()

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
            game.refreshPlayerHandArea(this)
        }

    var playedCrossroadsThisTurn: Boolean = false

    var finishEndTurnAfterResolvingActions: Boolean = false

    var cardsUnavailableToBuyThisTurn = mutableListOf<Card>()

    val cardsSetAsideUntilStartOfTurn = mutableSetOf<SetAsideUntilStartOfTurnCard>()

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
        this.coins += coins
        coinsGainedThisTurn += coins
        game.refreshPlayerSupply(this)
        game.refreshPlayerCardsBought(this)
    }

    fun addVictoryCoins(victoryCoins: Int) {
        this.victoryCoins += victoryCoins
        game.refreshPlayerHandArea(this)
    }

    fun addActions(actions: Int) {
        this.actions += actions
        game.refreshPlayerCardsPlayed(this)
    }

    fun addBuys(buys: Int) {
        this.buys += buys
        game.refreshPlayerCardsBought(this)
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

        bought.clear()
        played.clear()

        durationCards.forEach { addCardToDiscard(it, false, false) }

        durationCards.clear()

        for (card in inPlay) {
            if (card.isDuration) {
                durationCards.add(card)
                cardRemovedFromPlay(card)
            } else {
                addCardToDiscard(card, false, false)

                if (card is CardDiscardedFromPlayListener) {
                    card.onCardDiscarded(this)
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
        drawCards(5)

        isYourTurn = false

        game.turnEnded(isAutoEnd)
    }

    abstract fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, info: Any? = null)
    abstract fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)
    abstract fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String = "", cardActionableExpression: ((card: Card) -> Boolean)? = null)
    abstract fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    abstract fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)? = null)

    fun trashCardFromDiscard(card: Card) {
        addEventLogWithUsername("trashed " + card.cardNameWithBackgroundColor + " from discard")

        discard.remove(card)

        cardTrashed(card)

        game.refreshPlayerHandArea(this)
    }

    fun trashCardFromHand(card: Card) {
        addEventLogWithUsername("trashed " + card.cardNameWithBackgroundColor + " from hand")

        hand.remove(card)

        cardTrashed(card)

        game.refreshPlayerHandArea(this)

        if (!game.isPlayTreasureCards) {
            game.refreshPlayerCardsBought(this)
            game.refreshPlayerSupply(this)
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

        game.refreshPlayerHandArea(this)

        if (!game.isPlayTreasureCards) {
            game.refreshPlayerCardsBought(this)
            game.refreshPlayerSupply(this)
        }
    }

    fun cardTrashed(card: Card, showLog: Boolean = false) {
        if (showLog) {
            addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor}")
        }

        game.trashedCards.add(card)

        cardRemovedFromPlay(card)

        numCardsTrashedThisTurn++

        if (isYourTurn) {
            currentTurnSummary.trashedCards.add(card)
        }

        if (card is AfterCardTrashedListenerForSelf) {
            card.afterCardTrashed(this)
        }

        val cardTrashedListenersForCardsInHand = hand.filter { it is AfterCardTrashedListenerForCardsInHand }
        for (listener in cardTrashedListenersForCardsInHand) {
            (listener as AfterCardTrashedListenerForCardsInHand).afterCardTrashed(card, this)
        }
    }

    fun removeCardInPlay(card: Card) {
        inPlay.remove(card)
        cardRemovedFromPlay(card)
        game.cardsPlayed.remove(card)
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
        game.refreshPlayerHandArea(this)
    }

    fun gainCardToHand(card: Card) {
        isNextCardToHand = true
        cardGained(card)
        game.refreshPlayerHandArea(this)
    }

    fun addCardToTopOfDeck(card: Card, addGameLog: Boolean = true) {
        deck.add(0, card)
        if (addGameLog) {
            addEventLogWithUsername("added " + card.cardNameWithBackgroundColor + " to top of deck")
        }
        game.refreshPlayerHandArea(this)
    }

    fun cardGained(card: Card) {

        var gainCardHandled = false

        game.availableCards.filter { it is CardGainedListenerForCardsInSupply }
                .forEach {
                    (it as CardGainedListenerForCardsInSupply).onCardGained(card, this)
                }

        val cardGainedListenersForCardsInHand = hand.filter { it is CardGainedListenerForCardsInHand }
        for (listener in cardGainedListenersForCardsInHand) {
            val handled = (listener as CardGainedListenerForCardsInHand).onCardGained(card, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        val cardGainedListenersForCardsInPlay = inPlay.filter { it is CardGainedListenerForCardsInPlay }
        for (listener in cardGainedListenersForCardsInPlay) {
            val handled = (listener as CardGainedListenerForCardsInPlay).onCardGained(card, this)
            if (handled) {
                gainCardHandled = true
                break
            }
        }

        if (gainCardHandled) {
            return
        }

        if (card is BeforeCardGainedListenerForSelf) {
            card.beforeCardGained(this)
        }

        when {
            isNextCardToHand -> {
                isNextCardToHand = false
                addCardToHand(card)
            }
            isNextCardToTopOfDeck -> {
                isNextCardToTopOfDeck = false
                addCardToTopOfDeck(card)
            }
            else -> {
                addCardToDiscard(card)
            }
        }

        if (card is AfterCardGainedListenerForSelf) {
            card.afterCardGained(this)
        }

        if (isYourTurn) {
            currentTurnSummary.cardsGained.add(card)
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

    fun buyCard(card: Card) {
        if (availableCoins >= this.getCardCostWithModifiers(card)) {
            addEventLogWithUsername("bought card: " + card.cardNameWithBackgroundColor)
            coins -= this.getCardCostWithModifiers(card)
            buys -= 1
            bought.add(card)
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

            if (card is AfterCardBoughtListenerForSelf) {
                card.afterCardBought(this)
            }

            val cardBoughtListenersForCardsInPlay = inPlay.filter { it is AfterCardBoughtListenerForCardsInPlay }

            for (listener in cardBoughtListenersForCardsInPlay) {
                (listener as AfterCardBoughtListenerForCardsInPlay).afterCardBought(card, this)
            }

            val cardBoughtListenersForCardsInHand = hand.filter { it is AfterCardBoughtListenerForCardsInHand }

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

    fun playCard(card: Card, refresh: Boolean = true, repeatedAction: Boolean = false) {

        game.addEventLog("Played card: ${card.cardNameWithBackgroundColor}")

        if (!repeatedAction) {

            played.add(card)
            game.cardsPlayed.add(card)

            inPlay.filter { it is CardPlayedListener }
                    .forEach { (it as CardPlayedListener).onCardPlayed(card, this) }

            inPlay.add(card)
            hand.remove(card)

            if (refresh) {
                game.refreshPlayerHandArea(this)
                game.refreshCardsPlayed()
            }
        }

        if (card.isAction) {
            numActionsPlayed++
        }

        game.availableCards.filter { it is CardPlayedListenerForCardsInSupply }
                .forEach {
                    (it as CardPlayedListenerForCardsInSupply).onCardPlayed(card, this)
                }

        inPlay.filter { it is CardPlayedListenerForCardsInPlay }
                .forEach {
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

    abstract fun chooseSupplyCardToGainForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard)

    abstract fun chooseSupplyCardToGainToTopOfDeck(maxCost: Int?)

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

    fun addCardsToDiscard(cards: List<Card>) {
        cards.forEach { addCardToDiscard(it, refresh = false) }
        game.refreshPlayerHandArea(this)
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
            game.refreshPlayerHandArea(this)
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
            game.refreshPlayerCardsBought(this)
            game.refreshPlayerSupply(this)
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
            game.refreshPlayerHandArea(this)
            game.refreshPlayerSupply(this)
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
        game.refreshPlayerHandArea(this)
    }

    fun addCardToDeck(card: Card) {
        deck.add(card)
        game.refreshPlayerHandArea(this)
    }

    fun addCardToHand(card: Card, showLog: Boolean = false) {
        hand.add(card)

        if (showLog) {
            addEventLogWithUsername("added " + card.cardNameWithBackgroundColor + " to hand")
        }

        game.refreshPlayerHandArea(this)

        if (!game.isPlayTreasureCards && card.addCoins > 0) {
            game.refreshPlayerCardsBought(this)
        }
    }

    fun addCardsToHand(cards: List<Card>) {
        hand.addAll(cards)

        game.refreshPlayerHandArea(this)

        if (!game.isPlayTreasureCards && cards.any { it.addCoins > 0 }) {
            game.refreshPlayerCardsBought(this)
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

        durationCards.forEach {
            if (it is StartOfTurnDurationAction) {
                it.durationStartOfTurnAction(this)
            }
        }

        resolveActions()

        takeTurn()
    }

    abstract fun takeTurn()

    abstract fun addCardAction(card: CardActionCard, text: String)

    fun addRepeatCardAction(card: Card) {
        actionsQueue.add(RepeatCardAction(card))
    }

    abstract fun addCardFromDiscardToTopOfDeck(maxCost: Int?)

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

        game.refreshPlayerHandArea(this)

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
        game.refreshPlayerHandArea(this)
    }

    fun removeCardsFromDiscard(cards: List<Card>) {
        cards.forEach { discard.remove(it) }
        game.refreshPlayerHandArea(this)
    }

    fun removeCardFromDiscard(card: Card) {
        discard.remove(card)
        game.refreshPlayerHandArea(this)
    }

    fun removeCardFromHand(card: Card) {
        hand.remove(card)

        game.refreshPlayerHandArea(this)

        if (!game.isPlayTreasureCards && card.addCoins > 0) {
            game.refreshPlayerCardsBought(this)
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

    abstract fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard)

    fun triggerAttack(attackCard: Card) {

        opponentsInOrder.forEach { opponent ->
            opponent.hand.filter { it is HandBeforeAttackListener }
                    .forEach { (it as HandBeforeAttackListener).onBeforeAttack(attackCard, opponent, this) }

            opponent.durationCards.filter { it is DurationBeforeAttackListener }
                    .forEach { (it as DurationBeforeAttackListener).onBeforeAttack(attackCard, opponent, this) }
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
        hand.filter { it.isTreasure && !it.isTreasureExcludedFromAutoPlay }.sortedBy { it.cost }.forEach { card ->
            playCard(card, refresh = false)
        }

        game.refreshPlayerHandArea(this)
        game.refreshCardsPlayed()
    }

    fun discardHand() {
        addEventLogWithUsername("discarded their hand")
        hand.toMutableList().forEach { discardCardFromHand(it) }
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
        val revealedCards = mutableListOf<Card>()
        val cardsToDiscard = mutableListOf<Card>()

        var cardFound: Card? = null

        while(true) {
            val card = removeTopCardOfDeck()
            if (card != null) {
                revealedCards.add(card)
                if (cardExpression.invoke(card)) {
                    cardFound = card
                    break
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
            addEventLogWithUsername("discarded ${revealedCards.groupedString}")
        }

        return cardFound
    }

    fun putDeckIntoDiscard() {
        discard.addAll(deck)
        deck.clear()
        game.refreshPlayerHandArea(this)
        addEventLogWithUsername("added deck into discard pile")
    }
}
