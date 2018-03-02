package com.kingdom.util.computercardaction

import com.kingdom.model.Card
import com.kingdom.model.CardAction
import com.kingdom.model.Game
import com.kingdom.model.Player
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler

import java.util.ArrayList

object KingdomComputerCardActionHandler {
    fun handleCardAction(cardAction: CardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = cardAction.cardName
        val type = cardAction.type

        when (cardName) {
            "Artisan" -> when {
                cardAction.type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> chooseHighestCostCard(cardAction, computer, game, player)
                else -> when {
                    player.actions == 0 && !player.actionCards.isEmpty() -> {
                        val card = computer.getHighestCostCard(player.actionCards)
                        chooseCard(cardAction, game, player, card)
                    }
                    else -> chooseLowestCostCard(cardAction, computer, game, player)
                }
            }
            "Bureaucrat" -> {
                val cardIds = computer.getCardsNotNeeded(cardAction.cards, 1)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Cellar" -> {
                val cardsToDiscard = cardAction.cards
                        .filter { computer.isCardToDiscard(it) }
                        .map { it.cardId }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
            }
            "Chancellor" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Chapel" -> {
                val cardsToTrash = ArrayList<Int>()
                for (card in cardAction.cards) {
                    if (computer.isCardToTrash(card)) {
                        cardsToTrash.add(card.cardId)
                    }
                    if (cardsToTrash.size == 4) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1)
            }
            "Feast" -> chooseHighestCostCard(cardAction, computer, game, player)
            "Library" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Militia" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "Mine" -> when (type) {
                CardAction.TYPE_TRASH_CARDS_FROM_HAND -> chooseLowestCostCard(cardAction, computer, game, player)
                else -> chooseHighestCostCard(cardAction, computer, game, player)
            }
            "Remodel" -> when (type) {
                CardAction.TYPE_TRASH_CARDS_FROM_HAND -> chooseLowestCostCard(cardAction, computer, game, player)
                else -> chooseHighestCostCard(cardAction, computer, game, player)
            }
            "Spy" -> {
                var yesNoAnswer = "yes"
                val topCard = cardAction.cards[0]
                if (topCard.cardId == Card.CURSE_ID || topCard.cardId == Card.COPPER_ID) {
                    yesNoAnswer = "no"
                }
                player.getVictoryCards()
                        .filter { !it.isTreasure && !it.isAction }
                        .forEach { yesNoAnswer = "no" }
                if (cardAction.playerId == player.userId) {
                    if (yesNoAnswer == "yes") {
                        yesNoAnswer = "no"
                    } else {
                        yesNoAnswer = "yes"
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Thief" -> {
                val cardIds = ArrayList<Int>()
                when (type) {
                    CardAction.TYPE_CHOOSE_CARDS -> when {
                        cardAction.numCards >= 1 -> {
                            val card1 = cardAction.cards[0]
                            when {
                                cardAction.cards.size == 2 -> {
                                    val card2 = cardAction.cards[1]
                                    when {
                                        card1.isTreasure && card2.isTreasure -> when {
                                            card1.cost > card2.cost -> cardIds.add(card1.cardId)
                                            else -> cardIds.add(card2.cardId)
                                        }
                                        card1.isTreasure -> cardIds.add(card1.cardId)
                                        else -> cardIds.add(card2.cardId)
                                    }
                                }
                                else -> cardIds.add(card1.cardId)
                            }
                        }
                    }
                    else -> when {
                        cardAction.numCards > 0 -> cardAction.cards
                                .filter { it.cost > 0 }
                                .mapTo(cardIds) { it.cardId }
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            "Throne Room" -> {
                val cardToPlay = computer.getActionToDuplicate(cardAction.cards, 2)
                chooseCard(cardAction, game, player, cardToPlay)
            }
            "Workshop" -> {
                val cardToGain = computer.getHighestCostCard(cardAction.cards)
                val cardIds = ArrayList<Int>()
                cardIds.add(cardToGain!!.cardId)
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
            }
            else -> throw RuntimeException("Kingdom Card Action not handled for card: " + cardAction.cardName + " and type: " + cardAction.type)
        }
    }

    private fun chooseLowestCostCard(cardAction: CardAction, computer: ComputerPlayer, game: Game, player: Player) {
        val card = computer.getLowestCostCard(cardAction.cards)
        chooseCard(cardAction, game, player, card)
    }

    private fun chooseHighestCostCard(cardAction: CardAction, computer: ComputerPlayer, game: Game, player: Player) {
        val card = computer.getHighestCostCard(cardAction.cards)
        chooseCard(cardAction, game, player, card)
    }

    private fun chooseCard(cardAction: CardAction, game: Game, player: Player, card: Card?) {
        var cardToChoose = card
        if (cardToChoose == null) {
            cardToChoose = cardAction.cards[0]
        }
        val cardIds = ArrayList<Int>()
        cardIds.add(cardToChoose.cardId)
        CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1)
    }
}
