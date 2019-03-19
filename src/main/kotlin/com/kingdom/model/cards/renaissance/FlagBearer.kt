package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class FlagBearer : RenaissanceCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        disabled = true
        addCoins = 2
        special = "When you gain or trash this, take the Flag."
    }

    override fun afterCardGained(player: Player) {
        //todo
    }

    override fun afterCardTrashed(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Flag Bearer"
    }
}