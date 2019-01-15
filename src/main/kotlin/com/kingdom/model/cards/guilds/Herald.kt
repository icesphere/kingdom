package com.kingdom.model.cards.guilds

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.players.Player

class Herald : GuildsCard(NAME, CardType.Action, 4), ChoiceActionCard, AfterCardBoughtListenerForSelf {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal the top card of your deck. If it is an Action, play it. When you buy this, you may overpay for it. For each \$1 you overpaid, look through your discard pile and put a card from it onto your deck."
        isOverpayForCardAllowed = true
        textSize = 85
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealTopCardOfDeck()

        if (card != null && card.isAction) {
            player.showInfoMessage("$cardNameWithBackgroundColor revealed and played ${card.cardNameWithBackgroundColor}")
            player.removeTopCardOfDeck()
            player.addActions(1, false)
            player.playCard(card)
        } else if (card != null) {
            player.showInfoMessage("$cardNameWithBackgroundColor revealed ${card.cardNameWithBackgroundColor}")
        }
    }

    override fun afterCardBought(player: Player) {
        if (player.availableCoins > 0) {
            player.yesNoChoice(this, "Overpay to gain a Silver for each \$1 you overpaid?")
        } else {
            player.showInfoMessage("No coins available for overpaying")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val choosingOverpayAmount = info != null && info as Boolean

        if (choosingOverpayAmount) {
            player.addCoins(choice * -1)
            repeat(choice) {
                player.addCardFromDiscardToTopOfDeck()
            }
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
        const val NAME: String = "Herald"
    }
}

