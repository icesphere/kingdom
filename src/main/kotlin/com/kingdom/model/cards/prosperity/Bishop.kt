package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Bishop : ProsperityCard(NAME, CardType.Action, 4), TrashCardsForBenefitActionCard {

    init {
        addCoins = 1
        addVictoryCoins = 1
        special = "Trash a card from your hand. +1 VP per \$2 it costs (round down). Each other player may trash a card from their hand."
        isTrashingCard = true
        isTrashingFromHandRequiredCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. +1 VP per \$2 it costs (round down)")

        for (opponent in player.opponentsInOrder) {
            opponent.trashCardsFromHand(1, true)
        }
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        val card = trashedCards.first()

        player.addVictoryCoins(card.cost/2)
    }

    companion object {
        const val NAME: String = "Bishop"
    }
}

