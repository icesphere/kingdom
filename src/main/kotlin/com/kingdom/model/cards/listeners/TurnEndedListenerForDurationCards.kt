package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface TurnEndedListenerForDurationCards {

    fun onTurnEnded(player: Player)

}