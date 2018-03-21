package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Estate
import java.util.*

abstract class BotPlayer(user: User, game: Game) : Player(user, game) {

    private val random = Random()

    override fun takeTurn() {
        var endTurn = false

        while (!endTurn) {
            endTurn = true

            if (!hand.isEmpty()) {
                endTurn = false

                while (!hand.isEmpty()) {
                    val sortedCards = hand.sortedByDescending { getPlayCardScore(it) }

                    if (sortedCards.isEmpty()) {
                        break
                    }

                    val card = sortedCards[0]
                    playCard(card)
                }
            }

            refreshGamePageForOpponent()

            if (trade > 0) {
                val cardsToBuy = cardsToBuy
                if (!cardsToBuy.isEmpty()) {
                    endTurn = false
                    for (card in cardsToBuy) {
                        this.buyCard(card)
                    }
                }
            }

            if (!endTurn) {
                refreshGamePageForOpponent()
            }
        }
    }

    override fun addCardAction(card: CardActionCard, text: String) {
        val result = ActionResult()
        //todo
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

    override fun discardCardsFromHand(cards: Int) {
        val cardsToDiscard = getCardsToDiscard(cards, false)
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

    override fun trashCardFromHand(optional: Boolean) {
        val card = getCardToTrashFromHand(optional)
        if (card != null) {
            trashCardFromHand(card)
        }
    }

    override fun acquireFreeCard(maxCost: Int?) {
        val card = chooseFreeCardToAcquire(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.name)

            cardAcquired(card)
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

    val cardsToBuy: List<Card>
        get() {
            val cardsToBuy = ArrayList<Card>()

            val cards = ArrayList(game.nonEmptySupplyCards)

            val cardsAvailableToBuy = cards.filter { c -> trade >= this.getCardCostWithModifiers(c) }

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

                if (sortedCards.size > 2) {
                    val cardToBuyScoreMap = HashMap<Card, Int>()

                    for (card in cards) {
                        if (!cardToBuyScoreMap.containsKey(card)) {
                            cardToBuyScoreMap.put(card, getBuyCardScore(card))
                        }
                    }

                    val twoCardsList = ArrayList<List<Card>>()

                    for (i in 1..sortedCards.size - 1 - 1) {
                        val cardToCompareAgainst = sortedCards[i]
                        for (j in i + 1..sortedCards.size - 1) {
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
        if (this.getCardCostWithModifiers(card1) + this.getCardCostWithModifiers(card2) <= trade) {
            val cards = ArrayList<Card>(2)
            cards.add(card1)
            cards.add(card2)
            twoCardList.add(cards)
            return true
        }
        return false
    }

    open fun getBuyCardScore(card: Card): Int {
        return card.cost
    }

    fun getPlayCardScore(card: Card): Int {
        //todo
        return 20
    }

    fun getCardToTopOfDeckScore(card: Card): Int {
        return getBuyCardScore(card)
    }

    fun getDiscardCardScore(card: Card): Int {
        //todo
        if (card.isVictory) {
            return 100
        } else if (card.isCopper) {
            return 50
        }
        return 20 - card.cost
    }

    fun getTrashCardScore(card: Card): Int {
        //todo
        if (card is Estate) {
            return 100
        } else if (card is Copper) {
            return 90
        }

        return 20 - card.cost
    }

    fun getReturnCardToTopOfDeckScore(card: Card): Int {
        return 1000 - getBuyCardScore(card)
    }

    open fun getChoice(card: ChoiceActionCard, choices: Array<Choice>): Int {
        //todo
        return 1
    }

    fun getCardsToDiscard(cards: Int, optional: Boolean): List<Card> {
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

    fun getCardToTrashFromHand(optional: Boolean): Card? {
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


    fun getCardsToTrashFromHand(cards: Int): List<Card> {
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

    fun getCardsToOptionallyTrashFromHand(cards: Int): List<Card> {
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

    fun getBuyScoreIncrease(extraTrade: Int): Int {
        var cardToBuyScore = 0

        val cardsToBuy = cardsToBuy
        if (!cardsToBuy.isEmpty()) {
            for (cardToBuy in cardsToBuy) {
                cardToBuyScore += getBuyCardScore(cardToBuy)
            }
        }

        val sortedCards = game.nonEmptySupplyCards.filter { c -> trade + extraTrade >= this.getCardCostWithModifiers(c) }.sortedByDescending { getBuyCardScore(it) }
        if (!sortedCards.isEmpty()) {
            val bestCardScore = getBuyCardScore(sortedCards[0])
            return bestCardScore - cardToBuyScore
        }

        return 0
    }

    fun getHighestBuyScoreForTrade(trade: Int): Int {
        val sortedCards = game.nonEmptySupplyCards.filter { c -> trade >= this.getCardCostWithModifiers(c) }.sortedByDescending { getBuyCardScore(it) }
        if (!sortedCards.isEmpty()) {
            return getBuyCardScore(sortedCards[0])
        }

        return 0
    }

    fun chooseCardFromDiscardToAddToTopOfDeck(): Card? {
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

    fun chooseFreeCardToAcquire(maxCost: Int?, cardType: CardType? = null): Card? {
        val cardsToChooseFrom = ArrayList(game.nonEmptySupplyCards)

        val cards = cardsToChooseFrom
                .filter { c ->
                    (maxCost == null || this.getCardCostWithModifiers(c) <= maxCost)
                    && (cardType == null || cardType == c.type)
                }

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

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String) {
        val cards = getCardsToTrashFromHand(numCardsToTrash)
        cards.forEach { this.trashCardFromHand(it) }
        card.cardsScrapped(this, cards)
    }

    override fun acquireFreeCardOfTypeToHand(maxCost: Int?, cardType: CardType) {
        val card = chooseFreeCardToAcquire(maxCost, cardType)
        if (card != null) {
            game.removeCardFromSupply(card)

            addGameLog(username + " acquired a free card from the supply: " + card.name)

            cardAcquired(card)
        }
    }

    override fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String) {
        //todo
        choiceActionCard.actionChoiceMade(this, 1)
    }

    override fun addCardFromHandToTopOfDeck() {
        //todo
        if (hand.isNotEmpty()) {
            val firstCard = this.hand.first()
            this.hand.remove(firstCard)
            addCardToTopOfDeck(firstCard, false)
        }
    }

    override fun waitForOtherPlayersToResolveActions() {
        //do nothing
    }

    fun refreshGamePageForOpponent() {
        sendGameMessageToOpponent("refresh_game_page")
    }

    fun sendGameMessageToOpponent(message: String) {
        //todo
        //sendGameMessage(opponent.playerName, message)
    }

    fun sendGameMessage(recipient: String, message: String) {
        //todo
    }
}
