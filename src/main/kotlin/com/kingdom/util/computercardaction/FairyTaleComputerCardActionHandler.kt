package com.kingdom.util.computercardaction

import com.kingdom.model.OldCardAction
import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.computer.ComputerPlayer
import com.kingdom.util.cardaction.CardActionHandler
import java.util.*

object FairyTaleComputerCardActionHandler {
    fun handleCardAction(oldCardAction: OldCardAction, computer: ComputerPlayer) {

        val game = computer.game
        val player = computer.player

        val cardName = oldCardAction.cardName
        val type = oldCardAction.type

        when (cardName) {
            "Bridge Troll" -> {
                //todo better algorithm for deciding which card to add token to
                val cardNames = ArrayList<String>()
                val card = computer.getRandomHighestCostCardFromCostMap(8, false)
                if (card != null) {
                    cardNames.add(card.name)
                } else {
                    cardNames.add(oldCardAction.cards[0].name)
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Druid" -> {
                //todo determine when not to discard a victory card
                val cardsToDiscard = oldCardAction.cards.map { it.name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1)
            }
            "Enchanted Palace" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
            "Lost Village 1" -> //todo determine when it is good to get +2 actions
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "draw", -1)
            "Lost Village" -> //todo determine when it is good to discard
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "draw", -1)
            "Magic Beans" -> if (type == OldCardAction.TYPE_CHOICES) {
                //todo determine when it is good to return to supply
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "trash", -1)
            } else {
                val cardToGain = computer.getHighestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToGain!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Master Huntsman" -> {
                //todo determine best action to discard
                val cardToDiscard = computer.getLowestCostCard(oldCardAction.cards)
                val cardNames = ArrayList<String>()
                cardNames.add(cardToDiscard!!.name)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Quest" -> {
                //todo determine best choice
                val cardNames = ArrayList<String>()
                cardNames.add(Estate.NAME)
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Sorceress" -> //todo determine best choices
                CardActionHandler.handleSubmittedCardAction(game, player, null!!, null, "coins", -1)
            "Storybook" -> {
                val cardNames = oldCardAction.cards.map { it.name }
                CardActionHandler.handleSubmittedCardAction(game, player, cardNames, null, null, -1)
            }
            "Tinker" -> CardActionHandler.handleSubmittedCardAction(game, player, null!!, "yes", null, -1)
        }
    }
}
