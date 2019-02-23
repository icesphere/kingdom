package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class Banquet : EmpiresEvent(NAME, 3) {

    init {
        special = "Gain two Coppers and a non-Victory card costing up to \$5."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Copper(), true)
        player.gainSupplyCard(Copper(), true)
        player.chooseSupplyCardToGain(5, { c -> !c.isVictory })
    }

    companion object {
        const val NAME: String = "Banquet"
    }
}