package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.Game
import com.kingdom.model.Player
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler

import java.util.ArrayList

object FanComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName

        when (cardName) {
            "Archivist" -> //todo determine when other choice would be better
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "draw", -1)
            "Museum" -> {
                //todo add logic for when to use museum
                val cardIds = ArrayList<Int>()
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
        }
    }
}
