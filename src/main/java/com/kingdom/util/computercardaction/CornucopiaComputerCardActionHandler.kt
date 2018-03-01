package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object CornucopiaComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Followers" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "Hamlet" -> when (type) {
                CardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, 1), null, null, -1)
                else -> {
                    //todo better logic on when computer needs +1 action
                    val yesNoAnswer: String
                    val numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.hand)
                    if (numCardsWorthDiscarding > 0) {
                        yesNoAnswer = "yes"
                    } else {
                        yesNoAnswer = "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Hamlet2" -> when (type) {
                CardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, 1), null, null, -1)
                else -> {
                    //todo better logic on when computer needs +1 buy
                    val yesNoAnswer: String
                    val numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.hand)
                    yesNoAnswer = if (numCardsWorthDiscarding > 0) {
                        "yes"
                    } else {
                        "no"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
                }
            }
            "Horse Traders" -> when (type) {
                CardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, cardAction.numCards), null, null, -1)
                CardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            }
            "Horn of Plenty" -> {
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Jester" -> {
                var choice = "them"
                if (cardAction.cards[0].cost >= 5 || cardAction.cards[0].addActions > 0) {
                    choice = "me"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Remake" -> when (type) {
                CardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Tournament" -> when (type) {
                CardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                CardAction.TYPE_CHOICES -> {
                    var choice = "prize"
                    if (game.prizeCards.isEmpty()) {
                        choice = "duchy"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                CardAction.TYPE_GAIN_CARDS -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Trusty Steed" -> {
                //todo determine when other choices would be better
                var choice = "cardsAndCoins"
                if (!player.actionCards.isEmpty() && player.actions == 0) {
                    choice = "cardsAndActions"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Young Witch" -> when (type) {
                CardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, cardAction.numCards), null, null, -1)
                CardAction.TYPE_CHOICES -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "reveal", -1)
            }
            else -> throw RuntimeException("Cornucopia Card Action not handled for card: " + cardAction.cardName + " and type: " + type)
        }
    }
}
