package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Lackeys : RenaissanceCard(NAME, CardType.Action, 2), AfterCardGainedListenerForSelf {

    init {
        addCards = 2
        special = "When you gain this, +2 Villagers."
    }

    override fun afterCardGained(player: Player) {
        player.addVillagers(2)
    }

    companion object {
        const val NAME: String = "Lackeys"
    }
}