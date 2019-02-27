package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Advance : EmpiresEvent(NAME, 0), TrashCardsForBenefitActionCard {

    init {
        special = "You may trash an Action card from your hand. If you do, gain an Action card costing up to \$6."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.any { it.isAction }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special, { c -> c.isAction })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        player.chooseSupplyCardToGainWithMaxCost(6, { c -> c.isAction }, "Gain an Action card costing up to \$6")
    }

    companion object {
        const val NAME: String = "Advance"
    }
}