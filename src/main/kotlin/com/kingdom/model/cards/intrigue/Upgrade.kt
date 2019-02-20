package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Upgrade : IntrigueCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 1
        special = "Trash a card from your hand. Gain a card costing exactly \$1 more than it."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.trashCardsFromHandForBenefit(this, 1, "")
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 1 }) {
            player.chooseSupplyCardToGainWithExactCost(player.getCardCostWithModifiers(card) + 1)
        }
    }

    companion object {
        const val NAME: String = "Upgrade"
    }
}

