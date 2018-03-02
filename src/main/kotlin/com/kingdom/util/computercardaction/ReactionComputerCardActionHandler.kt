package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.Game
import com.kingdom.model.Player
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler

object ReactionComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {
        val game = computer.game
        val player = computer.player
        val cardName = cardAction.cardName

        when (cardName) {
            "Choose Reaction" -> //todo figure out if one if better than another to play first
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, cardAction.choices[0].value, -1)
            "Duchess for Duchy" -> //todo determine when it is good to get a Duchess
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
        }
    }
}
