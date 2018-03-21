package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class WaitForOtherPlayersToResolveActions : Action("Waiting for other players...") {
    override fun isCardActionable(card: Card, cardLocation: CardLocation, player: Player): Boolean = false

    override fun processAction(player: Player): Boolean = opponentsHaveActions(player)

    override fun processActionResult(player: Player, result: ActionResult): Boolean = !opponentsHaveActions(player)

    private fun opponentsHaveActions(player: Player): Boolean {
        return player.opponents.any { it.currentAction != null }
    }
}