package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.BeforeCardGainedListenerForSelf
import com.kingdom.model.players.Player

class NomadCamp : HinterlandsCard(NAME, CardType.Action, 4), BeforeCardGainedListenerForSelf {

    init {
        addBuys = 1
        addCoins = 2
        special = "This is gained onto your deck (instead of to your discard pile)."
        fontSize = 11
    }

    override fun beforeCardGained(player: Player) {
        player.isNextCardToTopOfDeck = true
    }

    companion object {
        const val NAME: String = "Nomad Camp"
    }
}

