package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object LeaderComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName

        when (cardName) {
            "Setup Leaders" -> {
                val cards = ArrayList(cardAction.cards)
                Collections.shuffle(cards)

                val cardIds = (0 until cardAction.numCards).map { cards[it].cardId }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Bilkis" -> {
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Plato" -> {
                val cardsToTrash = ArrayList<Int>()
                for (card in cardAction.cards) {
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
