package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.TurnSummary
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardCopier
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.modifiers.CardCostModifier
import java.util.*
import java.util.function.Function

abstract class Player protected constructor(val game: Game) {
    var deck: MutableList<Card> = ArrayList()
    val hand: MutableList<Card> = ArrayList()
    val discard: MutableList<Card> = ArrayList()
    val played: MutableList<Card> = ArrayList()
    val inPlay: MutableList<Card> = ArrayList()

    abstract val userId: Int

    abstract val username: String

    protected var actionsQueue: MutableList<Action> = ArrayList()

    var currentAction: Action? = null
        protected set

    var isYourTurn: Boolean = false
        protected set

    var trade: Int = 0

    var buys: Int = 0

    var actions: Int = 0

    var coinTokens: Int = 0

    lateinit var opponents: List<Player>

    var isNextCardToTopOfDeck: Boolean = false

    var isNextCardToHand: Boolean = false

    var shuffles: Int = 0
        protected set

    var isFirstPlayer: Boolean = false

    lateinit var playerName: String

    var turns: Int = 0

    var turn: Int = 0

    var numCardsTrashpedThisTurn: Int = 0
        private set
    var tradeGainedThisTurn: Int = 0
        private set
    var combatGainedThisTurn: Int = 0
        private set
    var authorityGainedThisTurn: Int = 0
        private set
    val shipsPlayedThisTurn: MutableList<Card> = ArrayList()

    var lastTurnSummary: TurnSummary? = null
        private set

    var currentTurnSummary = TurnSummary()
        private set

    var isWaitingForComputer: Boolean = false

    private var acquireCardToTopOfDeck: Boolean = false

    private var acquireCardToHand: Boolean = false

    var cardCostModifier: CardCostModifier? = null

    fun drawCard() {
        drawCards(1)
    }

    fun drawCards(numCards: Int): List<Card> {
        val cards = getCardsFromDeck(numCards)
        cards.forEach { addCardToHand(it, false) }
        return cards
    }

    fun getCardsFromDeck(numCards: Int): List<Card> {
        if (numCards == 0) {
            return ArrayList()
        }

        val cardsDrawn = ArrayList<Card>()
        var log = playerName + " drawing " + numCards
        if (numCards == 1) {
            log += " card"
        } else {
            log += " cards"
        }
        addGameLog(log)

        for (i in 0 until numCards) {
            if (deck.isEmpty()) {
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
        addGameLog("Shuffling deck")
        Collections.shuffle(deck)
        shuffles++
    }

    private fun cardRemovedFromPlay(card: Card) {
        card.removedFromPlay(this)
    }

    fun addTrade(trade: Int) {
        this.trade += trade
        tradeGainedThisTurn += trade
    }

    fun endTurn() {
        addGameLog("Ending turn")

        currentTurnSummary.cardsPlayed.addAll(played)

        lastTurnSummary = currentTurnSummary

        currentTurnSummary = TurnSummary()

        turns++

        trade = 0
        actions = 0
        buys = 0

        isNextCardToTopOfDeck = false
        isNextCardToHand = false

        numCardsTrashpedThisTurn = 0
        tradeGainedThisTurn = 0
        combatGainedThisTurn = 0
        authorityGainedThisTurn = 0
        shipsPlayedThisTurn.clear()

        played.clear()

        for (card in inPlay) {
            discard.add(card)
            cardRemovedFromPlay(card)
        }

        inPlay.clear()

        discard.addAll(hand)
        hand.clear()

        drawCards(5)

        isYourTurn = false

        game.turnEnded()
    }

    abstract fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String)
    abstract fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String)
    abstract fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String)
    abstract fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String)

    abstract fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String)

    fun trashCardFromDiscard(card: Card) {
        addGameLog("Trashed " + card.name + " from discard")
        discard.remove(card)
        playerCardTrashed(card)
    }

    fun trashCardFromHand(card: Card) {
        addGameLog("Trashed " + card.name + " from hand")
        hand.remove(card)
        playerCardTrashed(card)
    }

    private fun playerCardTrashed(card: Card) {
        game.trashedCards.add(card)
        cardRemovedFromPlay(card)
        numCardsTrashpedThisTurn++

        if (isYourTurn) {
            currentTurnSummary.trashedCards.add(card)
        }
    }

    fun trashCardInPlayForBenefit(card: Card) {
        addGameLog("Trashed " + card.name + " from in play for benefit")
        inPlay.remove(card)

        var cardToTrash = card

        if (card is CardCopier) {
            val cardCopier = card
            if (cardCopier.cardBeingCopied != null) {
                cardToTrash = cardCopier.cardBeingCopied!!
            }
        }

        playerCardTrashed(cardToTrash)
    }

    fun acquireCardToTopOfDeck(card: Card) {
        acquireCardToTopOfDeck = true
        cardAcquired(card)
    }

    fun acquireCardToHand(card: Card) {
        acquireCardToHand = true
        cardAcquired(card)
    }

    @JvmOverloads
    fun addCardToTopOfDeck(card: Card, addGameLog: Boolean = true) {
        deck.add(0, card)
        if (addGameLog) {
            addGameLog("Added " + card.name + " to top of deck")
        }
    }

    @JvmOverloads
    fun addCardToHand(card: Card, addToGameLog: Boolean = true) {
        hand.add(card)
        if (addToGameLog) {
            addGameLog("Added " + card.name + " to hand")
        }
    }

    fun cardAcquired(card: Card) {
        if (acquireCardToHand) {
            acquireCardToHand = false
            addCardToHand(card)
        } else if (acquireCardToTopOfDeck) {
            acquireCardToTopOfDeck = false
            addCardToTopOfDeck(card)
        } else if (isNextCardToTopOfDeck) {
            isNextCardToTopOfDeck = false
            addCardToTopOfDeck(card)
        } else if (isNextCardToHand) {
            isNextCardToHand = false
            addCardToHand(card)
        } else {
            discard.add(card)
        }

        if (isYourTurn) {
            currentTurnSummary.cardsAcquired.add(card)
        }
    }

    abstract fun makeChoice(card: ChoiceActionCard, vararg choices: Choice)

    abstract fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice)

    abstract fun trashCardFromHand(optional: Boolean)

    fun buyCard(card: Card) {
        if (trade >= this.getCardCostWithModifiers(card)) {
            addGameLog("Bought card: " + card.name)
            trade -= this.getCardCostWithModifiers(card)
            game.removeCardFromSupply(card)
            cardAcquired(card)
        }
    }

    val allCards: List<Card>
        get() {
            val cards = ArrayList<Card>()
            cards.addAll(hand)
            cards.addAll(deck)
            cards.addAll(discard)
            cards.addAll(inPlay)

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

    fun playCard(card: Card) {
        if (!card.isCopied) {
            game.gameLog("Played card: " + card.name)

            played.add(card)
            inPlay.add(card)
            hand.remove(card)
        }

        card.cardPlayed(this)
    }

    fun countCardsByType(cards: List<Card>, typeMatcher: Function<Card, Boolean>): Int {
        return cards.filter({ typeMatcher.apply(it) }).count()
    }

    val currentDeckNumber: Int
        get() = shuffles + 1

    abstract fun acquireFreeCard(maxCost: Int?)

    abstract fun acquireFreeCardToTopOfDeck(maxCost: Int?)

    abstract fun acquireFreeCardToHand(maxCost: Int?)

    abstract fun acquireFreeCardOfTypeToHand(maxCost: Int?, cardType: CardType)

    abstract fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int)

    abstract fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String)

    fun acquireFreeCardFromSupply(card: Card) {
        if (game.isCardAvailableInSupply(card)) {
            game.removeCardFromSupply(card)
            cardAcquired(card)
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

    fun addGameLog(log: String) {
        if (game != null) {
            game!!.gameLog(log)
        }
    }

    fun addCardToDiscard(card: Card) {
        discard.add(card)
    }

    fun discardCardFromHand() {
        discardCardsFromHand(1)
    }

    fun discardCardFromHand(card: Card) {
        hand.remove(card)
        addCardToDiscard(card)
        addGameLog(playerName + " discarded " + card.name + " from hand")
    }

    abstract fun discardCardsFromHand(cards: Int)

    fun resolveActions() {
        if (!actionsQueue.isEmpty()) {
            val action = actionsQueue.removeAt(0)
            processNextAction(action)
        }
    }

    private fun processNextAction(action: Action) {
        if (action.processAction(this)) {
            currentAction = action
        } else {
            action.onNotUsed(this)
            resolveActions()
        }
    }

    fun isCardBuyable(card: Card): Boolean {
        return isYourTurn && this.getCardCostWithModifiers(card) <= trade
    }

    fun addCardToDeck(card: Card) {
        deck.add(card)
    }

    fun setup() {
        Collections.shuffle(deck)
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
            }
        }
    }

    fun playAll() {
        val copyOfHand = ArrayList(hand)
        copyOfHand.forEach({ this.playCard(it) })
    }

    fun startTurn() {
        isWaitingForComputer = false
        actions = 1
        buys = 1
        isYourTurn = true
        turn++
        addGameLog("")
        addGameLog("*** $playerName's Turn $turn ***")
        addGameLog("Deck: " + currentDeckNumber)

        currentTurnSummary = TurnSummary()
        currentTurnSummary.gameTurn = game.turn

        resolveActions()

        takeTurn()
    }

    abstract fun takeTurn()

    abstract fun addCardAction(card: CardActionCard, text: String)

    abstract fun addCardFromDiscardToTopOfDeck(maxCost: Int?)

    abstract fun addCardFromHandToTopOfDeck()

    val isBot: Boolean = this is BotPlayer

    override fun hashCode(): Int {
        return Objects.hash(playerName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val other = other as Player
        return this.playerName == other.playerName
    }

    fun revealTopCardsOfDeck(cards: Int): List<Card> {
        val revealedCards = ArrayList<Card>()

        if (deck.size < cards) {
            //todo account for cards in hand before shuffle list
            shuffleDiscardIntoDeck()
        }

        if (deck.isEmpty()) {
            addGameLog(playerName + " had no cards to reveal")
        } else {
            for (i in 0..cards - 1) {
                if (deck.size < i + 1) {
                    addGameLog("No more cards to reveal")
                } else {
                    val card = deck[i]
                    addGameLog(playerName + " revealed " + card.name + " from top of deck")
                    revealedCards.add(card)
                }
            }
        }

        return revealedCards
    }

    val cardOnTopOfDiscard: Card?
        get() {
            if (!discard.isEmpty()) {
                return discard.last()
            }
            return null
        }

    open val infoForGameLogName: String
        get() = playerName.replace("\\s".toRegex(), "_")

    fun getCardCostWithModifiers(card: Card): Int {
        if (cardCostModifier != null) {
            return cardCostModifier!!.getCardCost(card, this)
        }

        return card.cost
    }

    fun discardTopCardOfDeck(): Card? {
        val cardsFromDeck = getCardsFromDeck(1)
        if (cardsFromDeck.isEmpty()) {
            return null
        } else {
            val discardedCard = cardsFromDeck.get(0)
            addCardToDiscard(discardedCard)
            return discardedCard
        }
    }
}
