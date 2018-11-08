package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface AfterCardBoughtListenerForSelf {

    fun afterCardBought(player: Player)

}