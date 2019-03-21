package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Hideout : RenaissanceCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        addCards = 1
        addActions = 2
        special = "Trash a card from your hand. If it’s a Victory card, gain a Curse."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()
        if (card.isVictory) {
            player.gainSupplyCard(Curse(), true)
        }
    }

    companion object {
        const val NAME: String = "Hideout"
    }
}