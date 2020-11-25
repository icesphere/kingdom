package com.kingdom.model.cards.adventures.events

import com.kingdom.model.players.Player

class TravellingFair : AdventuresEvent(NAME, 2) {

    init {
        addBuys = 2
        special = "When you gain a card this turn, you may put it onto your deck."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.numCardGainedMayPutOnTopOfDeck++
    }

    companion object {
        const val NAME: String = "Travelling Fair"
    }
}