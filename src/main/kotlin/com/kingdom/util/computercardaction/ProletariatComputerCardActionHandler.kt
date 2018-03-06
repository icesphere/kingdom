package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object ProletariatComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {
        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Cattle Farm" -> {
                val choice = if (computer.isCardToDiscard(oldCardAction.associatedCard!!)) {
                    "discard"
                } else {
                    "back"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "City Planner" -> when (type) {
                OldCardAction.TYPE_CHOOSE_CARDS -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            }
            "Fruit Merchant" -> {
                val cardNames = ArrayList<String>()
                var numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(oldCardAction.cards)
                if (numCardsWorthDiscarding > 2) {
                    numCardsWorthDiscarding = 2
                }
                if (numCardsWorthDiscarding > 0) {
                    cardNames.addAll(computer.getCardsToDiscard(oldCardAction.cards, numCardsWorthDiscarding))
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Hooligans" -> when (type) {
                OldCardAction.TYPE_CHOOSE_CARDS -> {
                    val cardNames = ArrayList<String>()
                    val cardToPutOnTopOfDeck = computer.getCardToPutOnTopOfDeck(oldCardAction.cards)
                    cardNames.add(cardToPutOnTopOfDeck!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_CHOICES -> {
                    val choice = if (computer.isCardToDiscard(oldCardAction.associatedCard!!)) {
                        "deck"
                    } else {
                        "discard"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
            }
            "Orchard" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Rancher" -> when (type) {
                OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_CHOOSE_UP_TO -> {
                    val cardNames = ArrayList<String>()
                    Collections.shuffle(oldCardAction.cards)
                    cardNames.add(oldCardAction.cards[0].name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_CHOICES -> //todo choice should usually be "cattle" once the computer knows how to use cattle tokens
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "buy", -1)
            }
            "Squatter" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Shepherd" -> {
                var yesNo = "yes"
                if (computer.goldsBought == 0) {
                    yesNo = "no"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNo, null, -1)
            }
            "Trainee" -> when (type) {
                OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_CHOOSE_CARDS -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, 1), null, null, -1)
            }
            else -> throw RuntimeException("Proletariat Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }
}
