package com.kingdom.model.cards.adventures.events

import com.kingdom.model.players.Player

class Expedition : AdventuresEvent(NAME, 3) {

    init {
        special = "Draw 2 extra cards for your next hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.numExtraCardsToDrawAtEndOfTurn += 2
    }

    companion object {
        const val NAME: String = "Expedition"
    }
}