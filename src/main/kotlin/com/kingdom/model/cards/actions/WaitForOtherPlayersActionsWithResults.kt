package com.kingdom.model.cards.actions

import com.kingdom.model.players.Player

class WaitForOtherPlayersActionsWithResults(private val currentTurnPlayer: Player,
                                            private val resultHandler: ActionResultHandler)
    : WaitForOtherPlayersActions(currentTurnPlayer) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        resultHandler.processActionResult(currentTurnPlayer, player, result)
        
        return super.processActionResult(player, result)
    }
}