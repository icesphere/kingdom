package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object ProsperityComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Bishop" -> {
                val cardIds = if (computer.getNumCardsWorthTrashing(cardAction.cards) > 0) {
                    computer.getCardsToTrash(cardAction.cards, 1)
                } else {
                    computer.getCardsToDiscard(cardAction.cards, 1, false)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Bishop 2" -> {
                val cardIds = if (computer.getNumCardsWorthTrashing(cardAction.cards) > 0) {
                    computer.getCardsToTrash(cardAction.cards, 1)
                } else {
                    ArrayList()
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Contraband" -> {
                //todo better logic for determining card
                val cardIds = ArrayList<Int>()
                val card = computer.getHighestCostCard(cardAction.cards)
                cardIds.add(card!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Counting House" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, null, cardAction.endNumber)
            "Expand" -> when (type) {
                CardAction.TYPE_TRASH_CARDS_FROM_HAND -> {
                    val cardToTrash = computer.getLowestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    if (cardToTrash != null) {
                        cardIds.add(cardToTrash.cardId)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                else -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    if (cardToGain != null) {
                        cardIds.add(cardToGain.cardId)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Forge" -> //todo need way better logic for this card
                when (type) {
                    CardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                        var numToTrash = 3
                        if (cardAction.numCards < 3) {
                            numToTrash = cardAction.numCards
                        }
                        CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, numToTrash), null, null, -1)
                    }
                    else -> {
                        val cardToGain = computer.getHighestCostCard(cardAction.cards)
                        val cardIds = ArrayList<Int>()
                        if (cardToGain != null) {
                            cardIds.add(cardToGain.cardId)
                        }
                        CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                    }
                }
            "Goons" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "King's Court" -> {
                var cardToPlay = computer.getActionToDuplicate(cardAction.cards, 3)
                if (cardToPlay == null) {
                    cardToPlay = cardAction.cards[0]
                }
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToPlay.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Loan" -> {
                val choice = if (cardAction.cards[0].isCopper) {
                    "trash"
                } else {
                    "discard"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Mint" -> {
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                if (cardToGain != null) {
                    cardIds.add(cardToGain.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Mountebank" -> //todo determine when it would be good to get curse and copper
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "discard", -1)
            "Rabble" -> {
                //todo determine when to reorder
                val cardIds = cardAction.cards.map { it.cardId }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Royal Seal" -> {
                val yesNoAnswer = if (computer.isCardToDiscard(cardAction.cards[0])) {
                    "no"
                } else {
                    "yes"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Trade Route" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
            "Vault" -> when (type) {
                CardAction.TYPE_DISCARD_UP_TO_FROM_HAND -> {
                    val cardsToDiscard = cardAction.cards
                            .filter { computer.isCardToDiscard(it) }
                            .map { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
                }
                else -> {
                    val cardsToDiscard = cardAction.cards.count { computer.isCardToDiscard(it) }
                    val yesNoAnswer = if (cardsToDiscard >= cardAction.numCards) {
                        "yes"
                    } else {
                        "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Vault2" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, 2), null, null, -1)
            "Watchtower" -> {
                val choice = if (computer.isCardToTrash(cardAction.cards[0])) {
                    "trash"
                } else if (computer.isCardToDiscard(cardAction.cards[0])) {
                    "no_reveal"
                } else {
                    "deck"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            else -> throw RuntimeException("Prosperity Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }
}
