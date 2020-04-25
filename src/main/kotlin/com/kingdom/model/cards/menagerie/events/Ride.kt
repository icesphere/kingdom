package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.menagerie.UsesHorses
import com.kingdom.model.players.Player

class Ride : MenagerieEvent(NAME, 2), UsesHorses {

    init {
        special = "Gain a Horse."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainHorse()
    }

    companion object {
        const val NAME: String = "Ride"
    }
}