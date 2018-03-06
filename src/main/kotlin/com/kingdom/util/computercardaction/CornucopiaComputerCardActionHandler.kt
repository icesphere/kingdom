package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object CornucopiaComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Followers" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "Hamlet" -> when (type) {
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, 1), null, null, -1)
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
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, 1), null, null, -1)
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
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                OldCardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            }
            "Horn of Plenty" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Jester" -> {
                var choice = "them"
                if (oldCardAction.cards[0].cost >= 5 || oldCardAction.cards[0].addActions > 0) {
                    choice = "me"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Remake" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Tournament" -> when (type) {
                OldCardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                OldCardAction.TYPE_CHOICES -> {
                    var choice = "prize"
                    if (game.prizeCards.isEmpty()) {
                        choice = "duchy"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                OldCardAction.TYPE_GAIN_CARDS -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
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
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                OldCardAction.TYPE_CHOICES -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "reveal", -1)
            }
            else -> throw RuntimeException("Cornucopia Card Action not handled for card: " + oldCardAction.cardName + " and type: " + type)
        }
    }
}
