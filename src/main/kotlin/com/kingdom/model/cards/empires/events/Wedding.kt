package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Wedding : EmpiresEvent(NAME, 4 ,3) {

    init {
        addVictoryCoins = 1
        special = "Gain a Gold."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Gold(), true)
    }

    companion object {
        const val NAME: String = "Wedding"
    }
}