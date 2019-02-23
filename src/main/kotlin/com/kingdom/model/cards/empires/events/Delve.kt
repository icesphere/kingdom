package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Delve : EmpiresEvent(NAME, 2) {

    init {
        addBuys = 1
        special = "Gain a Silver."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Silver(), true)
    }

    companion object {
        const val NAME: String = "Delve"
    }
}