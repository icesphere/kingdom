package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface StartOfCleanupListenerForCardsPlayedThisTurn {

    fun onStartOfCleanup(player: Player)

}