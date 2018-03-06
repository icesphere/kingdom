package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object AlchemyComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName

        when (cardName) {
            "Alchemist" -> {
                val cardNames = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    cardNames.add(card.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Apothecary" -> {
                //todo determine when to reorder
                val cardNames = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    cardNames.add(card.name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Apprentice" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, 1), null, null, -1)
            "Golem" -> {
                //todo determine which action is better to play first
                val cardNames = ArrayList<String>()
                cardNames.add(oldCardAction.cards[0].name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Herbalist" -> {
                val cardNames = ArrayList<String>()
                for (card in oldCardAction.cards) {
                    if (card.cost > 0) {
                        cardNames.add(card.name)
                    }
                    if (cardNames.size == oldCardAction.numCards) {
                        break
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Scrying Pool" -> {
                var yesNoAnswer = "yes"
                val topCard = oldCardAction.cards[0]
                if (topCard.name == Curse.NAME || topCard.isCopper) {
                    yesNoAnswer = "no"
                }
                player.getVictoryCards()
                        .filter { !it.isTreasure && !it.isAction }
                        .forEach { yesNoAnswer = "no" }
                if (oldCardAction.playerId == player.userId) {
                    yesNoAnswer = when (yesNoAnswer) {
                        "yes" -> "no"
                        else -> "yes"
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, yesNoAnswer, null, -1)
            }
            "Transmute" -> //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(oldCardAction.cards, 1), null, null, -1)
            "University" -> {
                //todo determine which action would be best to get
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            else -> throw RuntimeException("Alchemy Card Action not handled for card: " + oldCardAction.cardName + " and type: " + oldCardAction.type)
        }
    }
}
