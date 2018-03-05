package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler

object ReactionComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {
        val game = computer.game
        val player = computer.player
        val cardName = oldCardAction.cardName

        when (cardName) {
            "Choose Reaction" -> //todo figure out if one if better than another to play first
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, oldCardAction.choices[0].value, -1)
            "Duchess for Duchy" -> //todo determine when it is good to get a Duchess
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
        }
    }
}
