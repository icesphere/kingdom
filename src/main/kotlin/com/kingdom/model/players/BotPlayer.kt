package com.kingdom.model.players

import com.kingdom.model.Choice
import com.kingdom.model.Game
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.*
import com.kingdom.model.cards.adventures.Disciple
import com.kingdom.model.cards.adventures.Messenger
import com.kingdom.model.cards.adventures.Miser
import com.kingdom.model.cards.cornucopia.Hamlet
import com.kingdom.model.cards.cornucopia.HorseTraders
import com.kingdom.model.cards.cornucopia.Jester
import com.kingdom.model.cards.cornucopia.Remake
import com.kingdom.model.cards.darkages.*
import com.kingdom.model.cards.darkages.ruins.Survivors
import com.kingdom.model.cards.darkages.shelters.Hovel
import com.kingdom.model.cards.guilds.*
import com.kingdom.model.cards.hinterlands.*
import com.kingdom.model.cards.intrigue.*
import com.kingdom.model.cards.kingdom.*
import com.kingdom.model.cards.prosperity.*
import com.kingdom.model.cards.seaside.*
import com.kingdom.model.cards.supply.*
import java.util.*

abstract class BotPlayer(user: User, game: Game) : Player(user, game) {

    private val random = Random()

    var isWaitingForPlayers = false

    abstract val difficulty: Int

    open val onlyBuyVictoryCards: Boolean = false

    private val cardsToPlay: List<Card>
        get() {
            val actionCards = hand.filter { it.isAction }.filter { getPlayCardScore(it) >= 0 }
            val treasureCards = hand.filter { it.isTreasure }.filter { getPlayCardScore(it) >= 0 }

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

                if (sortedCards.first().isTreasure && sortedCards.any { !it.isTreasureExcludedFromAutoPlay }) {
                    playAllTreasureCards()
                } else {
                    val card = sortedCards[0]
                    playCard(card)
                }

                while (isWaitingForPlayers) {
                    Thread.sleep(500)
                }
            }

            if (availableCoins > 0 && buys > 0) {
                val cardToBuy = getCardToBuy()
                if (cardToBuy != null) {
                    endTurn = false
                    this.buyCard(game.getNewInstanceOfCard(cardToBuy))
                }
            }
        }

        endTurn()
    }

    val availableCardsToBuy: List<Card>
        get() = game.allCards.filter { canBuyCard(it) && !excludeCard(it) }

    val availableCardsToBuyNames: List<String>
        get() = availableCardsToBuy.map { it.name }

    abstract fun getCardToBuy(): String?

    open fun excludeCard(card: Card): Boolean {
        return card.isCurseOnly
    }

    override fun addCardFromDiscardToTopOfDeck(maxCost: Int?) {
        val card = chooseCardFromDiscardToAddToTopOfDeck()
        if (card != null) {
            discard.remove(card)
            addCardToTopOfDeck(card)
        }
    }

    override fun optionallyTrashCardsFromHand(numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        val cards = getCardsToOptionallyTrashFromHand(numCardsToTrash)

        cards.forEach({ this.trashCardFromHand(it) })
    }

    override fun optionallyTrashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        val cards = getCardsToOptionallyTrashFromHand(numCardsToTrash, cardActionableExpression)

        cards.forEach({ this.trashCardFromHand(it) })

        card.cardsTrashed(this, cards)
    }

    override fun discardCardsFromHand(numCardsToDiscard: Int, optional: Boolean) {
        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, optional)
        cardsToDiscard.forEach({ this.discardCardFromHand(it) })
    }

    override fun optionallyDiscardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, info: Any?) {
        val optionalDiscard = true

        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, optionalDiscard)

        cardsToDiscard.forEach({ this.discardCardFromHand(it) })

        if (!cardsToDiscard.isEmpty()) {
            card.cardsDiscarded(this, cardsToDiscard, info)
        } else {
            card.onChoseDoNotUse(this, info)
        }
    }

    override fun discardCardsForBenefit(card: DiscardCardsForBenefitActionCard, numCardsToDiscard: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo better logic

        val cardsToDiscard = getCardsToDiscard(numCardsToDiscard, false)

        if (!cardsToDiscard.isEmpty()) {
            cardsToDiscard.forEach({ this.discardCardFromHand(it) })
            card.cardsDiscarded(this, cardsToDiscard, null)
        } else {
            card.onChoseDoNotUse(this, null)
        }
    }

    override fun makeChoice(card: ChoiceActionCard, vararg choices: Choice) {
        val choice = getChoice(card, arrayOf(*choices), null)
        card.actionChoiceMade(this, choice, null)
    }

    override fun makeChoice(card: ChoiceActionCard, text: String, vararg choices: Choice) {
        makeChoice(card, *choices)
    }

    override fun makeChoiceWithInfo(card: ChoiceActionCard, text: String, info: Any, vararg choices: Choice) {
        val choice = getChoice(card, arrayOf(*choices), info)
        card.actionChoiceMade(this, choice, info)
    }

    override fun trashCardsFromHand(numCardsToTrash: Int, optional: Boolean, cardActionableExpression: ((card: Card) -> Boolean)?) {
        val card = getCardToTrashFromHand(optional, null, cardActionableExpression)
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
            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the trash")
        }
    }

    override fun chooseSupplyCardToGain(maxCost: Int?, cardActionableExpression: ((card: Card) -> Boolean)?, text: String?) {
        val card = chooseFreeCardToGain(maxCost, cardActionableExpression)
        if (card != null) {
            game.removeCardFromSupply(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

            cardGained(card)
        }
    }

    override fun chooseSupplyCardToGainWithExactCost(cost: Int) {
        val card = chooseFreeCardToGainWithExactCost(cost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

            cardGained(card)
        }
    }

    override fun chooseSupplyCardToGainForBenefit(maxCost: Int?, text: String, freeCardFromSupplyForBenefitActionCard: FreeCardFromSupplyForBenefitActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo logic for different cards
        val card = chooseFreeCardToGain(maxCost, cardActionableExpression)
        if (card != null) {
            game.removeCardFromSupply(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

            cardGained(card)

            freeCardFromSupplyForBenefitActionCard.onCardGained(this, card)
        }
    }

    override fun chooseSupplyCardToGainToTopOfDeck(maxCost: Int?) {
        val card = chooseFreeCardToGain(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            isNextCardToTopOfDeck = true

            cardGained(card)
        }
    }

    override fun chooseSupplyCardToGainToTopOfDeckWithMaxCostAndType(maxCost: Int?, cardType: CardType) {
        val card = chooseFreeCardToGain(maxCost, { c -> c.type == cardType })
        if (card != null) {
            game.removeCardFromSupply(card)

            isNextCardToTopOfDeck = true

            cardGained(card)
        }
    }

    override fun chooseSupplyCardToGainToTopOfDeckWithExactCost(cost: Int) {
        val card = chooseFreeCardToGainWithExactCost(cost)
        if (card != null) {
            game.removeCardFromSupply(card)

            addCardToTopOfDeck(card)
            cardGained(card)
        }
    }

    override fun chooseSupplyCardToGainToHandWithMaxCost(maxCost: Int?) {
        val card = chooseFreeCardToGain(maxCost)
        if (card != null) {
            game.removeCardFromSupply(card)

            gainCardToHand(card)
        }
    }

    private val cardsToBuy: List<Card>
        get() {
            val cardsToBuy = ArrayList<Card>()

            //todo better logic
            if (coffers > 0) {
                useCoffers(coffers)
            }

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
        return when {
            card.isTrashingFromHandRequiredCard -> {
                val cardToTrash = getCardToTrashFromHand(false, { c -> c.id == card.id })
                return when {
                    cardToTrash == null -> -1
                    !card.isTrashingFromHandToUpgradeCard && getBuyCardScore(cardToTrash) > 3 -> -1
                    card.isTrashingFromHandToUpgradeCard && (cardToTrash.isProvince || cardToTrash.isColony) -> -1
                    (card.name == Upgrade.NAME || card.name == Remake.NAME) && cardToTrash.cost > 2 && game.availableCards.none { it.cost == card.cost + 1 } -> 1
                    card.addActions > 0 -> 10
                    else -> card.cost
                }
            }
            card.addActions > 0 -> 10
            else -> card.cost
        }
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

    open fun getChoice(choiceActionCard: ChoiceActionCard, choices: Array<Choice>, info: Any?): Int {
        val card = choiceActionCard as Card

        return when (choiceActionCard.name) {
            Advisor.NAME -> {
                @Suppress("UNCHECKED_CAST")
                val cards = info as List<Card>
                return cards.indexOf(cards.maxBy { if (it.isVictoryOnly) -1 else it.cost })
            }
            Ambassador.NAME -> choices.last().choiceNumber
            Baron.NAME -> 1
            Beggar.NAME -> 1
            Catacombs.NAME -> {
                @Suppress("UNCHECKED_CAST")
                val cards = info as List<Card>
                if (cards.sumBy { getBuyCardScore(it) } >= 9) 1 else 2
            }
            Count.NAME -> {
                val firstChoice = info as Boolean
                if (firstChoice) {
                    if (actions == 0 && hand.any { it.isAction }) 1 else 2
                } else {
                    when {
                        onlyBuyVictoryCards -> 3
                        hand.count { getTrashCardScore(it) > 50 } > hand.size - 2 -> 2
                        else -> 1
                    }
                }
            }
            Counterfeit.NAME -> 1
            CountingHouse.NAME -> choices.last().choiceNumber
            Courtier.NAME -> when {
                actions == 0 && hand.any { it.isAction && it.cost > 3 } -> 1
                buys < 2 && availableCoins > 11 -> 2
                buys > 1 || turns > 10 -> 3
                else -> 4
            }
            Cultist.NAME -> 1
            DeathCart.NAME -> when {
                actions == 0 -> 1
                onlyBuyVictoryCards -> 2
                hand.any { it.isAction && it.cost < 5 } -> 1
                else -> 2
            }
            Doctor.NAME -> 2
            Diplomat.NAME -> if (hand.count { getDiscardCardScore(it) > 50 } > 2) 1 else 2
            Explorer.NAME -> 1
            Graverobber.NAME -> if (game.trashedCards.any { getCardCostWithModifiers(it) in 3..6 }) 1 else 2
            Hamlet.NAME -> {
                val hamlet = card as Hamlet
                when {
                    hand.any { getDiscardCardScore(it) > 90 } -> 1
                    hamlet.discardingCardForAction && hand.any { it.isAction } && hand.any { getDiscardCardScore(it) > 50 } -> 1
                    !hamlet.discardingCardForAction && availableCoins > 11 && buys == 1 -> 1
                    else -> 2
                }
            }
            Herald.NAME -> 2
            HorseTraders.NAME -> 1
            Hovel.NAME -> 1
            HuntingGrounds.NAME -> 1
            IllGottenGains.NAME -> if (availableCoins == 4 || availableCoins == 5 || availableCoins == 7 || (availableCoins == 10 && game.isIncludeColonyCards)) 1 else 2
            Ironmonger.NAME -> {
                val cardToDiscard = info as Card
                if (getDiscardCardScore(cardToDiscard) > 50) 1 else 2
            }
            JackOfAllTrades.NAME -> if (getDiscardCardScore(cardOnTopOfDeck!!) > 50) 1 else 2
            Jester.NAME -> {
                val cardToGain = info as Card
                if (getBuyCardScore(cardToGain) > 2) choices.first().choiceNumber else choices.last().choiceNumber
            }
            Library.NAME -> if (actions > 0 && hand.none { it.isAction }) 1 else 2
            Loan.NAME -> if (card.isCopper) 2 else 1
            Lurker.NAME -> if (game.trashedCards.any { getBuyCardScore(it) > 4 }) 2 else 1
            MarketSquare.NAME -> if (turns < 10 || game.numInPileMap[Province.NAME]!! > 3) 1 else 2
            Masterpiece.NAME -> 2
            Mercenary.NAME -> if (hand.count { getTrashCardScore(it) > 15 } > 2) 1 else 2
            Messenger.NAME -> if (turns < 10) 1 else 2
            Mill.NAME -> if (hand.count { getDiscardCardScore(it) > 50 } > 1) 1 else 2
            MiningVillage.NAME -> when {
                game.availableCards.any { it.isColony } && availableCoins < 11 && availableCoins > 8 -> 1
                turns > 7 && game.availableCards.any { it.isProvince } && availableCoins < 8 && availableCoins > 5 -> 1
                else -> 2
            }
            Minion.NAME -> if (hand.size < 4 || hand.count { getDiscardCardScore(it) > 50 } > 2) 2 else 1
            Miser.NAME -> if (turns < 10 || tavernCards.count { it.isCopper } == 0) 1 else 2
            Moat.NAME -> 1
            Moneylender.NAME -> 1
            Mountebank.NAME -> 1
            NativeVillage.NAME -> if (nativeVillageCards.size < 2) 1 else 2
            NobleBrigand.NAME -> 2
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
            Plaza.NAME -> if (hand.any { it.isCopper }) 1 else 2
            Scavenger.NAME -> 1
            Sentry.NAME -> when (card.cost) {
                0 -> 1
                1 -> 2
                2 -> 2
                else -> 3
            }
            SpiceMerchant.NAME -> when {
                choices.first().choiceNumber == 1 -> {
                    if (hand.any { it.isTreasure && it.cost < 6 } && (hand.any { actions == 0 && it.isAction && it.cost > 3 } || (availableCoins > 11 && buys == 1))) 1 else 2
                }
                else -> {
                    if (hand.any { actions == 0 && it.isAction && it.cost > 3 }) 3 else 4
                }
            }
            Squire.NAME -> when {
                actions == 0 && hand.any { it.isAction } -> 1
                buys == 1 && availableCoins > 11 -> 2
                else -> 3
            }
            Stables.NAME -> if (hand.any { (it.isTreasure && it.cost < 6) || (actions == 0 && it.isAction && it.cost > 3) }) 1 else 2
            Steward.NAME -> when {
                turns < 5 && hand.count { it.cost <= 2 } >= 2 -> 3
                actions > 0 -> 1
                else -> 2
            }
            Stonemason.NAME -> 2
            Survivors.NAME -> {
                val cards = mutableListOf(deck[0])
                if (deck.size > 1) {
                    cards.add(deck[1])
                }
                if (cards.all { getDiscardCardScore(it) > 50 }) 1 else 2
            }
            Trader.NAME -> {
                val cardToGain = info as Card
                if (getBuyCardScore(Silver()) > getBuyCardScore(cardToGain)) 1 else 2
            }
            Torturer.NAME -> 1
            Treasury.NAME -> 1
            Urchin.NAME -> if (allCards.count { it is Mercenary } <= 2 ) 1 else 2
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

    private fun getCardToTrashFromHand(optional: Boolean, excludeCardExpression: ((card: Card) -> Boolean)? = null, cardActionableExpression: ((card: Card) -> Boolean)? = null): Card? {

        val cardsAvailableToTrash = hand.filter { (excludeCardExpression == null || !excludeCardExpression(it)) && (cardActionableExpression == null || cardActionableExpression(it)) }

        if (cardsAvailableToTrash.isNotEmpty()) {
            val sortedCards = cardsAvailableToTrash.sortedByDescending { getTrashCardScore(it) }
            val card = sortedCards[0]
            if (optional && getTrashCardScore(card) < 20) {
                return null
            }
            return card
        }
        return null
    }


    private fun getCardsToTrashFromHand(cards: Int, excludeCardExpression: ((card: Card) -> Boolean)? = null, cardActionableExpression: ((card: Card) -> Boolean)? = null): List<Card> {
        val cardsToTrashFromHand = ArrayList<Card>()

        val cardsAvailableToTrash = hand.filter { (excludeCardExpression == null || !excludeCardExpression(it)) && (cardActionableExpression == null || cardActionableExpression(it)) }

        if (cardsAvailableToTrash.isNotEmpty()) {
            val sortedHandCards = cardsAvailableToTrash.sortedByDescending { getTrashCardScore(it) }

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

    private fun getCardsToOptionallyTrashFromHand(cards: Int, cardActionableExpression: ((card: Card) -> Boolean)? = null): List<Card> {
        val cardsToTrashFromHand = ArrayList<Card>()

        val actionableCards = hand.filter { cardActionableExpression == null || cardActionableExpression.invoke(it) }

        if (actionableCards.isNotEmpty()) {
            val sortedHandCards = actionableCards.sortedByDescending { getTrashCardScore(it) }

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

    private fun chooseFreeCardToGain(maxCost: Int?, cardActionableExpression: ((card: Card) -> Boolean)? = null): Card? {
        val cards = game.availableCards
                .filter { c ->
                    (maxCost == null || this.getCardCostWithModifiers(c) <= maxCost)
                            && (cardActionableExpression == null || cardActionableExpression.invoke(c))
                }

        return pickCardBasedOnBuyScore(cards)
    }

    private fun chooseFreeCardToGainWithExactCost(cost: Int): Card? {
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

    override fun trashCardsFromHandForBenefit(card: TrashCardsForBenefitActionCard, numCardsToTrash: Int, text: String, cardActionableExpression: ((card: Card) -> Boolean)?) {
        if (hand.none { cardActionableExpression == null || cardActionableExpression(it) }) {
            return
        }

        val cards = getCardsToTrashFromHand(numCardsToTrash, null, cardActionableExpression)
        cards.forEach { this.trashCardFromHand(it) }
        card.cardsTrashed(this, cards)
    }

    override fun chooseSupplyCardToGainToHandWithMaxCostAndType(maxCost: Int?, cardType: CardType) {
        val card = chooseFreeCardToGain(maxCost, { c -> c.type == cardType })
        if (card != null) {
            game.removeCardFromSupply(card)

            addEventLogWithUsername("gained ${card.cardNameWithBackgroundColor} from the supply")

            cardGained(card)
        }
    }

    override fun yesNoChoice(choiceActionCard: ChoiceActionCard, text: String, info: Any?) {
        val choice = getChoice(choiceActionCard, arrayOf(Choice(1, "Yes"), Choice(2, "No")), info)
        choiceActionCard.actionChoiceMade(this, choice, info)
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
        isWaitingForPlayers = true
    }

    override fun waitForOtherPlayersForResolveAttack(attackCard: Card) {
        isWaitingForPlayers = true
    }

    override fun waitForOtherPlayersToResolveActionsWithResults(resultHandler: ActionResultHandler) {
        isWaitingForPlayers = true
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
                addEventLogWithUsername("put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s hand")
                opponent.gainCardToHand(card)
            }
            CardLocation.Deck -> {
                addEventLogWithUsername("put ${card.cardNameWithBackgroundColor} on top of ${opponent.username}'s deck")
                opponent.gainCardToTopOfDeck(card)
            }
            else -> {
                addEventLogWithUsername("put ${card.cardNameWithBackgroundColor} into ${opponent.username}'s discard")
                opponent.cardGained(card)
            }
        }
    }

    override fun chooseCardFromHand(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        val cards = hand.filter { cardActionableExpression == null || cardActionableExpression(it) }

        if (cards.isEmpty()) {
            return
        }

        val card = chooseCardActionCard as Card

        val chosenCard: Card = choseCard(card, cards)

        chooseCardActionCard.onCardChosen(this, chosenCard)
    }

    private fun choseCard(card: Card, cards: List<Card>): Card {
        return when (card.name) {
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
            ThroneRoom.NAME, KingsCourt.NAME, Procession.NAME, Disciple.NAME -> cards.maxBy { getPlayCardScore(it) }!!
            else -> cards.first()
        }
    }

    override fun chooseCardFromHandOptional(text: String, chooseCardActionCard: ChooseCardActionCardOptional, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo handle optional

        val cards = hand.filter { cardActionableExpression == null || cardActionableExpression(it) }

        if (cards.isEmpty()) {
            return
        }

        val card = chooseCardActionCard as Card

        val chosenCard: Card = choseCard(card, cards)

        chooseCardActionCard.onCardChosen(this, chosenCard)
    }

    override fun chooseCardsFromHand(text: String, numToChoose: Int, optional: Boolean, chooseCardsActionCard: ChooseCardsActionCard, cardActionableExpression: ((card: Card) -> Boolean)?) {
        //todo better logic

        val availableCards = hand.filter { cardActionableExpression == null || cardActionableExpression(it) }

        val cards = if (availableCards.size < numToChoose) {
            availableCards
        } else {
            availableCards.subList(0, numToChoose)
        }

        chooseCardsActionCard.onCardsChosen(this, cards)
    }

    override fun chooseCardFromSupply(text: String, chooseCardActionCard: ChooseCardActionCard, cardActionableExpression: ((card: Card) -> Boolean)?, info: Any?, choosingEmptyPilesAllowed: Boolean) {

        val availableSupplyCards = if (choosingEmptyPilesAllowed) game.allCards else game.availableCards

        val availableCards = availableSupplyCards.filter { cardActionableExpression == null || cardActionableExpression(it) }

        if (availableCards.isEmpty()) {
            return
        }

        chooseCardActionCard.onCardChosen(this, availableCards.maxBy { getBuyCardScore(it) }!!, info)
    }

    override fun chooseCardAction(text: String, chooseCardActionCard: ChooseCardActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        if (cardsToSelectFrom.isEmpty()) {
            return
        }

        val card = chooseCardActionCard as Card

        val chosenCards = getChosenCards(1, cardsToSelectFrom, card)

        chooseCardActionCard.onCardChosen(this, chosenCards.first(), info)
    }

    override fun chooseCardsAction(numCardsToChoose: Int, text: String, chooseCardsActionCard: ChooseCardsActionCard, cardsToSelectFrom: List<Card>, optional: Boolean, info: Any?) {
        if (cardsToSelectFrom.isEmpty()) {
            return
        }

        val card = chooseCardsActionCard as Card

        val numCards = if (numCardsToChoose <= cardsToSelectFrom.size) numCardsToChoose else cardsToSelectFrom.size

        val chosenCards = getChosenCards(numCards, cardsToSelectFrom, card)

        chooseCardsActionCard.onCardsChosen(this, chosenCards, info)
    }

    private fun getChosenCards(numCardsToChoose: Int, cardsToSelectFrom: List<Card>, card: Card): MutableList<Card> {

        val chosenCards = mutableListOf<Card>()

        repeat(numCardsToChoose) {
            //todo handle optional

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

            chosenCards.add(chosenCard)
        }

        return chosenCards
    }
}
