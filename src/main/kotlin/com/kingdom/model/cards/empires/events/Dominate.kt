package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Province
import com.kingdom.model.players.Player

class Dominate : EmpiresEvent(NAME, 14) {

    init {
        special = "Gain a Province and +9 VP."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.game.isCardAvailableInSupply(Province())
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Province(), true)
        player.addVictoryCoins(9, true)
    }

    companion object {
        const val NAME: String = "Dominate"
    }
}