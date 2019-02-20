package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Salvager : SeasideCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        addBuys = 1
        special = "Trash a card from your hand. +\$1 per \$1 it costs."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        player.addCoins(player.getCardCostWithModifiers(card))
    }

    companion object {
        const val NAME: String = "Salvager"
    }
}

