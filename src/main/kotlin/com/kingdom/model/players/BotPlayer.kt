package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.kingdom.*
import com.kingdom.model.cards.prosperity.*
import com.kingdom.model.cards.seaside.*
import com.kingdom.model.cards.supply.*
import java.util.*

abstract class BotPlayer(user: User, game: Game) : Player(user, game) {

    private val random = Random()

    var difficulty = 3

    private val cardsToPlay: List<Card>
        get() {
            val actionCards = hand.filter { it.isAction }
            val treasureCards = hand.filter { it.isTreasure }

            return when {
                actions > 0 && actionCards.isNotEmpty() -> actionCards
                !isCardsBought -> treasureCards
                else -> emptyList()
            }
        }

    override fun takeTurn() {
        var endTurn = false

        while (!endTurn) {
            endTurn = true

            while (cardsToPlay.isNotEmpty()) {
                endTurn = false
                val sortedCards = cardsToPlay.sortedByDescending { getPlayCardScore(it) }

                if (sortedCards.isEmpty()) {
                    break
                }

                if (sortedCards.first().isTreasure && sortedCards.any { !it.isTreasureExcludedFromAutoPlay }) {
                    playAllTreasureCards()
                } else {
                    val card = sortedCards[0]
                    playCard(card)
                }
            }

            if (availableCoins > 0 && buys > 0) {
                val cardToBuy = getCardToBuy()
                if (cardToBuy != null) {
                    endTurn = false
                    this.buyCard(game.getSupplyCard(cardToBuy))
                }
            }
        }

        endTurn()
    }

    val availableCardsToBuy: List<Card>
        get() = (game.kingdomCards + game.supplyCards).filter { canBuyCard(it) && !excludeCard(it) }

    val availableCardsToBuyNames: List<String>
        get() = availableCardsToBuy.map { it.name }

    abstract fun getCardToBuy(): String?

    open fun excludeCard(card: Card): Boolean {
        return card.isCurseOnly
    }

    override fun addCardAction(card: CardActionCard, text: String) {
        if (!card.processCardAction(this)) {
            return
        }

        if (card is ThroneRoom) {
            val sortedCards = hand.sortedByDescending { getBuyCardScore(it) }
            val result = ActionResult().apply { selectedCard = sortedCards.first() }
            card.processCardActionResult(CardAction(card, ""), this, result)
        }
    }


    override fun addCardFromDiscardToTopOfDeck(maxCost: Int?) {
        val card = chooseCardFromDiscardToAddToTopOfDeck()
        if (card != null) {
            discard.remove(card)
            addCardToTopOfDeck(card)
        }
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String) {
        val cards = getCardsToOptionallyTrashFromHand(numCardsToTrash)

        cards.forEach({ this.trashCardFromHand(it) })
    }

    override fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String) {
        val cards = getCardsToOptionallyTrashFromHand(numCardsToTrash)

        cards.forEach({ this.trashCardFromHand(it) })

        card.cardsScrapped(this, cards)
    }

    override fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean) {
        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, optional)
        cardsToDiscard.forEach({ this.discardCardFromHand(it) })
    }

    override fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String) {
        val optionalDiscard = true

        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, optionalDiscard)

        cardsToDiscard.forEach({ this.discardCardFromHand(it) })

        if (!cardsToDiscard.isEmpty()) {
            card.cardsDiscarded(this, cardsToDiscard)
        } else {
            card.onChoseDoNotUse(this)
        }
    }

    override fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String) {
        //todo better logic

        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, false)

        if (!cardsToDiscard.isEmpty()) {
            cardsToDiscard.forEach({ this.discardCardFromHand(it) })
            card.cardsDiscarded(this, cardsToDiscard)
        } else {
            card.onChoseDoNotUse(this)
        }
    }

    override fun makeChoice(card: ChoiceActionCard, vararg choices: Choice) {
        val choice = getChoice(card, arrayOf(*choices))
        card.actionChoiceMade(this, choice)
    }

    override fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice) {
        makeChoice(card, *choices)
    }

    override fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean) {
        val card = getCardToTrashFromHand(optional)
        if (card != null) {
            trashCardFromHand(card)
        }
    }

    override fun trashCardFromSupply(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo better logic
        val card: Card = game.availableCards.filter { cardActionableExpression == null || cardActionableExpression(it) }.shuffled().first()
        game.removeCardFromSupply(card)
    }

    override fun gainCardFromTrash(optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo better logic
        val trashedCards = game.trashedCards.filter { cardActionableExpression == null || cardActionableExpression(it) }.sortedByDescending { it.cost }
        if (trashedCards.isNotEmpty()) {
            val card = trashedCards.first()
            game.trashedCards.remove(card)
            addGameLog("$username gained ${card.cardNameWithBackgroundColor} from the trash")
        }
    }

    override fun passCardFromHandToPlayerOnLeft() {
        val card = getCardToTrashFromHand(false)
        if (card != null) {
            hand.remove(card)
            val playerToLeft = game.getPlayerToLeft(this)
            playerToLeft.acquireCardToHand(card)
        }
    }

    override fun acquireFreeCard(maxCost: Int?) {
        val card = chooseFreeCardToAcquire(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

            cardAcquired(card)
        }
    }

    override fun acquireFreeCardWithCost(cost: Int) {
        val card = chooseFreeCardToAcquireExactCost(cost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

            cardAcquired(card)
        }
    }

    override fun acquireFreeCardForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard) {
        //todo logic for different cards
        val card = chooseFreeCardToAcquire(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

            cardAcquired(card)

            freeCardFromSupplyForBenefitActionCard.onCardAcquired(this, card)
        }
    }

    override fun acquireFreeCardToTopOfDeck(maxCost: Int?) {
        val card = chooseFreeCardToAcquire(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addCardToTopOfDeck(card)
            cardAcquired(card)
        }
    }

    override fun acquireFreeCardToHand(maxCost: Int?) {
        val card = chooseFreeCardToAcquire(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            acquireCardToHand(card)
        }
    }

    private val cardsToBuy: List<Card>
        get() {
            val cardsToBuy = ArrayList<Card>()

            val cardsAvailableToBuy = game.availableCards.filter { c -> availableCoins >= this.getCardCostWithModifiers(c) }

            if (cardsAvailableToBuy.isEmpty()) {
                return cardsToBuy
            }

            val sortedCards = cardsAvailableToBuy.sortedByDescending { getBuyCardScore(it) }

            if (cardsAvailableToBuy.size > 1) {
                val card = pickCardBasedOnBuyScore(sortedCards)
                if (card != null && card != sortedCards[0]) {
                    Collections.swap(sortedCards, 0, 1)
                }
            }

            if (!sortedCards.isEmpty() && getBuyCardScore(sortedCards[0]) > 0) {
                val cardWithHighestBuyScore = sortedCards[0]

                if (sortedCards.size > 2 && buys > 1) {
                    val cardToBuyScoreMap = HashMap<Card, Int>()

                    for (card in cardsAvailableToBuy) {
                        if (!cardToBuyScoreMap.containsKey(card)) {
                            cardToBuyScoreMap[card] = getBuyCardScore(card)
                        }
                    }

                    val twoCardsList = ArrayList<List<Card>>()

                    for (i in 1 until sortedCards.size - 1) {
                        val cardToCompareAgainst = sortedCards[i]
                        for (j in i + 1 until sortedCards.size) {
                            if (addTwoCardListIfEnoughTrade(twoCardsList, cardToCompareAgainst, sortedCards[j])) {
                                break
                            }
                        }
                    }

                    for (cardList in twoCardsList) {
                        var totalBuyScore = 0
                        totalBuyScore += cardToBuyScoreMap[cardList[0]]!!
                        totalBuyScore += cardToBuyScoreMap[cardList[1]]!!

                        if (totalBuyScore > cardToBuyScoreMap[cardWithHighestBuyScore]!!) {
                            return cardList
                        }
                    }
                }

                cardsToBuy.add(cardWithHighestBuyScore)
            }

            return cardsToBuy
        }

    private fun addTwoCardListIfEnoughTrade(twoCardList: MutableList<List<Card>>, card1: Card, card2: Card): Boolean {
        if (this.getCardCostWithModifiers(card1) + this.getCardCostWithModifiers(card2) <= availableCoins) {
            val cards = ArrayList<Card>(2)
            cards.add(card1)
            cards.add(card2)
            twoCardList.add(cards)
            return true
        }
        return false
    }

    open fun getBuyCardScore(card: Card): Int {
        //todo better logic

        if (card.isCurseOnly) {
            return -1
        }

        return card.cost
    }

    private fun getPlayCardScore(card: Card): Int {
        //todo
        return card.cost
    }

    fun getCardToTopOfDeckScore(card: Card): Int {
        return getBuyCardScore(card)
    }

    private fun getDiscardCardScore(card: Card): Int {
        //todo
        return when {
            card.isCurseOnly -> 200
            card.isVictory -> 100
            card.isCopper -> 90
            else -> 20 - card.cost
        }
    }

    private fun getTrashCardScore(card: Card): Int {
        //todo
        return when (card) {
            is Curse -> 200
            is Estate -> 100
            is Copper -> 90
            else -> 20 - card.cost
        }

    }

    private fun getReturnCardToTopOfDeckScore(card: Card): Int {
        return 1000 - getBuyCardScore(card)
    }

    open fun getChoice(choiceActionCard: ChoiceActionCard, choices: Array<Choice>): Int {
        val card = choiceActionCard as Card

        return when (choiceActionCard.name) {
            Ambassador.NAME -> choices.last().choiceNumber
            Baron.NAME -> 1
            CountingHouse.NAME -> choices.last().choiceNumber
            Courtier.NAME -> when {
                actions == 0 && hand.any { it.isAction && it.cost > 3 } -> 1
                buys < 2 && availableCoins > 11 -> 2
                buys > 1 || turns > 10 -> 3
                else -> 4
            }
            Diplomat.NAME -> if (hand.count { getDiscardCardScore(it) > 50 } > 2) 1 else 2
            Explorer.NAME -> 1
            Library.NAME -> if (actions > 0 && hand.none { it.isAction }) 1 else 2
            Loan.NAME -> if (card.isCopper) 2 else 1
            Lurker.NAME -> if (game.trashedCards.any { getBuyCardScore(it) > 4 }) 2 else 1
            Mill.NAME ->  if (hand.count { getDiscardCardScore(it) > 50 } > 1) 1 else 2
            MiningVillage.NAME -> when {
                game.availableCards.any { it.isColony } && availableCoins < 11 && availableCoins > 8 -> 1
                turns > 7 && game.availableCards.any { it.isProvince } && availableCoins < 8 && availableCoins > 5 -> 1
                else -> 2
            }
            Minion.NAME -> if (hand.size < 4 || hand.count { getDiscardCardScore(it) > 50 } > 2) 2 else 1
            Moat.NAME -> 1
            Moneylender.NAME -> 1
            Mountebank.NAME -> 1
            NativeVillage.NAME -> if (nativeVillageCards.size < 2) 1 else 2
            Nobles.NAME -> if (actions == 0 && hand.any { it.isAction }) 2 else 1
            RoyalSeal.NAME -> if (card.cost > 2) 1 else 2
            Pawn.NAME -> when {
                actions == 0 && hand.any { it.isAction } -> when {
                    availableCoins > 11 && buys == 1 -> 4
                    else -> 1
                }
                availableCoins > 11 && buys == 1 -> 2
                else -> 3
            }
            PearlDiver.NAME -> if (getBuyCardScore(card) > 3) 1 else 2
            PirateShip.NAME -> if (pirateShipCoins > 2) 1 else 2
            Sentry.NAME -> when (card.cost) {
                0 -> 1
                1 -> 2
                2 -> 2
                else -> 3
            }
            Steward.NAME -> when {
                turns < 5 && hand.count { it.cost <= 2 } >= 2 -> 3
                actions > 0 -> 1
                else -> 2
            }
            Torturer.NAME -> 1
            Treasury.NAME -> 1
            Vassal.NAME -> 1
            Vault.NAME -> if (hand.count { getDiscardCardScore(it) > 50 } > 1) 1 else 2
            Watchtower.NAME -> if (card.cost > 2) 1 else 2
            else -> choices[0].choiceNumber
        }
    }

    private fun getCardsToDiscard(cards: Int, optional: Boolean): List<Card> {
        var numCardsToDiscard = cards
        val cardsToDiscard = ArrayList<Card>()

        if (!hand.isEmpty()) {
            if (numCardsToDiscard > hand.size) {
                numCardsToDiscard = hand.size
            }
            val sortedCards = hand.sortedByDescending { getDiscardCardScore(it) }
            for (i in 0 until numCardsToDiscard) {
                val card = sortedCards[i]
                val score = getDiscardCardScore(card)
                if (hand.isEmpty() || optional && score < 20) {
                    break
                } else {
                    cardsToDiscard.add(card)
                }
            }
        }

        return cardsToDiscard
    }

    private fun getCardToTrashFromHand(optional: Boolean): Card? {
        if (!hand.isEmpty()) {
            val sortedCards = hand.sortedByDescending { getTrashCardScore(it) }
            val card = sortedCards[0]
            if (optional && getTrashCardScore(card) < 20) {
                return null
            }
            return card
        }
        return null
    }


    private fun getCardsToTrashFromHand(cards: Int): List<Card> {
        val cardsToTrashFromHand = ArrayList<Card>()

        if (!hand.isEmpty()) {
            val sortedHandCards = hand.sortedByDescending { getTrashCardScore(it) }

            for (i in 0 until cards) {
                if (sortedHandCards.size <= i) {
                    break
                }
                val card = sortedHandCards[i]
                cardsToTrashFromHand.add(card)
                if (cardsToTrashFromHand.size == cards) {
                    break
                }
            }
        }

        return cardsToTrashFromHand
    }

    private fun getCardsToOptionallyTrashFromHand(cards: Int): List<Card> {
        val cardsToTrashFromHand = ArrayList<Card>()

        if (!hand.isEmpty()) {
            val sortedHandCards = hand.sortedByDescending { getTrashCardScore(it) }

            for (i in 0 until cards) {
                if (sortedHandCards.size <= i) {
                    break
                }
                val card = sortedHandCards[i]
                val score = getTrashCardScore(card)
                if (score < 20) {
                    break
                } else {
                    cardsToTrashFromHand.add(card)
                    if (cardsToTrashFromHand.size == cards) {
                        break
                    }
                }
            }
        }

        return cardsToTrashFromHand
    }

    private fun getCardsToTrashFromDeck(cards: List<Card>, numCardsToTrash: Int, optional: Boolean): List<Card> {
        val cardsToTrashFromDeck = ArrayList<Card>()

        if (cards.isNotEmpty()) {
            val sortedCards = cards.sortedByDescending { getTrashCardScore(it) }

            for (i in 0 until numCardsToTrash) {
                if (sortedCards.size <= i) {
                    break
                }
                val card = sortedCards[i]

                if (optional && getTrashCardScore(card) < 20) {
                    break
                }

                cardsToTrashFromDeck.add(card)
                if (cardsToTrashFromDeck.size == numCardsToTrash) {
                    break
                }
            }
        }

        return cardsToTrashFromDeck
    }

    fun getBuyScoreIncrease(extraTrade: Int): Int {
        var cardToBuyScore = 0

        val cardsToBuy = cardsToBuy
        if (!cardsToBuy.isEmpty()) {
            for (cardToBuy in cardsToBuy) {
                cardToBuyScore += getBuyCardScore(cardToBuy)
            }
        }

        val sortedCards = game.availableCards
                .filter { c -> availableCoins + extraTrade >= this.getCardCostWithModifiers(c) }
                .sortedByDescending { getBuyCardScore(it) }

        if (!sortedCards.isEmpty()) {
            val bestCardScore = getBuyCardScore(sortedCards[0])
            return bestCardScore - cardToBuyScore
        }

        return 0
    }

    fun getHighestBuyScoreForTrade(trade: Int): Int {
        val sortedCards = game.availableCards.filter { c -> trade >= this.getCardCostWithModifiers(c) }.sortedByDescending { getBuyCardScore(it) }
        if (!sortedCards.isEmpty()) {
            return getBuyCardScore(sortedCards[0])
        }

        return 0
    }

    private fun chooseCardFromDiscardToAddToTopOfDeck(): Card? {
        return pickCardBasedOnBuyScore(discard)
    }

    private fun pickCardBasedOnBuyScore(cards: List<Card>?): Card? {
        if (cards == null || cards.isEmpty()) {
            return null
        }

        val sortedCards = cards.sortedByDescending { getBuyCardScore(it) }

        val firstCard = sortedCards[0]

        val firstBuyScore = getBuyCardScore(firstCard)

        val randomPercent = random.nextInt(100)

        if (sortedCards.size == 1) {
            if (firstBuyScore == 0) {
                if (randomPercent <= 2) {
                    return firstCard
                } else {
                    return null
                }
            } else {
                return firstCard
            }
        }

        val secondCard = sortedCards[1]

        val secondBuyScore = getBuyCardScore(secondCard)

        if (secondBuyScore == 0) {
            if (firstBuyScore < 10 && randomPercent <= 3) {
                return secondCard
            } else {
                return firstCard
            }
        }

        if (firstBuyScore - secondBuyScore > 10) {
            return firstCard
        }

        val percentageForFirstCard = firstBuyScore / (firstBuyScore + secondBuyScore) * 100

        if (randomPercent < percentageForFirstCard + 5) {
            return firstCard
        } else {
            return secondCard
        }
    }

    private fun chooseFreeCardToAcquire(maxCost: Int?, cardType: CardType? = null): Card? {
        val cards = game.availableCards
                .filter { c ->
                    (maxCost == null || this.getCardCostWithModifiers(c) <= maxCost)
                            && (cardType == null || cardType == c.type)
                }

        return pickCardBasedOnBuyScore(cards)
    }

    private fun chooseFreeCardToAcquireExactCost(cost: Int): Card? {
        val cards = game.availableCards
                .filter { this.getCardCostWithModifiers(it) == cost }

        return pickCardBasedOnBuyScore(cards)
    }

    override fun drawCardsAndPutSomeBackOnTop(cardsToDraw: Int, cardsToPutBack: Int) {
        var numCardsToPutBack = cardsToPutBack
        val cards = drawCards(cardsToDraw)

        if (!cards.isEmpty()) {
            val cardsPutBack = 0

            val sortedCards = cards.sortedByDescending { getReturnCardToTopOfDeckScore(it) }

            for (card in sortedCards) {
                if (cardsPutBack <= numCardsToPutBack) {
                    hand.remove(card)
                    addCardToTopOfDeck(card, false)
                    numCardsToPutBack++
                }
            }
        }
    }

    override fun putCardsOnTopOfDeckInAnyOrder(cards: List<Card>) {
        cards.forEach { addCardToTopOfDeck(it, false) }
    }

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String) {
        val cards = getCardsToTrashFromHand(numCardsToTrash)
        cards.forEach { this.trashCardFromHand(it) }
        card.cardsScrapped(this, cards)
    }

    override fun acquireFreeCardOfTypeToHand(maxCost: Int?, cardType: CardType) {
        val card = chooseFreeCardToAcquire(maxCost, cardType)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.cardNameWithBackgroundColor)

            cardAcquired(card)
        }
    }

    override fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String) {
        val choice = getChoice(choiceActionCard, arrayOf(Choice(1, "Yes"), Choice(2, "No")))
        choiceActionCard.actionChoiceMade(this, choice)
    }

    override fun addCardFromHandToTopOfDeck(cardFilter: ((Card) -> Boolean)?, chooseCardActionCard: ChooseCardActionCard?) {
        val cards =
                if (cardFilter != null) {
                    hand.filter(cardFilter)
                } else hand

        if (cards.isEmpty()) {
            return
        }

        val chosenCard = when {
            actions == 0 && cards.any { it.isAction && it.cost > 2 } -> cards.filter { it.isAction }.maxBy { it.cost }!!
            buys == 1 && availableCoins > game.availableCards.maxBy { it.cost }!!.cost -> cards.filter { it.isTreasure }.minBy { it.cost }!!
            else -> cards.maxBy { getDiscardCardScore(it) }!!
        }

        hand.remove(chosenCard)
        addCardToTopOfDeck(chosenCard, false)
        chooseCardActionCard?.onCardChosen(this, chosenCard)
    }

    override fun waitForOtherPlayersToResolveActions() {
        //do nothing
    }

    override fun waitForOtherPlayersForResolveAttack(attackCard: Card) {
        //do nothing
    }

    override fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler) {
        //todo
    }

    override fun chooseCardForOpponentToGain(cost: Int, text: String, destination: CardLocation, opponent: Player) {
        val availableCards = game.availableCards.filter { getCardCostWithModifiers(it) == cost }
        if (availableCards.isEmpty()) {
            return
        }

        val card = availableCards.minBy { getBuyCardScore(it) }!!

        game.removeCardFromSupply(card)

        when (destination) {
            CardLocation.Hand -> {
                addGameLog("$username put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s hand")
                opponent.acquireCardToHand(card)
            }
            CardLocation.Deck -> {
                addGameLog("$username put ${card.cardNameWithBackgroundColor} on top of ${opponent.username}'s deck")
                opponent.acquireCardToTopOfDeck(card)
            }
            else -> {
                addGameLog("$username put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s discard")
                opponent.cardAcquired(card)
            }
        }
    }

    override fun chooseCardFromHand(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        val cards = hand.filter { cardActionableExpression == null || cardActionableExpression.invoke(it) }

        if (cards.isEmpty()) {
            return
        }

        val card = chooseCardActionCard as Card

        val chosenCard: Card = when (card.name) {
            Ambassador.NAME -> cards.minBy { getBuyCardScore(it) }!!
            Courtier.NAME -> cards.maxBy { it.numTypes }!!
            Haven.NAME -> when {
                actions == 0 && cards.any { it.isAction && it.cost > 2 } -> cards.filter { it.isAction }.maxBy { it.cost }!!
                buys == 1 && availableCoins > game.availableCards.maxBy { it.cost }!!.cost -> cards.filter { it.isTreasure }.minBy { it.cost }!!
                else -> cards.maxBy { getDiscardCardScore(it) }!!
            }
            Island.NAME -> when {
                cards.any { it.isVictoryOnly } -> cards.first { it.isVictoryOnly }
                else -> cards.maxBy { getTrashCardScore(it) }!!
            }
            Mint.NAME -> cards.maxBy { getBuyCardScore(it) }!!
            else -> cards.first()
        }

        chooseCardActionCard.onCardChosen(this, chosenCard)
    }

    override fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard) {
        if (game.availableCards.isEmpty()) {
            return
        }

        chooseCardActionCard.onCardChosen(this, game.availableCards.maxBy { getBuyCardScore(it) }!!)
    }

    override fun chooseCardAction(text: String, chooseCardActionCard: ChooseCardActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        if (cardsToSelectFrom.isEmpty()) {
            return
        }

        val card = chooseCardActionCard as Card

        val chosenCard: Card = when (card.name) {
            Bandit.NAME -> cardsToSelectFrom.minBy { getBuyCardScore(it) }!!
            Contraband.NAME -> when {
                cardsToSelectFrom.any { it.isPlatinum } && turns > 5 && game.currentPlayer.cardCountByName(Platinum.NAME) == 0 -> Platinum()
                cardsToSelectFrom.any { it.isColony } && turns > 8 -> Colony()
                cardsToSelectFrom.any { it.isProvince } && turns > 3 && (game.currentPlayer.cardCountByName(Gold.NAME) > 2 || turns > 10) -> Province()
                cardsToSelectFrom.any { it.isDuchy } && turns > 10 -> Duchy()
                else -> Gold()
            }
            Lookout.NAME -> cardsToSelectFrom.minBy { getDiscardCardScore(it) }!!
            PirateShip.NAME -> cardsToSelectFrom.maxBy { getBuyCardScore(it) }!!
            Smugglers.NAME -> cardsToSelectFrom.maxBy { getBuyCardScore(it) }!!
            WishingWell.NAME -> deck.maxBy { cardCountByName(it.name) }!!
            else -> cardsToSelectFrom.first()
        }

        chooseCardActionCard.onCardChosen(this, chosenCard, info)
    }
}
