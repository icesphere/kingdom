package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object LeaderComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName

        when (cardName) {
            "Setup Leaders" -> {
                val cards = ArrayList(oldCardAction.cards)
                Collections.shuffle(cards)

                val cardIds = (0 until oldCardAction.numCards).map { cards[it].cardId }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Bilkis" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Plato" -> {
                val cardsToTrash = ArrayList<Int>()
                for (card in oldCardAction.cards) {
                    if (computer.isCardToTrash(card)) {
                        cardsToTrash.add(card.cardId)
                    }
                    if (cardsToTrash.size == 2) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1)
            }
        }
    }
}
