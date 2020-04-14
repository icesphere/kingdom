package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.players.Player

class Populate : MenagerieEvent(NAME, 10) {

    init {
        special = "Gain one card from each Action Supply pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.game.availableCards
                .filter { it.isAction }
                .forEach {
                    player.gainSupplyCard(it, true)
                }
    }

    companion object {
        const val NAME: String = "Populate"
    }
}