package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class BorderVillage : HinterlandsCard(NAME, CardType.Action, 6), AfterCardGainedListenerForSelf {

    init {
        addCards = 1
        addActions = 2
        special = "When you gain this, gain a cheaper card."
        fontSize = 10
    }

    override fun afterCardGained(player: Player) {
        player.acquireFreeCard(player.getCardCostWithModifiers(this) - 1)
    }

    companion object {
        const val NAME: String = "Border Village"
    }
}

