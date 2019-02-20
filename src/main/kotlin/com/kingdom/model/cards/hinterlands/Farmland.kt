package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Farmland : HinterlandsCard(NAME, CardType.Victory, 6), AfterCardBoughtListenerForSelf, TrashCardsForBenefitActionCard {

    init {
        victoryPoints = 2
        special = "When you buy this, trash a card from your hand and gain a card costing exactly \$2 more than it."
    }

    override fun afterCardBought(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand, then gain a card costing exactly \$2 more than it.")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 2 }) {
            player.chooseSupplyCardToGainWithExactCost(player.getCardCostWithModifiers(card) + 2)
        }
    }

    companion object {
        const val NAME: String = "Farmland"
    }
}

