package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

open class WaitForOtherPlayersForResolveAttack(private val currentTurnPlayer: Player, private val attackCard: Card) : WaitForOtherPlayersActions(currentTurnPlayer) {

    override fun processActionResult(player: Player, result: ActionResult): Boolean {
        if (!currentTurnPlayer.isOpponentHasAction) {
            val attackResolver = attackCard as AttackCard

            val affectedOpponents = currentTurnPlayer.opponentsInOrder.filterNot { attackCard.playersExcludedFromCardEffects.contains(it) }

            attackResolver.resolveAttack(currentTurnPlayer, affectedOpponents)

            if (currentTurnPlayer.isOpponentHasAction) {
                currentTurnPlayer.waitForOtherPlayersToResolveActions()
            }

            return true
        }

        return false
    }

}