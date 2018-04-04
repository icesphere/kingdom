package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

interface ActionResultHandler {

    fun processActionResult(currentTurnPlayer: Player, actionPlayer: Player, result: ActionResult)
}