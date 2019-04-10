package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface AfterShuffleListener {

    fun afterShuffle(player: Player)

}