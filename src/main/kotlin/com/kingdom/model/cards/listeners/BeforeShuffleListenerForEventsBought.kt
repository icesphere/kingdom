package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface BeforeShuffleListenerForEventsBought {

    fun beforeShuffle(player: Player)
}
