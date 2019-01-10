package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Masterpiece : GuildsCard(NAME, CardType.Treasure, 3), AfterCardBoughtListenerForSelf, ChoiceActionCard {

    init {
        addCoins = 1
        special = "When you buy this, you may overpay for it. For each \$1 you overpaid, gain a Silver."
        isOverpayForCardAllowed = true
        textSize = 73
        fontSize = 11
    }

    override fun afterCardBought(player: Player) {
        if (player.availableCoins > 0) {
            player.yesNoChoice(this, "For each \$1 you overpaid, gain a Silver. Overpay?")
        } else {
            player.showInfoMessage("No coins available for overpaying")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val choosingOverpayAmount = info != null && info as Boolean

        if (choosingOverpayAmount) {
            player.addCoins(choice * -1)
            repeat(choice) {
                player.gainSupplyCard(Silver())
            }
            player.addEventLogWithUsername("gained $choice ${Silver().cardNameWithBackgroundColor}")
        } else {
            if (choice == 1) {
                val choices = mutableListOf<Choice>()

                for (i in 1..player.availableCoins) {
                    choices.add(Choice(i, i.toString()))
                }

                player.makeChoiceFromListWithInfo(this, "How much do you want to overpay?", true, choices)
            }
        }
    }

    companion object {
        const val NAME: String = "Masterpiece"
    }
}

