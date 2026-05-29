package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Wheelwright : HinterlandsCard(NAME, CardType.Action, 5), DiscardCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "You may discard a card to gain an Action card costing as much as it or less."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyDiscardCardsForBenefit(this, 1, "Discard a card to gain an Action card costing as much as it or less")
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        if (discardedCards.isEmpty()) {
            return
        }

        val maxCost = player.getCardCostWithModifiers(discardedCards.first())
        if (player.game.availableCards.any { it.isAction && it.debtCost == 0 && player.getCardCostWithModifiers(it) <= maxCost }) {
            player.chooseSupplyCardToGainWithMaxCost(maxCost, { it.isAction }, "Gain an Action card costing up to \$$maxCost")
        }
    }

    companion object {
        const val NAME: String = "Wheelwright"
    }
}
