package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Enhance : MenagerieEvent(NAME, 3), TrashCardsForBenefitActionCard {

    init {
        special = "You may trash a non-Victory card from your hand, to gain a card costing up to \$2 more than it."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.hand.any { !it.isVictory }
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Choose an non-victory card to trash from your hand, to gain a card costing up to \$2 more than it.", { !it.isVictory })
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        if (trashedCards.isNotEmpty()) {
            player.chooseSupplyCardToGainWithMaxCost(player.getCardCostWithModifiers(trashedCards[0]) + 2)
        }
    }

    companion object {
        const val NAME: String = "Enhance"
    }
}