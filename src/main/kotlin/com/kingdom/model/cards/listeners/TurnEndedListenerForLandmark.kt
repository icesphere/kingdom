package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface TurnEndedListenerForLandmark {

    fun onTurnEnded(player: Player)

}