package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Remodel : KingdomCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than the trashed card."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }
    
    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        if (scrappedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(scrappedCards[0]) + 2)
        }
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Remodel"
    }
}

