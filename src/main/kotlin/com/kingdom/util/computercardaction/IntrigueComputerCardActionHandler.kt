package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object IntrigueComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Baron" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Courtyard" -> {
                val cardNames = ArrayList<String>()
                cardNames.add(computer.getCardToPutOnTopOfDeck(oldCardAction.cards)!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Ironworks" -> {
                //todo determine which card would be best to get
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Masquerade" -> when (type) {
                OldCardAction.TYPE_TRASH_UP_TO_FROM_HAND -> {
                    val cardNames = ArrayList<String>()
                    if (computer.getNumCardsWorthTrashing(oldCardAction.cards) > 0) {
                        cardNames.addAll(computer.getCardsToTrash(oldCardAction.cards, 1))
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
                else -> {
                    val cardNames = ArrayList<String>()
                    cardNames.add(computer.getCardToPass(oldCardAction.cards)!!)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
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
                val cardNames = ArrayList<String>()
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                if (cardToGain!!.cost > 2) {
                    cardNames.add(cardToGain.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Scout" -> {
                //todo determine when to reorder
                val cardNames = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    cardNames.add(card.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Secret Chamber" -> when (type) {
                OldCardAction.TYPE_DISCARD_UP_TO_FROM_HAND -> {
                    val cardsToDiscard = oldCardAction.cards
                            .filter { computer.isCardToDiscard(it) }
                            .mapTo(ArrayList()) { it.name }
                    CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
                }
                else -> //todo determine when putting cards on top of deck is a good idea
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, "no", null, -1)
            }
            "Steward" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    val cardsToTrash = computer.getNumCardsWorthTrashing(oldCardAction.cards)
                    //todo determine when it would be good to trash just one card
                    val choice = when {
                        !computer.isGardensStrategy && cardsToTrash >= 2 && (player.actions > 0 || player.coins < 3) -> "trash"
                        player.actions > 0 && player.coins < 3 -> "cards"
                        else -> "coins"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            }
            "Swindler" -> {
                val cardNames = ArrayList<String>()
                if (oldCardAction.cards[0].cost == 0 && game.supply[Curse.NAME]!! > 0) {
                    cardNames.add(Curse.NAME)
                } else {
                    Collections.shuffle(oldCardAction.cards)
                    cardNames.add(oldCardAction.cards[0].name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Torturer" -> when (type) {
                OldCardAction.TYPE_CHOICES -> {
                    //todo determine other situations where getting a curse would be best
                    val choice = if (game.supply[Curse.NAME] == 0) {
                        "curse"
                    } else {
                        "discard"
                    }
                    CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, choice, -1)
                }
                else -> {
                    val numCardsToDiscard = oldCardAction.numCards
                    CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(oldCardAction.cards, numCardsToDiscard), null, null, -1)
                }
            }
            "Trading Post" -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
            "Upgrade" -> when (type) {
                OldCardAction.TYPE_TRASH_CARDS_FROM_HAND -> CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, oldCardAction.numCards), null, null, -1)
                else -> {
                    val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                    val cardNames = ArrayList<String>()
                    cardNames.add(cardToGain!!.name)
                    CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
                }
            }
            "Wishing Well" -> {
                //todo determine which card is most likely to show up
                val cardNames = ArrayList<String>()
                cardNames.add(Copper.NAME)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            else -> throw RuntimeException("Intrigue Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }
}
