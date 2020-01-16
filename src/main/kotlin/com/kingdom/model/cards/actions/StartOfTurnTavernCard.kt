package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface StartOfTurnTavernCard {

    fun onStartOfTurn(player: Player)

}