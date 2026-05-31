package com.kingdom.model.cards.risingsun

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.actions.CardRepeater
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.cards.actions.ChooseCardActionCardOptional
import com.kingdom.model.cards.actions.ChooseCardsActionCard
import com.kingdom.model.cards.actions.handleCardToRepeatChosen
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Amass : RisingSunEvent(NAME, 2) {
    init {
        special = "If you have no Action cards in play, gain an Action card costing up to \$5."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.inPlayWithDuration.none { player.isActionForCurrentGame(it) }) {
            player.chooseSupplyCardToGainWithMaxCost(5, { player.isActionForCurrentGame(it) }, "Gain an Action card costing up to \$5")
        }
    }

    companion object {
        const val NAME: String = "Amass"
    }
}

class Asceticism : RisingSunEvent(NAME, 2), ChooseCardsActionCard {
    init {
        special = "Pay any amount of \$ to trash that many cards from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val maxToTrash = minOf(player.availableCoins, player.hand.size)
        if (maxToTrash > 0) {
            player.chooseCardsFromHand("Pay up to \$$maxToTrash to trash that many cards from your hand", maxToTrash, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        player.addCoins(-cards.size)
        cards.forEach { player.trashCardFromHand(it) }
    }

    companion object {
        const val NAME: String = "Asceticism"
    }
}

class Continue : RisingSunEvent(NAME, 0, debtCost = 8, oncePerTurnEvent = true), ChooseCardActionCard {
    init {
        special = "Once per turn: Gain a non-Attack Action card costing up to \$4. Return to your Action phase and play it. +1 Action and +1 Buy."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Gain a non-Attack Action card costing up to \$4", this, { card ->
            player.isActionForCurrentGame(card) && !card.isAttack && card.debtCost == 0 && player.getCardCostWithModifiers(card) <= 4
        }, choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.gainSupplyCard(card, true, CardLocation.Hand)
        player.returnToActionPhaseIfBuyPhase()
        player.addActions(1)
        player.addBuys(1)
        player.hand.firstOrNull { it.id == card.id || it.name == card.name }?.let { player.playCard(it) }
    }

    companion object {
        const val NAME: String = "Continue"
    }
}

class Credit : RisingSunEvent(NAME, 2), ChooseCardActionCard {
    init {
        special = "Gain an Action or Treasure costing up to \$8. Take debt equal to its cost."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Gain an Action or Treasure costing up to \$8", this, { card ->
            (player.isActionForCurrentGame(card) || card.isTreasure) && card.debtCost == 0 && player.getCardCostWithModifiers(card) <= 8
        }, choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        val cost = player.getCardCostWithModifiers(card)
        player.gainSupplyCard(card, true)
        player.addDebt(cost)
    }

    companion object {
        const val NAME: String = "Credit"
    }
}

class Foresight : RisingSunEvent(NAME, 2) {
    init {
        special = "Reveal cards from your deck until revealing an Action. Set it aside and discard the rest. Put it into your hand at end of turn."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val revealed = mutableListOf<Card>()
        while (true) {
            val card = player.removeTopCardOfDeck() ?: break
            revealed.add(card)
            if (player.isActionForCurrentGame(card)) {
                break
            }
        }

        if (revealed.isEmpty()) {
            return
        }

        player.addEventLogWithUsername("revealed ${revealed.groupedString}")
        val action = revealed.firstOrNull { player.isActionForCurrentGame(it) }
        val rest = revealed.filter { it.id != action?.id }
        if (rest.isNotEmpty()) {
            player.addCardsToDiscard(rest, true)
        }
        if (action != null) {
            player.cardsToPutIntoHandAfterDrawingCardsAtEndOfTurn.add(action)
            player.cardRemovedFromPlay(action, CardLocation.SetAside)
        }
    }

    companion object {
        const val NAME: String = "Foresight"
    }
}

class Gather : RisingSunEvent(NAME, 7), ChooseCardActionCard {
    private val remainingCosts = mutableListOf<Int>()

    init {
        special = "Gain a card costing exactly \$3, a card costing exactly \$4, and a card costing exactly \$5."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        remainingCosts.clear()
        remainingCosts.addAll(listOf(3, 4, 5))
        gainNext(player)
    }

    private fun gainNext(player: Player) {
        if (remainingCosts.isEmpty()) {
            return
        }

        val cost = remainingCosts.removeAt(0)
        val canGain = player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) == cost }
        if (!canGain) {
            gainNext(player)
            return
        }

        player.chooseCardFromSupply("Gain a card costing exactly \$$cost", this, { card ->
            card.debtCost == 0 && player.getCardCostWithModifiers(card) == cost
        }, choosingEmptyPilesAllowed = false)
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.gainSupplyCard(card, true)
        gainNext(player)
    }

    companion object {
        const val NAME: String = "Gather"
    }
}

class Kintsugi : RisingSunEvent(NAME, 3), ChooseCardsActionCard {
    init {
        special = "Trash a card from your hand. If you've gained a Gold this game, gain a card costing up to \$2 more than the trashed card."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash a card from your hand", 1, false, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        if (cards.isEmpty()) {
            return
        }

        val trashed = cards.first()
        val maxCost = player.getCardCostWithModifiers(trashed) + 2
        player.trashCardFromHand(trashed)
        if (player.hasGainedGold) {
            player.chooseSupplyCardToGainWithMaxCost(maxCost)
        }
    }

    companion object {
        const val NAME: String = "Kintsugi"
    }
}

class Practice : RisingSunEvent(NAME, 3), ChooseCardActionCardOptional, CardRepeater {
    override var cardBeingRepeated: Card? = null
    override val timesRepeated: Int = 1

    init {
        special = "You may play an Action card from your hand twice."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { player.isActionForCurrentGame(it) }) {
            player.chooseCardFromHandOptional("You may play an Action card from your hand twice", this) { player.isActionForCurrentGame(it) }
        }
    }

    override fun onCardChosen(player: Player, card: Card?, info: Any?) {
        handleCardToRepeatChosen(card, player)
    }

    companion object {
        const val NAME: String = "Practice"
    }
}

class ReceiveTribute : RisingSunEvent(NAME, 5), ChooseCardsActionCard {
    init {
        special = "If you've gained at least 3 cards this turn, gain up to 3 differently named Action cards you don't have copies of in play."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.cardsGained.size < 3) {
            return
        }

        val inPlayNames = player.inPlayWithDuration.map { it.name }.toSet()
        val cards = player.game.availableCards
                .filter { player.isActionForCurrentGame(it) && !inPlayNames.contains(it.name) }
                .distinctBy { it.name }
        if (cards.isNotEmpty()) {
            player.chooseCardsAction(minOf(3, cards.size), "Gain up to 3 differently named Action cards you don't have copies of in play", this, cards, true)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.distinctBy { it.name }.take(3).forEach { player.gainSupplyCard(it, true) }
    }

    companion object {
        const val NAME: String = "Receive Tribute"
    }
}

class SeaTrade : RisingSunEvent(NAME, 4), ChooseCardsActionCard {
    init {
        special = "+1 Card per Action card you have in play. Trash up to that many cards from your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val actionCount = player.inPlayWithDuration.count { player.isActionForCurrentGame(it) }
        player.drawCards(actionCount)
        if (actionCount > 0 && player.hand.isNotEmpty()) {
            player.chooseCardsFromHand("Trash up to $actionCount cards from your hand", actionCount, true, this)
        }
    }

    override fun onCardsChosen(player: Player, cards: List<Card>, info: Any?) {
        cards.forEach { player.trashCardFromHand(it) }
    }

    companion object {
        const val NAME: String = "Sea Trade"
    }
}
