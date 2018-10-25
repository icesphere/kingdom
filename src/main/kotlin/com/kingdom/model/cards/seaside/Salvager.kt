package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Salvager : SeasideCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        addBuys = 1
        special = "Trash a card from your hand. +\$1 per \$1 it costs."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, special)
    }

    override fun cardsScrapped(player: Player, scrappedCards: List<Card>) {
        val card = scrappedCards.first()

        player.addCoins(player.getCardCostWithModifiers(card))
    }

    override fun isCardApplicable(card: Card): Boolean = true

    companion object {
        const val NAME: String = "Salvager"
    }
}

