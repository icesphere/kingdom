package com.kingdom.util.computercardaction

import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object PromoComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Black Market" -> when (type) {
                CardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
                CardAction.TYPE_CHOOSE_CARDS -> {
                    val cardIds = ArrayList<Int>()
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_CHOOSE_IN_ORDER -> {
                    val cardIds = ArrayList<Int>()
                    for (card in cardAction.cards) {
                        cardIds.add(card.cardId)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Envoy" -> {
                //todo determine which card would be best to get
                val cardToDiscard = computer.getHighestCostCard(cardAction.cards, false)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToDiscard!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Governor" -> when (type) {
                CardAction.TYPE_CHOICES -> {
                    val choice = if (computer.getNumCardsWorthTrashing(player.hand) > 0) {
                        "trash"
                    } else if (computer.goldsBought == 0) {
                        "money"
                    } else {
                        "cards"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                CardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    val cardIds = if (computer.getNumCardsWorthTrashing(cardAction.cards) > 0) {
                        computer.getCardsToTrash(cardAction.cards, 1)
                    } else {
                        ArrayList()
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Walled Village" -> when (type) {
                CardAction.TYPE_YES_NO -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
                else -> {
                    val cardIds = cardAction.cards.map { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            else -> throw RuntimeException("Promo Card Action not handled for card: " + cardAction.cardName + " and type: " + type)
        }
    }
}
