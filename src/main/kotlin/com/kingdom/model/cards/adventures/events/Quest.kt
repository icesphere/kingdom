package com.kingdom.model.cards.adventures.events

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Quest : AdventuresEvent(NAME, 0), ChoiceActionCard, DiscardCardsForBenefitActionCard {

    init {
        special = "You may discard an Attack, two Curses, or six cards. If you do, gain a Gold."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && (player.hand.any { it.isAttack } || player.hand.count { it.isCurse } >= 2 || player.hand.size >= 6)
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val choices = mutableListOf<Choice>()

        if (player.hand.any { it.isAttack }) {
            choices.add(Choice(1, "Discard Attack"))
        }

        if (player.hand.count { it.isCurse } >= 2) {
            choices.add(Choice(2, "Discard two Curses"))
        }

        if (player.hand.size >= 6) {
            choices.add(Choice(3, "Discard six cards"))
        }

        if (choices.size == 1) {
            actionChoiceMade(player, choices.first().choiceNumber, null)
        } else {
            player.makeChoiceFromList(this, choices)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {

        when(choice) {
            1 -> {
                if (player.hand.count { it.isAttack } > 1) {
                    player.discardCardsForBenefit(this, 1, "Discard an Attack card", null) { c -> c.isAttack }
                } else {
                    player.discardCardFromHand(player.hand.first { it.isAttack })
                    player.gainSupplyCard(Gold(), true)
                }
            }
            2 -> {
                player.discardCardFromHand(player.hand.first { it.isCurse })
                player.discardCardFromHand(player.hand.first { it.isCurse })
                player.gainSupplyCard(Gold(), true)
            }
            3 -> {
                if (player.hand.size > 6) {
                    player.discardCardsForBenefit(this, 6, "Discard 6 cards")
                } else {
                    player.discardHand()
                    player.gainSupplyCard(Gold(), true)
                }
            }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.gainSupplyCard(Gold(), true)
    }

    companion object {
        const val NAME: String = "Quest"
    }
}