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

                val cardNames = (0 until oldCardAction.numCards).map { cards[it].name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Bilkis" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Plato" -> {
                val cardsToTrash = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    if (computer.isCardToTrash(card)) {
                        cardsToTrash.add(card.name)
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
