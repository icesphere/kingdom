package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Farmland : HinterlandsCard(NAME, CardType.Victory, 6), AfterCardBoughtListenerForSelf, TrashCardsForBenefitActionCard {

    init {
        testing = true
        victoryPoints = 2
        special = "When you buy this, trash a card from your hand and gain a card costing exactly \$2 more than it."
    }

    override fun afterCardBought(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand, then gain a card costing exactly \$2 more than it.")
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 2 }) {
            player.acquireFreeCardWithCost(player.getCardCostWithModifiers(card) + 2)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Farmland"
    }
}

