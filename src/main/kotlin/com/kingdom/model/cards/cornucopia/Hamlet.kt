package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Hamlet : CornucopiaCard(NAME, CardType.Action, 2), ChoiceActionCard, DiscardCardsForBenefitActionCard {

    var discardingCardForAction: Boolean = true

    init {
        addCards = 1
        addActions = 1
        special = "You may discard a card for +1 Action. You may discard a card for +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.yesNoChoice(this, "Discard a card for +1 Action?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        when (choice) {
            1 -> {
                player.discardCardsForBenefit(this, 1, "")
            }
            2 -> {
                if (discardingCardForAction) {
                    discardingCardForAction = false

                    player.yesNoChoice(this, "Discard a card for +1 Buy?")
                } else {
                    discardingCardForAction = true
                }
            }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>) {
        if (discardingCardForAction) {
            player.addActions(1)

            discardingCardForAction = false

            if (player.hand.isNotEmpty()) {
                player.yesNoChoice(this, "Discard a card for +1 Buy?")
            }

        } else {
            player.addBuys(1)

            discardingCardForAction = true
        }
    }

    override fun onChoseDoNotUse(player: Player) {
        //do nothing
    }

    companion object {
        const val NAME: String = "Hamlet"
    }
}

