package com.kingdom.model.cards

import com.kingdom.model.players.Player

interface NextTurnRepeater {

    fun keepAtEndOfTurn(player: Player): Boolean

}