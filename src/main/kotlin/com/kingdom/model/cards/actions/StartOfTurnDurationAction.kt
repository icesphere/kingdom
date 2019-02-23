package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface StartOfTurnDurationAction {

    fun durationStartOfTurnAction(player: Player)

}