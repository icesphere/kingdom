package com.kingdom.model.cards.adventures.events

import com.kingdom.model.players.Player

class Alms : AdventuresEvent(NAME, 0, true) {

    init {
        special = "Once per turn: If you have no Treasures in play, gain a card costing up to \$4."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.inPlay.none { it.isTreasure }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(4)
    }

    companion object {
        const val NAME: String = "Alms"
    }
}