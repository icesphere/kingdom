package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Mine : KingdomCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        special = "You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to \$3 more than it."
        fontSize = 13
        textSize = 99
    }

    override val isTrashingCard: Boolean = true

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        if (scrappedCards.isNotEmpty()) {
            player.acquireFreeCardOfTypeToHand(scrappedCards.get(0).cost + 3, CardType.Treasure)
        }
    }

    override fun isCardApplicable(card: Card): Boolean {
        return card.isTreasure
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.any { isCardApplicable(it) }) {
            player.optionallyTrashCardsFromHandForBenefit(this, 1, special)
        }
    }

    companion object {
        const val NAME: String = "Mine"
    }
}

