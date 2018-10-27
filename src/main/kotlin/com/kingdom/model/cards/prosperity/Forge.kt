package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Forge : ProsperityCard(NAME, CardType.Action, 7), TrashCardsForBenefitActionCard {

    init {
        isTrashingCard = true
        special = "Trash any number of cards from your hand. Gain a card with cost exactly equal to the total cost in \$ of the trashed cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, player.hand.size, special)
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        var totalCost = 0

        scrappedCards.forEach { totalCost += player.getCardCostWithModifiers(it) }

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == totalCost }) {
            player.acquireFreeCardWithCost(totalCost)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Forge"
    }
}

