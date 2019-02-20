package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Mine : BaseCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        special = "You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to \$3 more than it."
        fontSize = 13
        isTrashingCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.optionallyTrashCardsFromHandForBenefit(this, 1, special, { c -> c.isTreasure })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainToHandWithMaxCostAndType(player.getCardCostWithModifiers(trashedCards[0]) + 3, CardType.Treasure)
        }
    }

    companion object {
        const val NAME: String = "Mine"
    }
}

