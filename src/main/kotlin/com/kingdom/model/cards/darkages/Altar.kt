package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Altar : DarkAgesCard(NAME, CardType.Action, 6), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$5."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        player.chooseSupplyCardToGain(5)
    }

    companion object {
        const val NAME: String = "Altar"
    }
}

