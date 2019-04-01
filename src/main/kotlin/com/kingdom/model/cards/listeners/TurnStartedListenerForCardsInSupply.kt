package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface TurnStartedListenerForCardsInSupply {

    fun turnStarted(player: Player)

}