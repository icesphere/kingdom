package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Port : AdventuresCard(NAME, CardType.Action, 4), AfterCardBoughtListenerForSelf {

    init {
        addCards = 1
        addActions = 2
        special = "When you buy this, gain another Port."
    }

    override fun afterCardBought(player: Player) {
        player.gainSupplyCard(Port(), true)
    }

    companion object {
        const val NAME: String = "Port"
    }
}

