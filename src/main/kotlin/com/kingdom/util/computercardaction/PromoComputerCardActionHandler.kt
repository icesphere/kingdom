package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object PromoComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Black Market" -> when (type) {
                OldCardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
                OldCardAction.TYPE_CHOOSE_CARDS -> {
                    val cardNames = ArrayList<String>()
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_CHOOSE_IN_ORDER -> {
                    val cardNames = ArrayList<String>()
                    for (card in oldCardAction.cards) {
                        cardNames.add(card.name)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Envoy" -> {
                //todo determine which card would be best to get
                val cardToDiscard = computer.getHighestCostCard(oldCardAction.cards, false)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToDiscard!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Governor" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    val choice = if (computer.getNumCardsWorthTrashing(player.hand) > 0) {
                        "trash"
                    } else if (computer.goldsBought == 0) {
                        "money"
                    } else {
                        "cards"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    val cardNames = if (computer.getNumCardsWorthTrashing(oldCardAction.cards) > 0) {
                        computer.getCardsToTrash(oldCardAction.cards, 1)
                    } else {
                        ArrayList()
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Walled Village" -> when (type) {
                OldCardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                else -> {
                    val cardNames = oldCardAction.cards.map { it.name }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            else -> throw RuntimeException("Promo Card Action not handled for card: " + oldCardAction.cardName + " and type: " + type)
        }
    }
}
