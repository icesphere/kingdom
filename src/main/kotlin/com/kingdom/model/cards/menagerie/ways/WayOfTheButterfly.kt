package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class WayOfTheButterfly : MenagerieWay(NAME) {

    init {
        special = "You may return this to its pile to gain a card costing exactly \$1 more than it."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return !player.game.isCardNotInSupply(card) && player.game.availableCards.any { player.getCardCostWithModifiers(it) == player.getCardCostWithModifiers(card) + 1 }
    }

    override fun onUseWay(player: Player, card: Card) {
        player.removeCardInPlay(card, CardLocation.Supply)
        player.game.returnCardToSupply(card)
        player.chooseSupplyCardToGainWithExactCost(player.getCardCostWithModifiers(card) + 1)
    }

    companion object {
        const val NAME: String = "Way of the Butterfly"
    }

}