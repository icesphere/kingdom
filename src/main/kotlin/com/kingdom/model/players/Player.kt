package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.TurnSummary
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.listeners.BeforeAttackListener
import com.kingdom.model.cards.listeners.CardPlayedListener
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.util.KingdomUtil
import com.kingdom.util.toCardNames
import java.util.*
import java.util.function.Function

abstract class Player protected constructor(val user: User, val game: Game) {
    var deck: MutableList<Card> = ArrayList()
    val hand: MutableList<Card> = ArrayList()
    val discard: MutableList<Card> = ArrayList()
    val bought: MutableList<Card> = ArrayList()
    val played: MutableList<Card> = ArrayList()
    private val inPlay: MutableList<Card> = ArrayList()

    val numCards: Int
        get() = allCards.size

    val userId = user.userId
    val username = user.username
    val gender = user.gender
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

    var isNextCardToTopOfDeck: Boolean = false

    var isNextCardToHand: Boolean = false

    private var shuffles: Int = 0

    private var isFirstPlayer: Boolean = false

    var turns: Int = 0

    var turn: Int = 0

    private var numCardsTrashedThisTurn: Int = 0

    private var coinsGainedThisTurn: Int = 0

    private var lastTurnSummary: TurnSummary? = null

    private var currentTurnSummary = TurnSummary()

    var isWaitingForComputer: Boolean = false

    private var acquireCardToTopOfDeck: Boolean = false

    private var acquireCardToHand: Boolean = false

    lateinit var chatColor: String

    var isQuit: Boolean = false

    var victoryCoins: Int = 0

    var victoryPoints: Int = 0

    private var finalPointsCalculated = false
    var finalVictoryPoints = 0
    private var finalCards: List<Card>? = null

    var isWinner: Boolean = false
    var marginOfVictory: Int = 0

    val groupedHand: List<Card>
        get() {
            KingdomUtil.groupCards(hand)
            return hand
        }

    val currentHand: String
        get() = KingdomUtil.groupCards(hand, true)

    val allCardsString: String
        get() = KingdomUtil.groupCards(allCards, true)

    val playTreasureCards: Boolean = game.isPlayTreasureCards

    val isTreasureCardsPlayed: Boolean
        get() = game.cardsPlayed.any { it.isTreasure }

    val isCardsBought: Boolean
        get() = game.cardsBought.isNotEmpty()

    val isOpponentHasAction: Boolean
        get() = opponents.any { it.currentAction != null }

    var numActionsPlayed = 0

    init {
        if (game.isIdenticalStartingHands && game.players.size > 0) {
            val firstPlayer = game.players[0]
            deck.addAll(firstPlayer.deck)
            for (card in firstPlayer.hand) {
                addCardToHand(card)
            }
        } else {
            for (i in 1..7) {
                deck.add(Copper())
            }

            for (i in 1..3) {
                deck.add(Estate())
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
        cards.forEach { addCardToHand(it, false) }
        return cards
    }

    private fun getCardsFromDeck(numCards: Int): List<Card> {
        if (numCards == 0) {
            return ArrayList()
        }

        val cardsDrawn = ArrayList<Card>()
        var log = "$username drawing $numCards"
        log += if (numCards == 1) {
            " card"
        } else {
            " cards"
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

    private fun shuffleDiscardIntoDeck() {
        deck.addAll(discard)
        discard.clear()
        addGameLog("Shuffling deck")
        deck.shuffle()
        shuffles++
    }

    private fun cardRemovedFromPlay(card: Card) {
        card.removedFromPlay(this)
    }

    fun addCoins(coins: Int) {
        this.coins += coins
        coinsGainedThisTurn += coins
        game.refreshSupply()
        game.refreshPlayerCardsBought(this)
    }

    fun addActions(actions: Int) {
        this.actions += actions
        game.refreshPlayerCardsPlayed(this)
    }

    fun addBuys(buys: Int) {
        this.buys += buys
        game.refreshPlayerCardsBought(this)
    }

    fun endTurn() {

        addGameLog("Ending turn")

        currentTurnSummary.cardsPlayed.addAll(played)

        lastTurnSummary = currentTurnSummary

        currentTurnSummary = TurnSummary()

        turns++

        coins = 0
        coinsSpent = 0
        actions = 0
        buys = 0

        numActionsPlayed = 0

        isNextCardToTopOfDeck = false
        isNextCardToHand = false

        numCardsTrashedThisTurn = 0
        coinsGainedThisTurn = 0

        bought.clear()
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
        addGameLog("Trashed " + card.cardNameWithBackgroundColor + " from discard")
        discard.remove(card)
        cardTrashed(card)
        game.refreshPlayerHandArea(this)
    }

    fun trashCardFromHand(card: Card) {
        addGameLog("Trashed " + card.cardNameWithBackgroundColor + " from hand")
        hand.remove(card)
        cardTrashed(card)
        game.refreshPlayerHandArea(this)
    }

    fun cardTrashed(card: Card) {
        game.trashedCards.add(card)
        cardRemovedFromPlay(card)
        numCardsTrashedThisTurn++

        if (isYourTurn) {
            currentTurnSummary.trashedCards.add(card)
        }
    }

    fun trashCardInPlay(card: Card) {
        addGameLog("Trashed " + card.cardNameWithBackgroundColor + " from in play")
        inPlay.remove(card)
        cardTrashed(card)
        game.refreshCardsPlayed()
    }

    fun acquireCardToTopOfDeck(card: Card) {
        acquireCardToTopOfDeck = true
        cardAcquired(card)
        game.refreshPlayerHandArea(this)
    }

    fun acquireCardToHand(card: Card) {
        acquireCardToHand = true
        cardAcquired(card)
        game.refreshPlayerHandArea(this)
    }

    fun addCardToTopOfDeck(card: Card, addGameLog: Boolean = true) {
        deck.add(0, card)
        if (addGameLog) {
            addGameLog("Added " + card.cardNameWithBackgroundColor + " to top of deck")
        }
        game.refreshPlayerHandArea(this)
    }

    private fun addCardToHand(card: Card, addToGameLog: Boolean = true) {
        hand.add(card)
        if (addToGameLog) {
            addGameLog("Added " + card.cardNameWithBackgroundColor + " to hand")
        }
        game.refreshPlayerHandArea(this)
    }

    fun cardAcquired(card: Card) {
        when {
            acquireCardToHand -> {
                acquireCardToHand = false
                addCardToHand(card)
            }
            acquireCardToTopOfDeck -> {
                acquireCardToTopOfDeck = false
                addCardToTopOfDeck(card)
            }
            isNextCardToTopOfDeck -> {
                isNextCardToTopOfDeck = false
                addCardToTopOfDeck(card)
            }
            isNextCardToHand -> {
                isNextCardToHand = false
                addCardToHand(card)
            }
            else -> {
                addCardToDiscard(card)
            }
        }

        if (isYourTurn) {
            currentTurnSummary.cardsAcquired.add(card)
        }
    }

    abstract fun makeChoice(card: ChoiceActionCard, vararg choices: Choice)

    abstract fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice)

    fun trashCardFromHand(optional: Boolean) {
        trashCardsFromHand(1, optional)
    }

    abstract fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean)

    abstract fun trashCardFromSupply(optional: Boolean)

    abstract fun gainCardFromTrash(optional: Boolean)

    abstract fun passCardFromHandToPlayerOnLeft()

    fun buyCard(card: Card) {
        if (availableCoins >= this.getCardCostWithModifiers(card)) {
            addGameLog("Bought card: " + card.cardNameWithBackgroundColor)
            coins -= this.getCardCostWithModifiers(card)
            buys -= 1
            bought.add(card)
            game.cardsBought.add(card)
            game.removeCardFromSupply(card)
            game.refreshCardsBought()
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

    fun playCard(card: Card, repeatedAction: Boolean = false) {

        game.addHistory("Played card: ${card.cardNameWithBackgroundColor}")

        if (!repeatedAction) {

            played.add(card)
            game.cardsPlayed.add(card)

            if (card is CardCostModifier) {
                game.cardCostModifiers.add(card)
            }

            inPlay.filter { it is CardPlayedListener }
                    .forEach { (it as CardPlayedListener).onCardPlayed(card, this) }

            inPlay.add(card)
            hand.remove(card)

            game.refreshPlayerHandArea(this)
            game.refreshCardsPlayed()
        }

        if (card.isAction) {
            numActionsPlayed++
        }

        card.cardPlayed(this)
    }

    private fun countCardsByType(cards: List<Card>, typeMatcher: Function<Card, Boolean>): Int {
        return cards.filter({ typeMatcher.apply(it) }).count()
    }

    private val currentDeckNumber: Int
        get() = shuffles + 1

    abstract fun acquireFreeCard(maxCost: Int?)

    abstract fun acquireFreeCardForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard)

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

    fun addUsernameGameLog(log: String) {
        game.addHistory("$username $log")
    }

    fun addGameLog(log: String) {
        game.addHistory(log)
    }

    fun addCardToDiscard(card: Card) {
        discard.add(card)
        cardRemovedFromPlay(card)
        game.refreshPlayerHandArea(this)
    }

    fun discardCardFromHand() {
        discardCardsFromHand(1, false)
    }

    fun discardCardFromHand(card: Card) {
        hand.remove(card)
        addCardToDiscard(card)
        addGameLog(username + " discarded " + card.cardNameWithBackgroundColor + " from hand")
    }

    abstract fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean)

    abstract fun waitForOtherPlayersToResolveActions()

    abstract fun waitForOtherPlayersForResolveAttack(attackCard: Card)

    abstract fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler)

    fun resolveActions() {
        if (!actionsQueue.isEmpty()) {
            val action = actionsQueue.removeAt(0)
            processNextAction(action)
        }
    }

    @Suppress("CascadeIf")
    private fun processNextAction(action: Action) {
        if (action is SelfResolvingAction) {
            if (action is RepeatCardAction && actionsQueue.isNotEmpty()) {
                //previous play of card created action that created another action so that needs to be resolved before repeating action
                actionsQueue.add(action)
                resolveActions()
            } else {
                action.resolveAction(this)
                resolveActions()
            }
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
        return isYourTurn && this.getCardCostWithModifiers(card) <= availableCoins
    }

    fun addCardToDeck(card: Card) {
        deck.add(card)
        game.refreshPlayerHandArea(this)
    }

    fun addCardToHand(card: Card) {
        hand.add(card)
        game.refreshPlayerHandArea(this)
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

                for (opponent in opponents) {
                    if (opponent.currentAction is WaitForOtherPlayersActions) {
                        if (opponent.currentAction!!.processActionResult(this, result)) {
                            opponent.currentAction = null
                            opponent.game.refreshPlayerCardAction(opponent)
                            opponent.resolveActions()
                        }
                    }
                }

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

        game.showInfoMessage(this, "Your turn")

        addGameLog("")
        addGameLog("*** $username's Turn $turn ***")
        addGameLog("Deck: $currentDeckNumber")

        currentTurnSummary = TurnSummary()
        currentTurnSummary.gameTurn = game.turn

        resolveActions()

        takeTurn()
    }

    abstract fun takeTurn()

    abstract fun addCardAction(card: CardActionCard, text: String)

    fun addRepeatCardAction(card: Card) {
        actionsQueue.add(RepeatCardAction(card))
    }

    abstract fun addCardFromDiscardToTopOfDeck(maxCost: Int?)

    abstract fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)? = null)

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
        hand.forEach {
            addGameLog("$username revealed their hand: ${hand.toCardNames()}")
        }
    }

    fun revealCardFromHand(card: Card) {
        addGameLog("$username revealed ${card.cardNameWithBackgroundColor} from their hand")
    }

    fun revealTopCardsOfDeck(cards: Int): List<Card> {
        val revealedCards = ArrayList<Card>()

        if (deck.size < cards) {
            shuffleDiscardIntoDeck()
        }

        if (deck.isEmpty()) {
            addGameLog("$username had no cards to reveal")
        } else {
            for (i in 0 until cards) {
                if (deck.size < i + 1 && discard.isNotEmpty()) {
                    shuffleDiscardIntoDeck()
                }
                if (deck.size < i + 1) {
                    addGameLog("No more cards to reveal")
                } else {
                    val card = deck[i]
                    addGameLog("$username revealed ${card.cardNameWithBackgroundColor} from top of deck")
                    revealedCards.add(card)
                }
            }
        }

        return revealedCards
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

    fun removeCardFromDeck(card: Card) {
        deck.remove(card)
        game.refreshPlayerHandArea(this)
    }

    fun removeCardFromHand(card: Card) {
        hand.remove(card)
        game.refreshPlayerHandArea(this)
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

        return cost
    }

    fun discardTopCardOfDeck(): Card? {
        val cardsFromDeck = getCardsFromDeck(1)
        if (cardsFromDeck.isEmpty()) {
            return null
        }

        val discardedCard = cardsFromDeck[0]

        addGameLog("$username discarded ${discardedCard.cardNameWithBackgroundColor}")

        addCardToDiscard(discardedCard)

        return discardedCard
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

    abstract fun selectCardsToTrashFromDeck(cardsThatCanBeTrashed: List<Card>, numCardsToTrash: Int, optional: Boolean)

    abstract fun putCardsOnTopOfDeckInAnyOrder(cards: List<Card>)

    fun cardCountByName(cardName: String): Int {
        return allCards.count { it.name == cardName }
    }

    fun cardCountByExpression(expression: ((card: Card) -> Boolean)): Int {
        return allCards.count{ expression(it) }
    }

    fun canBuyCard(card: Card): Boolean {
        val cost = getCardCostWithModifiers(card)
        return game.isCardAvailableInSupply(card) && availableCoins >= cost
    }

    abstract fun chooseCardForOpponentToGain(cost: Int, text: String, destination: CardLocation, opponent: Player)

    abstract fun chooseCardFromHand(text: String, chooseCardFromhandActionCard: ChooseCardFromHandActionCard)

    fun triggerAttack(attackCard: Card) {

        opponents.forEach { opponent ->
            opponent.hand.filter { it is BeforeAttackListener }
                    .forEach { (it as BeforeAttackListener).onBeforeAttack(attackCard, opponent, this) }
        }

        if (isOpponentHasAction) {
            waitForOtherPlayersForResolveAttack(attackCard)
        } else {
            val attackResolver = attackCard as AttackCard
            attackResolver.resolveAttack(this, opponents.filterNot { attackCard.playersExcludedFromCardEffects.contains(it) })
        }
    }
}
