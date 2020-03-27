package com.kingdom.model.cards

import com.kingdom.model.players.Player

interface PermanentDuration : MultipleTurnDuration {

    override fun keepAtEndOfTurn(player: Player) = true

}