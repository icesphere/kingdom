package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Raid : AdventuresEvent(NAME, 5) {

    init {
        special = "Gain a Silver per Silver you have in play. Each other player puts their -1 Card token on their deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        repeat(player.inPlay.count { it.isSilver }) {
            player.gainSupplyCard(Silver(), true)
        }

        player.opponentsInOrder.forEach { it.isMinusCardTokenOnDeck = true }
    }

    companion object {
        const val NAME: String = "Raid"
    }
}