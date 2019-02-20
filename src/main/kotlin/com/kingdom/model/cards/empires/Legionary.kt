package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Legionary : EmpiresCard(NAME, CardType.ActionAttack, 5), ChoiceActionCard, AttackCard, DiscardCardsForBenefitActionCard {

    init {
        addCoins = 3
        special = "You may reveal a Gold from your hand. If you do, each other player discards down to 2 cards in hand, then draws a card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        if (player.hand.any { it.isGold }) {
            player.yesNoChoice(this, "Reveal ${Gold().cardNameWithBackgroundColor} to have each other player discard down to 2 cards in hand, then draw a card?", affectedOpponents)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("revealed a ${Gold().cardNameWithBackgroundColor} from their hand")

            @Suppress("UNCHECKED_CAST")
            val affectedOpponents = info as List<Player>

            for (opponent in affectedOpponents) {
                if (opponent.hand.size > 2) {
                    opponent.discardCardsForBenefit(this, opponent.hand.size - 2, "Discard down to 2 cards in hand, then you will draw a card")
                } else {
                    opponent.drawCard()
                }
            }
        }
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        player.drawCard()
    }

    companion object {
        const val NAME: String = "Legionary"
    }
}

