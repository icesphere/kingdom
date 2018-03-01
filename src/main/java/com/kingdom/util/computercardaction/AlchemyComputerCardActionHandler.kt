package com.kingdom.util.computercardaction

import com.kingdom.model.Card
import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object AlchemyComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName

        when (cardName) {
            "Alchemist" -> {
                val cardIds = ArrayList<Int>()
                for (card in cardAction.cards) {
                    cardIds.add(card.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Apothecary" -> {
                //todo determine when to reorder
                val cardIds = ArrayList<Int>()
                for (card in cardAction.cards) {
                    cardIds.add(card.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Apprentice" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, 1), null, null, -1)
            "Golem" -> {
                //todo determine which action is better to play first
                val cardIds = ArrayList<Int>()
                cardIds.add(cardAction.cards[0].cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Herbalist" -> {
                val cardIds = ArrayList<Int>()
                for (card in cardAction.cards) {
                    if (card.cost > 0) {
                        cardIds.add(card.cardId)
                    }
                    if (cardIds.size == cardAction.numCards) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Scrying Pool" -> {
                var yesNoAnswer = "yes"
                val topCard = cardAction.cards[0]
                if (topCard.cardId == Card.CURSE_ID || topCard.cardId == Card.COPPER_ID) {
                    yesNoAnswer = "no"
                }
                player.getVictoryCards()
                        .filter { !it.isTreasure && !it.isAction }
                        .forEach { yesNoAnswer = "no" }
                if (cardAction.playerId == player.userId) {
                    yesNoAnswer = when (yesNoAnswer) {
                        "yes" -> "no"
                        else -> "yes"
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Transmute" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, 1), null, null, -1)
            "University" -> {
                //todo determine which action would be best to get
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            else -> throw RuntimeException("Alchemy Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }
}
