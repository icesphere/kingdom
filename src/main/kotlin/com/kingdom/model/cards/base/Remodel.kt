package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Remodel : BaseCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than the trashed card."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }
    
    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.chooseSupplyCardToGain(player.getCardCostWithModifiers(trashedCards[0]) + 2)
        }
    }

    companion object {
        const val NAME: String = "Remodel"
    }
}

