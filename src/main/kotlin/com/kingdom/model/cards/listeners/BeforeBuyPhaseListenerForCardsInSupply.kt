package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface BeforeBuyPhaseListenerForCardsInSupply {

    fun beforeBuyPhase(player: Player)

}