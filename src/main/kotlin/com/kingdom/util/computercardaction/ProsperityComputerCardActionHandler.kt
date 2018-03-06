package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object ProsperityComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Bishop" -> {
                val cardNames = if (computer.getNumCardsWorthTrashing(oldCardAction.cards) > 0) {
                    computer.getCardsToTrash(oldCardAction.cards, 1)
                } else {
                    computer.getCardsToDiscard(oldCardAction.cards, 1, false)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Bishop 2" -> {
                val cardNames = if (computer.getNumCardsWorthTrashing(oldCardAction.cards) > 0) {
                    computer.getCardsToTrash(oldCardAction.cards, 1)
                } else {
                    ArrayList()
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Contraband" -> {
                //todo better logic for determining card
                val cardNames = ArrayList<String>()
                val card = computer.getHighestCostCard(oldCardAction.cards)
                cardNames.add(card!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Counting House" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, null, oldCardAction.endNumber)
            "Expand" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> {
                    val cardToTrash = computer.getLowestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    if (cardToTrash != null) {
                        cardNames.add(cardToTrash.name)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                else -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    if (cardToGain != null) {
                        cardNames.add(cardToGain.name)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Forge" -> //todo need way better logic for this card
                when (type) {
                    OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                        var numToTrash = 3
                        if (oldCardAction.numCards < 3) {
                            numToTrash = oldCardAction.numCards
                        }
                        CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, numToTrash), null, null, -1)
                    }
                    else -> {
                        val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                        val cardNames = ArrayList<String>()
                        if (cardToGain != null) {
                            cardNames.add(cardToGain.name)
                        }
                        CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                    }
                }
            "Goons" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "King's Court" -> {
                var cardToPlay = computer.getActionToDuplicate(oldCardAction.cards, 3)
                if (cardToPlay == null) {
                    cardToPlay = oldCardAction.cards[0]
                }
                val cardNames = ArrayList<String>()
                cardNames.add(cardToPlay.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Loan" -> {
                val choice = if (oldCardAction.cards[0].isCopper) {
                    "trash"
                } else {
                    "discard"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Mint" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                if (cardToGain != null) {
                    cardNames.add(cardToGain.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Mountebank" -> //todo determine when it would be good to get curse and copper
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "discard", -1)
            "Rabble" -> {
                //todo determine when to reorder
                val cardNames = oldCardAction.cards.map { it.name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Royal Seal" -> {
                val yesNoAnswer = if (computer.isCardToDiscard(oldCardAction.cards[0])) {
                    "no"
                } else {
                    "yes"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Trade Route" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            "Vault" -> when (type) {
                OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND -> {
                    val cardsToDiscard = oldCardAction.cards
                            .filter { computer.isCardToDiscard(it) }
                            .map { it.name }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
                }
                else -> {
                    val cardsToDiscard = oldCardAction.cards.count { computer.isCardToDiscard(it) }
                    val yesNoAnswer = if (cardsToDiscard >= oldCardAction.numCards) {
                        "yes"
                    } else {
                        "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Vault2" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, 2), null, null, -1)
            "Watchtower" -> {
                val choice = if (computer.isCardToTrash(oldCardAction.cards[0])) {
                    "trash"
                } else if (computer.isCardToDiscard(oldCardAction.cards[0])) {
                    "no_reveal"
                } else {
                    "deck"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            else -> throw RuntimeException("Prosperity Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }
}
