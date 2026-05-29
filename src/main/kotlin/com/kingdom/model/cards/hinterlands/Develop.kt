package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Develop : HinterlandsCard(NAME, CardType.Action, 3), TrashCardsForBenefitActionCard, ChoiceActionCard {

    init {
        special = "Trash a card from your hand. Gain two cards onto your deck, with one costing exactly \$1 more than it, and one costing exactly \$1 less than it."
        fontSize = 9
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val trashedCard = trashedCards.first()
        val trashedCost = player.getCardCostWithModifiers(trashedCard)
        val lowerCost = trashedCost - 1
        val higherCost = trashedCost + 1

        val canGainLower = lowerCost >= 0 && player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) == lowerCost }
        val canGainHigher = player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) == higherCost }

        when {
            canGainLower && canGainHigher -> player.makeChoiceWithInfo(this,
                    "Gain the cards for ${cardNameWithBackgroundColor} in which order?",
                    lowerCost to higherCost,
                    Choice(1, "\$$lowerCost then \$$higherCost"),
                    Choice(2, "\$$higherCost then \$$lowerCost"))
            canGainLower -> gainCardOntoDeck(player, lowerCost)
            canGainHigher -> gainCardOntoDeck(player, higherCost)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        @Suppress("UNCHECKED_CAST")
        val costs = info as Pair<Int, Int>
        val orderedCosts = if (choice == 1) listOf(costs.first, costs.second) else listOf(costs.second, costs.first)
        orderedCosts.forEach { gainCardOntoDeck(player, it) }
    }

    private fun gainCardOntoDeck(player: Player, cost: Int) {
        if (player.game.availableCards.any { it.debtCost == 0 && player.getCardCostWithModifiers(it) == cost }) {
            player.chooseSupplyCardToGainWithExactCost(cost, "Gain a card costing exactly \$$cost onto your deck", CardLocation.Deck)
        }
    }

    companion object {
        const val NAME: String = "Develop"
    }
}
