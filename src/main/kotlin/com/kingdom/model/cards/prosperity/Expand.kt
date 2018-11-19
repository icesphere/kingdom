package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Expand : ProsperityCard(NAME, CardType.Action, 7), TrashCardsForBenefitActionCard {

    init {
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
        isTrashingFromHandToUpgradeCard = true
        special = "Trash a card from your hand. Gain a card costing up to \$3 more than it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()
        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(card) + 3)
    }

    companion object {
        const val NAME: String = "Expand"
    }
}

