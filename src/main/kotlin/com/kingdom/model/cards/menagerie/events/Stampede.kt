package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.menagerie.UsesHorses
import com.kingdom.model.players.Player

class Stampede : MenagerieEvent(NAME, 5), UsesHorses {

    init {
        special = "If you have 5 or fewer cards in play, gain 5 Horses onto your deck."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.inPlay.size <= 5
    }

    override fun cardPlayedSpecialAction(player: Player) {
        repeat(5) {
            player.isNextCardToTopOfDeck = true
            player.gainHorse(false)
        }
    }

    companion object {
        const val NAME: String = "Stampede"
    }
}