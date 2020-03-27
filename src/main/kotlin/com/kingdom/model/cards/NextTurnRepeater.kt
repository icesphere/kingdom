package com.kingdom.model.cards

import com.kingdom.model.players.Player

interface NextTurnRepeater {

    var isNextTurn: Boolean

    fun keepAtEndOfTurn(player: Player): Boolean

}