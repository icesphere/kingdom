package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.players.Player

class Transmogrify : AdventuresCard(NAME, CardType.ActionReserve, 4), TavernCard, TrashCardsForBenefitActionCard {

    init {
        addActions = 1
        special = "Put this on your Tavern mat. At the start of your turn, you may call this, to trash a card from your hand, and gain a card to your hand costing up to \$1 more than it."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        return player.isStartOfTurn && player.hand.isNotEmpty()
    }

    override fun onTavernCardCalled(player: Player) {
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand, and gain a card to your hand costing up to \$1 more than it")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>) {
        val card = trashedCards.first()

        if (player.game.availableCards.any { player.getCardCostWithModifiers(it) <= player.getCardCostWithModifiers(card) + 1 }) {
            player.chooseSupplyCardToGainToHandWithMaxCost(player.getCardCostWithModifiers(card) + 1)
        }
    }

    companion object {
        const val NAME: String = "Transmogrify"
    }
}

