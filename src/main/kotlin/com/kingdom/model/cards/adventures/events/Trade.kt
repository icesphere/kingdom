package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Trade : AdventuresEvent(NAME, 5), TrashCardsForBenefitActionCard {

    init {
        special = "Trash up to 2 cards from your hand. Gain a Silver per card you trashed."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 2, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        repeat(trashedCards.size) {
            player.gainSupplyCard(Silver(), true)
        }
    }

    companion object {
        const val NAME: String = "Trade"
    }
}