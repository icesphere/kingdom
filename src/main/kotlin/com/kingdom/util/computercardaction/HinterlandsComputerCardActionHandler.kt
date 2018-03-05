package com.kingdom.util.computercardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldCardAction
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object HinterlandsComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Border Village" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Cartographer" -> when (type) {
                OldCardAction.TYPE_DISCARD_UP_TO -> {
                    val cardsToDiscard = ArrayList<Int>()
                    for (card in oldCardAction.cards) {
                        if (computer.isCardToDiscard(card)) {
                            cardsToDiscard.add(card.cardId)
                        }
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
                }
                OldCardAction.TYPE_CHOOSE_IN_ORDER -> {
                    //todo determine when to reorder
                    val cardIds = ArrayList<Int>()
                    for (card in oldCardAction.cards) {
                        cardIds.add(card.cardId)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Develop" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                OldCardAction.TYPE_CHOICES -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "more", -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Duchess" -> {
                val choice = if (computer.isCardToDiscard(oldCardAction.associatedCard!!)) {
                    "discard"
                } else {
                    "back"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
            }
            "Embassy" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            "Ill-Gotten Gains" -> {
                val yesNoAnswer: String
                if (computer.player.coins == 5 || computer.player.coins == 7) {
                    yesNoAnswer = "yes"
                } else {
                    yesNoAnswer = "no"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Farmland" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardIds = ArrayList<Int>()
                    cardIds.add(cardToGain!!.cardId)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Fool's Gold" -> {
                var yesNo = "yes"
                if (player.foolsGoldInHand > 1) {
                    yesNo = "no"
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNo, null, -1)
            }
            "Haggler" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Inn" -> when (type) {
                OldCardAction.TYPE_DISCARD_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                OldCardAction.TYPE_CHOOSE_UP_TO -> {
                    //todo don't add too many terminal actions
                    val cardIds = ArrayList<Int>()
                    for (card in oldCardAction.cards) {
                        cardIds.add(card.cardId)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Jack of all Trades" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    val choice: String
                    choice = if (computer.isCardToDiscard(oldCardAction.associatedCard!!)) {
                        "discard"
                    } else {
                        "back"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    val cardIds: List<Int>
                    cardIds = if (computer.getNumCardsWorthTrashing(oldCardAction.cards) > 0) {
                        computer.getCardsToTrash(oldCardAction.cards, 1)
                    } else {
                        ArrayList()
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Mandarin" -> {
                val cardIds = ArrayList<Int>()
                when (type) {
                    OldCardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK -> {
                        cardIds.add(computer.getCardToPutOnTopOfDeck(oldCardAction.cards)!!.cardId)
                        CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                    }
                    OldCardAction.TYPE_CHOOSE_IN_ORDER -> {
                        //todo determine when to reorder
                        for (card in oldCardAction.cards) {
                            cardIds.add(card.cardId)
                        }
                        CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                    }
                }
            }
            "Margrave" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "Noble Brigand" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "gold", -1)
            "Oasis" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            "Oracle" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    var choice: String
                    when {
                        oldCardAction.cards.size == 1 -> choice = if (computer.isCardToDiscard(oldCardAction.cards[0])) {
                            "back"
                        } else {
                            "discard"
                        }
                        else -> {
                            val firstCard = oldCardAction.cards[0]
                            val secondCard = oldCardAction.cards[1]
                            choice = when {
                                computer.isCardToDiscard(firstCard) && computer.isCardToDiscard(secondCard) -> "back"
                                !computer.isCardToDiscard(firstCard) && !computer.isCardToDiscard(secondCard) -> "discard"
                                else -> when {
                                    (firstCard.isTreasure || firstCard.isAction) && firstCard.cost >= 5 -> "discard"
                                    (secondCard.isTreasure || secondCard.isAction) && secondCard.cost >= 5 -> "discard"
                                    else -> "back"
                                }
                            }
                        }
                    }
                    if (oldCardAction.playerId == player.userId) {
                        choice = when (choice) {
                            "discard" -> "back"
                            else -> "discard"
                        }
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                OldCardAction.TYPE_CHOOSE_IN_ORDER -> {
                    //todo determine when to reorder
                    val cardIds = oldCardAction.cards.map { it.cardId }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
            }
            "Scheme" -> {
                val cardIds = ArrayList<Int>()
                for (card in oldCardAction.cards) {
                    if (card.cost > 4 || !card.isTerminalAction) {
                        cardIds.add(card.cardId)
                    }
                    if (cardIds.size == oldCardAction.numCards) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Spice Merchant" -> when (type) {
                OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    //todo determine when it is worth it to trash treasure cards other than Copper
                    val cardIds = ArrayList<Int>()
                    if (computer.player.treasureCards.contains(game.copperCard)) {
                        cardIds.add(Card.COPPER_ID)
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
                }
                OldCardAction.TYPE_CHOICES -> {
                    //todo better logic for which choice is best
                    val choice: String
                    choice = when {
                        player.coins in 4..5 && computer.goldsBought < 2 -> "money"
                        else -> "cards"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
            }
            "Stables" -> {
                val cardIds = ArrayList<Int>()
                if (computer.player.treasureCards.contains(game.copperCard)) {
                    cardIds.add(Card.COPPER_ID)
                } else if (computer.player.treasureCards.contains(game.silverCard)) {
                    cardIds.add(Card.SILVER_ID)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Trader" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    val choice = when {
                        oldCardAction.cards[0].cost < 3 -> "silver"
                        else -> "no_reveal"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> //todo better logic for determining which card to trash
                    CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, 1), null, null, -1)
            }
            "Tunnel" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            else -> throw RuntimeException("Hinterlands Card Action not handled for card: " + oldCardAction.cardName + " and type: " + type)
        }

    }
}
