package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class LostCity : AdventuresCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf {

    init {
        addCards = 2
        addActions = 2
        special = "When you gain this, each other player draws a card."
    }

    override fun afterCardGained(player: Player) {
        for (opponent in player.opponentsInOrder) {
            opponent.drawCard()
        }
    }

    companion object {
        const val NAME: String = "Lost City"
    }
}

