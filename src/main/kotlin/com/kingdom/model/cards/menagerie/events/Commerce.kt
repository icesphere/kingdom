package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Commerce : MenagerieEvent(NAME, 5) {

    init {
        special = "Gain a Gold per differently named card you've gained this turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numDifferentCards = player.cardsGained.groupBy { it.name }.size
        repeat(numDifferentCards) {
            player.gainSupplyCard(Gold(), true)
        }
    }

    companion object {
        const val NAME: String = "Commerce"
    }
}