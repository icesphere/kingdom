package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.DiscardCardsForBenefitActionCard
import com.kingdom.model.players.Player

class WayOfTheRat : MenagerieWay(NAME), DiscardCardsForBenefitActionCard {

    init {
        special = "You may discard a Treasure to gain a copy of this."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return !player.game.isCardNotInSupply(card) && player.game.isCardAvailableInSupply(card) && player.hand.any { it.isTreasure }
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.discardCardsForBenefit(this, 1, "Discard a Treasure", card) { it.isTreasure }
    }

    companion object {
        const val NAME: String = "Way of the Rat"
    }

    override fun cardsDiscarded(player: Player, discardedCards: List<Card>, info: Any?) {
        val card = info as Card
        player.gainSupplyCard(card, true)
    }

}