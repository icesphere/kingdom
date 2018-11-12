package com.kingdom.model.cards.darkages.shelters

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.darkages.DarkAgesCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class OvergrownEstate : DarkAgesCard(NAME, CardType.VictoryShelter, 1), AfterCardTrashedListenerForSelf {

    init {
        special = "When you trash this, +1 Card."
    }

    override fun afterCardTrashed(player: Player) {
        player.drawCard()
    }

    companion object {
        const val NAME: String = "Overgrown Estate"
    }
}

