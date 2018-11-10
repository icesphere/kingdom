package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class TradingPost : IntrigueCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        special = "Trash 2 cards from your hand. If you did, gain a Silver to your hand."
        fontSize = 10
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            when (player.hand.size) {
                1 -> player.trashCardFromHand(player.hand.first())
                2 -> {
                    player.hand.forEach { player.trashCardFromHand(it) }
                    player.gainSupplyCardToHand(Silver())
                }
                else -> {
                    player.trashCardsFromHandForBenefit(this, 2)
                }
            }
        }
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        player.gainSupplyCardToHand(Silver())
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Trading Post"
    }
}

