package com.kingdom.util.computercardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object SeasideComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Ambassador" -> {
                val cardNames = ArrayList<String>()
                when (type) {
                    OldCardAction.TYPE_CHOOSE_CARDS -> cardNames.add(computer.getCardToPass(oldCardAction.cards)!!)
                    else -> //todo decide when not to add cards back into supply
                        oldCardAction.cards.mapTo(cardNames) { it.name }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Embargo" -> {
                //todo better algorithm for deciding which card to embargo
                val cardNames = ArrayList<String>()
                val card = computer.getRandomHighestCostCardFromCostMap(5, false)
                if (card != null) {
                    cardNames.add(card.name)
                } else {
                    cardNames.add(oldCardAction.cards[0].name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Explorer" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "gold", -1)
            "Ghost Ship" -> when (type) {
                OldCardAction.TYPE_CHOOSE_IN_ORDER -> {
                    //todo determine when to reorder
                    val cardNames = oldCardAction.cards.map { it.name }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                else -> {
                    val numCardsNotNeeded = player.hand.size - 3
                    if (numCardsNotNeeded > 0) {
                        CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(oldCardAction.cards, numCardsNotNeeded), null, null, -1)
                    }
                }
            }
            "Haven" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(oldCardAction.cards, 1), null, null, -1)
            "Island" -> {
                val cardNames = ArrayList<String>()
                var islandCard: Card? = oldCardAction.cards.firstOrNull { it.isVictoryOnly }
                if (islandCard == null) {
                    islandCard = computer.getLowestCostCard(oldCardAction.cards)
                }
                cardNames.add(islandCard!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Lookout" -> //todo need better way to determine cards
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
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
                OldCardAction.TYPE_CHOICES -> {
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
                    val cardNames = oldCardAction.cards.map { it.name }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Pearl Diver" -> {
                val yesNoAnswer = when {
                    computer.isCardToDiscard(oldCardAction.cards[0]) -> "no"
                    else -> "yes"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Pirate Ship" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    val choice = if (player.pirateShipCoins > 2) {
                        "coins"
                    } else {
                        "attack"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> {
                    val cardNames = ArrayList<String>()
                    if (oldCardAction.numCards == 1) {
                        when {
                            oldCardAction.cards[0].isTreasure && oldCardAction.cards[1].isTreasure -> {
                                val cardToTrash = computer.getHighestCostCard(oldCardAction.cards)
                                if (cardToTrash != null) {
                                    cardNames.add(cardToTrash.name)
                                }
                            }
                            oldCardAction.cards[0].isTreasure -> cardNames.add(oldCardAction.cards[0].name)
                            else -> cardNames.add(oldCardAction.cards[1].name)
                        }
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Salvager" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, 1), null, null, -1)
            "Smugglers" -> {
                val cardNames = ArrayList<String>()
                if (oldCardAction.numCards > 0) {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    cardNames.add(cardToGain!!.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Treasury" -> {
                val cardNames = oldCardAction.cards.map { it.name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Warehouse" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            else -> throw RuntimeException("Seaside Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }
}
