package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Stonemason : GuildsCard(NAME, CardType.Action, 2), TrashCardsForBenefitActionCard, AfterCardBoughtListenerForSelf, ChoiceActionCard {

    init {
        fontSize = 11
        special = "Trash a card from your hand. Gain 2 cards each costing less than it. When you buy this, you may overpay for it. If you do, gain 2 Action cards each costing the amount you overpaid."
        isOverpayForCardAllowed = true
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. Gain 2 cards each costing less than it.")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val trashedCard = trashedCards.first()

        val maxCost = player.getCardCostWithModifiers(trashedCard) - 1

        if (maxCost >= 0 && player.game.availableCards.any { player.getCardCostWithModifiers(it) <= maxCost }) {
            player.chooseSupplyCardToGain(maxCost)
            player.chooseSupplyCardToGain(maxCost)
        } else {
            player.showInfoMessage("No cards available to gain")
        }
    }

    override fun afterCardBought(player: Player) {
        if (player.availableCoins > 0) {
            player.yesNoChoice(this, "Overpay for ${this.cardNameWithBackgroundColor} to gain 2 Action cards each costing the amount you overpaid?")
        } else {
            player.showInfoMessage("No coins available for overpaying")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val choosingOverpayAmount = info != null && info as Boolean

        if (choosingOverpayAmount) {
            player.addCoins(choice * -1)
            player.chooseSupplyCardToGain(choice)
            player.chooseSupplyCardToGain(choice)
        } else {
            if (choice == 1) {
                val choices = mutableListOf<Choice>()

                var maxOverpay = player.game.availableCards.filter { it.isAction }.map { player.getCardCostWithModifiers(it) }.max() ?: return

                if (player.availableCoins < maxOverpay) {
                    maxOverpay = player.availableCoins
                }

                for (i in 1..maxOverpay) {
                    choices.add(Choice(i, i.toString()))
                }

                player.makeChoiceFromListWithInfo(this, "How much do you want to overpay?", true, choices)
            }
        }
    }

    companion object {
        const val NAME: String = "Stonemason"
    }
}

