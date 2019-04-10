package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface StartOfCleanupListener {

    fun onStartOfCleanup(player: Player)

}