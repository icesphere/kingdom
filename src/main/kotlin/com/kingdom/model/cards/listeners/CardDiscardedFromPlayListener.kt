package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface CardDiscardedFromPlayListener {

    fun onCardDiscarded(player: Player)
}