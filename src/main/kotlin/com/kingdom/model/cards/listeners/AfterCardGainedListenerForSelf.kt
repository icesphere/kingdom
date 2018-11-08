package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface AfterCardGainedListenerForSelf {

    fun afterCardGained(player: Player)

}