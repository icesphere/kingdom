package com.kingdom.model.cards

import com.kingdom.model.players.Player

interface MultipleTurnDuration {

    fun keepAtEndOfTurn(player: Player): Boolean

}