package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Remodel : KingdomCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {
    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        if (scrappedCards.isNotEmpty()) {
            player.acquireFreeCard(scrappedCards.get(0).cost + 2)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than the trashed card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    companion object {
        const val NAME: String = "Remodel"
    }
}

