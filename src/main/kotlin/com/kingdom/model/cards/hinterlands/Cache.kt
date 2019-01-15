package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class Cache : HinterlandsCard(NAME, CardType.Treasure, 5), AfterCardGainedListenerForSelf {

    init {
        addCoins = 3
        special = "When you gain this, gain 2 Coppers."
    }

    override fun afterCardGained(player: Player) {
        player.gainSupplyCard(Copper(), showLog = true)
        player.gainSupplyCard(Copper(), showLog = true)
        player.showInfoMessage("You gained 2 ${Copper().cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Cache"
    }
}

