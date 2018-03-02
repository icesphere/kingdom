package com.kingdom.util.computercardaction

import com.kingdom.model.Card
import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object SalvationComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName

        when (cardName) {
            "Alms" -> {
                //todo don't use this card if only have good treasure cards in hand
                val cardToTrash = computer.getLowestCostCard(cardAction.cards)
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
                val attackToTrash = computer.getLowestCostCard(cardAction.cards)
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
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Edict" -> {
                //todo need better logic to determine best choice
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Graverobber" -> when {
                cardAction.type == CardAction.TYPE_CHOOSE_CARDS -> CardActionHandler.handleSubmittedCardAction(game, player, ArrayList(), null, null, -1)
                else -> {
                    val yesNoAnswer = if (cardAction.cards[0].cost > 0 || computer.wantsCoppers()) {
                        "yes"
                    } else {
                        "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Mendicant" -> {
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Scriptorium" -> {
                //todo determine best action to discard
                val cardToDiscard = computer.getLowestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToDiscard!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
        }
    }
}
