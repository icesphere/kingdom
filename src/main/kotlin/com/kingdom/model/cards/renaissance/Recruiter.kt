package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Recruiter : RenaissanceCard(NAME, CardType.Action, 5), TrashCardsForBenefitActionCard {

    init {
        addCards = 2
        special = "Trash a card from your hand. +1 Villager per \$1 it costs."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val trashedCardCost = player.getCardCostWithModifiers(trashedCards.first())
        player.addVillagers(trashedCardCost)
        player.addEventLogWithUsername("gained +$trashedCardCost from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Recruiter"
    }
}