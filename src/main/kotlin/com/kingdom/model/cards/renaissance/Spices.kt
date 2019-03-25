package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Spices : RenaissanceCard(NAME, CardType.Treasure, 5), AfterCardGainedListenerForSelf {

    init {
        addCoins = 2
        addBuys = 1
        special = "When you gain this, +2 Coffers."
    }

    override fun afterCardGained(player: Player) {
        player.addCoffers(2)
        player.addEventLogWithUsername("gained +2 Coffers from gaining $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Spices"
    }
}