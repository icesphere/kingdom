package com.kingdom.util.computercardaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldCardAction
import com.kingdom.model.Game
import com.kingdom.model.Player
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler

import java.util.ArrayList

object KingdomComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Artisan" -> when {
                oldCardAction.type == OldCardAction.TYPE_GAIN_CARDS_FROM_SUPPLY -> chooseHighestCostCard(oldCardAction, computer, game, player)
                else -> when {
                    player.actions == 0 && !player.actionCards.isEmpty() -> {
                        val card = computer.getHighestCostCard(player.actionCards)
                        chooseCard(oldCardAction, game, player, card)
                    }
                    else -> chooseLowestCostCard(oldCardAction, computer, game, player)
                }
            }
            "Bureaucrat" -> {
                val cardNames = computer.getCardsNotNeeded(oldCardAction.cards, 1)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Cellar" -> {
                val cardsToDiscard = oldCardAction.cards
                        .filter { computer.isCardToDiscard(it) }
                        .map { it.name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
            }
            "Chancellor" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Chapel" -> {
                val cardsToTrash = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    if (computer.isCardToTrash(card)) {
                        cardsToTrash.add(card.name)
                    }
                    if (cardsToTrash.size == 4) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1)
            }
            "Feast" -> chooseHighestCostCard(oldCardAction, computer, game, player)
            "Library" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Militia" -> {
                val numCardsToDiscard = player.hand.size - 3
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, numCardsToDiscard), null, null, -1)
            }
            "Mine" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> chooseLowestCostCard(oldCardAction, computer, game, player)
                else -> chooseHighestCostCard(oldCardAction, computer, game, player)
            }
            "Remodel" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> chooseLowestCostCard(oldCardAction, computer, game, player)
                else -> chooseHighestCostCard(oldCardAction, computer, game, player)
            }
            "Spy" -> {
                var yesNoAnswer = "yes"
                val topCard = oldCardAction.cards[0]
                if (topCard.name == Curse.NAME || topCard.isCopper) {
                    yesNoAnswer = "no"
                }
                player.getVictoryCards()
                        .filter { !it.isTreasure && !it.isAction }
                        .forEach { yesNoAnswer = "no" }
                if (oldCardAction.playerId == player.userId) {
                    if (yesNoAnswer == "yes") {
                        yesNoAnswer = "no"
                    } else {
                        yesNoAnswer = "yes"
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Thief" -> {
                val cardNames = ArrayList<String>()
                when (type) {
                    OldCardAction.TYPE_CHOOSE_CARDS -> when {
                        oldCardAction.numCards >= 1 -> {
                            val card1 = oldCardAction.cards[0]
                            when {
                                oldCardAction.cards.size == 2 -> {
                                    val card2 = oldCardAction.cards[1]
                                    when {
                                        card1.isTreasure && card2.isTreasure -> when {
                                            card1.cost > card2.cost -> cardNames.add(card1.name)
                                            else -> cardNames.add(card2.name)
                                        }
                                        card1.isTreasure -> cardNames.add(card1.name)
                                        else -> cardNames.add(card2.name)
                                    }
                                }
                                else -> cardNames.add(card1.name)
                            }
                        }
                    }
                    else -> when {
                        oldCardAction.numCards > 0 -> oldCardAction.cards
                                .filter { it.cost > 0 }
                                .mapTo(cardNames) { it.name }
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Throne Room" -> {
                val cardToPlay = computer.getActionToDuplicate(oldCardAction.cards, 2)
                chooseCard(oldCardAction, game, player, cardToPlay)
            }
            "Workshop" -> {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            else -> throw RuntimeException("Kingdom Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }

    private fun chooseLowestCostCard(oldCardAction: OldCardAction, computer: ComputerPlayer, game: Game, player: Player) {
        val card = computer.getLowestCostCard(oldCardAction.cards)
        chooseCard(oldCardAction, game, player, card)
    }

    private fun chooseHighestCostCard(oldCardAction: OldCardAction, computer: ComputerPlayer, game: Game, player: Player) {
        val card = computer.getHighestCostCard(oldCardAction.cards)
        chooseCard(oldCardAction, game, player, card)
    }

    private fun chooseCard(oldCardAction: OldCardAction, game: Game, player: Player, card: Card?) {
        var cardToChoose = card
        if (cardToChoose == null) {
            cardToChoose = oldCardAction.cards[0]
        }
        val cardNames = ArrayList<String>()
        cardNames.add(cardToChoose.name)
        CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
    }
}
