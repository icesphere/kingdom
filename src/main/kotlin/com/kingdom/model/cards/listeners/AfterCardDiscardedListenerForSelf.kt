package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface AfterCardDiscardedListenerForSelf {

    fun afterCardDiscarded(player: Player)
}
