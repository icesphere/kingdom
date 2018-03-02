package com.kingdom.util.computercardaction

import com.kingdom.model.Card
import com.kingdom.model.CardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object SeasideComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Ambassador" -> {
                val cardIds = ArrayList<Int>()
                when (type) {
                    CardAction.TYPE_CHOOSE_CARDS -> cardIds.add(computer.getCardToPass(cardAction.cards)!!)
                    else -> //todo decide when not to add cards back into supply
                        cardAction.cards.mapTo(cardIds) { it.cardId }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Embargo" -> {
                //todo better algorithm for deciding which card to embargo
                val cardIds = ArrayList<Int>()
                val card = computer.getRandomHighestCostCardFromCostMap(5, false)
                if (card != null) {
                    cardIds.add(card.cardId)
                } else {
                    cardIds.add(cardAction.cards[0].cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Explorer" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "gold", -1)
            "Ghost Ship" -> when (type) {
                CardAction.TYPE_CHOOSE_IN_ORDER -> {
                    //todo determine when to reorder
                    val cardIds = cardAction.cards.map { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                else -> {
                    val numCardsNotNeeded = player.hand.size - 3
                    if (numCardsNotNeeded > 0) {
                        CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(cardAction.cards, numCardsNotNeeded), null, null, -1)
                    }
                }
            }
            "Haven" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(cardAction.cards, 1), null, null, -1)
            "Island" -> {
                val cardIds = ArrayList<Int>()
                var islandCard: Card? = cardAction.cards.firstOrNull { it.isVictoryOnly }
                if (islandCard == null) {
                    islandCard = computer.getLowestCostCard(cardAction.cards)
                }
                cardIds.add(islandCard!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Lookout" -> //todo need better way to determine cards
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, cardAction.numCards), null, null, -1)
            "Native Village" -> {
                //todo analyze cards to determine best choice
                val choice = if (player.nativeVillageCards.size > 2) {
                    "hand"
                } else {
                    "card"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Navigator" -> when (type) {
                CardAction.TYPE_CHOICES -> {
                    //todo better analysis of cards to determine best choice
                    val choice = if (player.coinsInHand < 3 || player.getVictoryCards().size > 2) {
                        "discard"
                    } else {
                        "deck"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> {
                    //todo determine when to reorder
                    val cardIds = cardAction.cards.map { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Pearl Diver" -> {
                val yesNoAnswer = when {
                    computer.isCardToDiscard(cardAction.cards[0]) -> "no"
                    else -> "yes"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Pirate Ship" -> when (type) {
                CardAction.TYPE_CHOICES -> {
                    val choice = if (player.pirateShipCoins > 2) {
                        "coins"
                    } else {
                        "attack"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> {
                    val cardIds = ArrayList<Int>()
                    if (cardAction.numCards == 1) {
                        when {
                            cardAction.cards[0].isTreasure && cardAction.cards[1].isTreasure -> {
                                val cardToTrash = computer.getHighestCostCard(cardAction.cards)
                                if (cardToTrash != null) {
                                    cardIds.add(cardToTrash.cardId)
                                }
                            }
                            cardAction.cards[0].isTreasure -> cardIds.add(cardAction.cards[0].cardId)
                            else -> cardIds.add(cardAction.cards[1].cardId)
                        }
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Salvager" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.cards, 1), null, null, -1)
            "Smugglers" -> {
                val cardIds = ArrayList<Int>()
                if (cardAction.numCards > 0) {
                    val cardToGain = computer.getHighestCostCard(cardAction.cards)
                    cardIds.add(cardToGain!!.cardId)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Treasury" -> {
                val cardIds = cardAction.cards.map { it.cardId }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Warehouse" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, cardAction.numCards), null, null, -1)
            else -> throw RuntimeException("Seaside Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }
}
