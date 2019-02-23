package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface TurnEndedListenerForEventsBought {

    fun onTurnEnded(player: Player)

}