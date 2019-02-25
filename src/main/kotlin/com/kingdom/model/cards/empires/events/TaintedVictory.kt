package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.TrashCardsForBenefitActionCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class TaintedVictory : EmpiresEvent(NAME, 4), TrashCardsForBenefitActionCard {

    init {
        special = "Gain a Curse. If you do, trash a card from your hand. +1 VP per \$1 it cost."
        fontSize = 9
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.game.isCardAvailableInSupply(Curse()) && player.hand.isNotEmpty()
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Curse(), true)
        player.trashCardsFromHandForBenefit(this, 1, "Trash a card from your hand. +1 VP per \$1 it cost.")
    }

    override fun cardsTrashed(player: Player, trashedCards: List<Card>, info: Any?) {
        val card = trashedCards.first()

        val cost = player.getCardCostWithModifiers(card)

        player.addVictoryCoins(cost, true)
    }

    companion object {
        const val NAME: String = "Tainted Victory"
    }
}