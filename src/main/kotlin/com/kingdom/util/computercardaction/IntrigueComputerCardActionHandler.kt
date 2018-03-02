package com.kingdom.util.computercardaction

import com.kingdom.model.Card
import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object IntrigueComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Baron" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Courtyard" -> {
                val cardIds = ArrayList<Int>()
                cardIds.add(computer.getCardToPutOnTopOfDeck(cardAction.cards)!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Ironworks" -> {
                //todo determine which card would be best to get
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Masquerade" -> when (type) {
                CardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    val cardIds = ArrayList<Int>()
                    if (computer.getNumCardsWorthTrashing(cardAction.cards) > 0) {
                        cardIds.addAll(computer.getCardsToTrash(cardAction.cards, 1))
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                else -> {
                    val cardIds = ArrayList<Int>()
                    cardIds.add(computer.getCardToPass(cardAction.cards)!!)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Mining Village" -> {
                var yesNo = "no"
                if (computer.difficulty >= 3 && player.turns < 6 && (player.coins == 3 || player.coins == 4)) {
                    yesNo = "yes"
                }
                if (computer.onlyBuyVictoryCards()) {
                    yesNo = "yes"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNo, null, -1)
            }
            "Minion" -> {
                var choice = "coins"

                val numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.hand)
                if (numCardsWorthDiscarding >= 3 && player.coins < 5) {
                    choice = "discard"
                }

                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Nobles" -> {
                val choice = when {
                    player.actions == 0 -> "actions"
                    else -> "cards"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Pawn" -> //todo determine when other choices would be better
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "cardAndAction", -1)
            "Saboteur" -> {
                val cardIds = ArrayList<Int>()
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                if (cardToGain!!.cost > 2) {
                    cardIds.add(cardToGain.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Scout" -> {
                //todo determine when to reorder
                val cardIds = ArrayList<Int>()
                for (card in cardAction.cards) {
                    cardIds.add(card.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Secret Chamber" -> when (type) {
                CardAction.TYPE_DISCARD_UP_TO_FROM_HAND -> {
                    val cardsToDiscard = cardAction.cards
                            .filter { computer.isCardToDiscard(it) }
                            .mapTo(ArrayList()) { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
                }
                else -> //todo determine when putting cards on top of deck is a good idea
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
            }
            "Steward" -> when (type) {
                CardAction.TYPE_CHOICES -> {
                    val cardsToTrash = computer.getNumCardsWorthTrashing(cardAction.cards)
                    //todo determine when it would be good to trash just one card
                    val choice = when {
                        !computer.isGardensStrategy && cardsToTrash >= 2 && (player.actions > 0 || player.coins < 3) -> "trash"
                        player.actions > 0 && player.coins < 3 -> "cards"
                        else -> "coins"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
            }
            "Swindler" -> {
                val cardIds = ArrayList<Int>()
                if (cardAction.cards[0].cost == 0 && game.supply[Card.CURSE_ID]!! > 0) {
                    cardIds.add(Card.CURSE_ID)
                } else {
                    Collections.shuffle(cardAction.cards)
                    cardIds.add(cardAction.cards[0].cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Torturer" -> when (type) {
                CardAction.TYPE_CHOICES -> {
                    //todo determine other situations where getting a curse would be best
                    val choice = if (game.supply[Card.CURSE_ID] == 0) {
                        "curse"
                    } else {
                        "discard"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> {
                    val numCardsToDiscard = cardAction.numCards
                    CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, numCardsToDiscard), null, null, -1)
                }
            }
            "Trading Post" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
            "Upgrade" -> when (type) {
                CardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Wishing Well" -> {
                //todo determine which card is most likely to show up
                val cardIds = ArrayList<Int>()
                cardIds.add(Card.COPPER_ID)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            else -> throw RuntimeException("Intrigue Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }
}
