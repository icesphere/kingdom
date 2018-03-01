package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object ProletariatComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {
        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Cattle Farm" -> {
                val choice = if (computer.isCardToDiscard(cardAction.associatedCard!!)) {
                    "discard"
                } else {
                    "back"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "City Planner" -> when (type) {
                CardAction.TYPE_CHOOSE_CARDS -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                CardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, cardAction.numCards), null, null, -1)
            }
            "Fruit Merchant" -> {
                val cardIds = ArrayList<Int>()
                var numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(cardAction.cards)
                if (numCardsWorthDiscarding > 2) {
                    numCardsWorthDiscarding = 2
                }
                if (numCardsWorthDiscarding > 0) {
                    cardIds.addAll(computer.getCardsToDiscard(cardAction.cards, numCardsWorthDiscarding))
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Hooligans" -> when (type) {
                CardAction.TYPE_CHOOSE_CARDS -> {
                    val cardIds = ArrayList<Int>()
                    val cardToPutOnTopOfDeck = computer.getCardToPutOnTopOfDeck(cardAction.cards)
                    cardIds.add(cardToPutOnTopOfDeck!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_CHOICES -> {
                    val choice = if (computer.isCardToDiscard(cardAction.associatedCard!!)) {
                        "deck"
                    } else {
                        "discard"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
            }
            "Orchard" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Rancher" -> when (type) {
                CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_CHOOSE_UP_TO -> {
                    val cardIds = ArrayList<Int>()
                    Collections.shuffle(cardAction.cards)
                    cardIds.add(cardAction.cards[0].cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_CHOICES -> //todo choice should usually be "cattle" once the computer knows how to use cattle tokens
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
                CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_CHOOSE_CARDS -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, 1), null, null, -1)
            }
            else -> throw RuntimeException("Proletariat Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }
}
