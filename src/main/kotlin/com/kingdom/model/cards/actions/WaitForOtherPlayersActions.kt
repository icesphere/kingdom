package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

open class WaitForOtherPlayersActions(private val currentTurnPlayer: Player) : Action("Waiting for other players...") {
    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = opponentsHaveActions(currentTurnPlayer)

    override fun processActionResult(player: Player, result: ActionResult): Boolean = !opponentsHaveActions(currentTurnPlayer)

    private fun opponentsHaveActions(player: Player): Boolean {
        return player.opponents.any { it.currentAction != null }
    }
}