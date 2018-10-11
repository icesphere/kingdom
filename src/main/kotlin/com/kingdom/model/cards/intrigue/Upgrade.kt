package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Upgrade : IntrigueCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        testing = true
        addCards = 1
        addActions = 1
        special = "Trash a card from your hand. Gain a card costing exactly \$1 more than it."
    }

    override val isTrashingCard: Boolean = true

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, "")
        }
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()

        if (player.game.availableCards.any { it.cost == card.cost - 1 }) {
            player.acquireFreeCardWithCost(card.cost + 1)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Upgrade"
    }
}
