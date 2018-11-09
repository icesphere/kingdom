package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface BeforeCardGainedListenerForSelf {

    fun beforeCardGained(player: Player)

}