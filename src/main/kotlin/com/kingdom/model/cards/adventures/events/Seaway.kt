package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.FreeCardFromSupplyForBenefitActionCard
import com.kingdom.model.players.Player

class Seaway : AdventuresEvent(NAME, 5), FreeCardFromSupplyForBenefitActionCard {

    init {
        special = "Gain an Action card costing up to \$4. Move your +1 Buy token to its pile. (When you play a card from that pile, you first get +1 Buy.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainForBenefitWithMaxCost(4, special, this, { c -> c.isAction })
    }

    override fun onCardGained(player: Player, card: Card) {
        player.plusBuyTokenSupplyPile = card.name
        player.addEventLogWithUsername("moved +1 Buy token to the ${card.pileName} pile")
    }

    companion object {
        const val NAME: String = "Seaway"
    }
}