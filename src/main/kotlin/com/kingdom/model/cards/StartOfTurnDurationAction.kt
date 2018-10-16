package com.kingdom.model.cards

import com.kingdom.model.players.Player

interface StartOfTurnDurationAction {

    fun durationStartOfTurnAction(player: Player)

}