package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.Player

class Alliance : MenagerieEvent(NAME, 10) {

    init {
        special = "Gain a Province, a Duchy, an Estate, a Gold, a Silver, and a Copper."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Province(), true)
        player.gainSupplyCard(Duchy(), true)
        player.gainSupplyCard(Estate(), true)
        player.gainSupplyCard(Gold(), true)
        player.gainSupplyCard(Silver(), true)
        player.gainSupplyCard(Copper(), true)
    }

    companion object {
        const val NAME: String = "Alliance"
    }
}