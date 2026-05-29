package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class Nomads : HinterlandsCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        addBuys = 1
        addCoins = 2
        special = "When you gain or trash this, +\$2."
    }

    override fun afterCardGained(player: Player) {
        player.addCoins(2)
    }

    override fun afterCardTrashed(player: Player) {
        player.addCoins(2)
    }

    companion object {
        const val NAME: String = "Nomads"
    }
}
