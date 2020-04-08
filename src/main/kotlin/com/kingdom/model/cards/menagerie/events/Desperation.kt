package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.Player

class Desperation : MenagerieEvent(NAME, 0, true) {

    init {
        special = "Once per turn: You may gain a Curse. If you do, +1 Buy and +\$2."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.game.isCardAvailableInSupply(Curse())
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Curse(), true)
        player.addBuys(1)
        player.addCoins(2)
    }

    companion object {
        const val NAME: String = "Desperation"
    }
}