package com.kingdom.util.computercardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object SalvationComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName

        when (cardName) {
            "Alms" -> {
                //todo don't use this card if only have good treasure cards in hand
                val cardToTrash = computer.getLowestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToTrash!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Archbishop" -> {
                //todo better logic to determine best choice
                val choice = if (player.actions == 0) {
                    "actions"
                } else {
                    "sins"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Assassin" -> {
                //todo determine best attack card to trash
                val attackToTrash = computer.getLowestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(attackToTrash!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Baptistry" -> {
                //todo determine best card
                val cardIds = ArrayList<Int>()
                cardIds.add(Card.COPPER_ID)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Bell Tower" -> //todo determine best choice
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "after", -1)
            "Catacombs" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Edict" -> {
                //todo need better logic to determine best choice
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Graverobber" -> when {
                oldCardAction.type == OldCardAction.TYPE_CHOOSE_CARDS -> CardActionHandler.handleSubmittedCardAction(game, player, ArrayList(), null, null, -1)
                else -> {
                    val yesNoAnswer = if (oldCardAction.cards[0].cost > 0 || computer.wantsCoppers()) {
                        "yes"
                    } else {
                        "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Mendicant" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Scriptorium" -> {
                //todo determine best action to discard
                val cardToDiscard = computer.getLowestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToDiscard!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
        }
    }
}
