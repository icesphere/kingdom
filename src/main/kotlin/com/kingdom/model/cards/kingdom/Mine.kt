package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Mine : KingdomCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        special = "You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to \$3 more than it."
        fontSize = 13
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special, { c -> c.isTreasure })
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        if (scrappedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainToHandWithMaxCostAndType(player.getCardCostWithModifiers(scrappedCards[0]) + 3, CardType.Treasure)
        }
    }

    companion object {
        const val NAME: String = "Mine"
    }
}

