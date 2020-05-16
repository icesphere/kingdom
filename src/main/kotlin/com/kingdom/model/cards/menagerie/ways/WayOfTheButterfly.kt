package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheButterfly : MenagerieWay(NAME) {

    init {
        special = "You may return this to its pile to gain a card costing exactly \$1 more than it."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return !player.game.isCardNotInSupply(card)
    }

    override fun onUseWay(player: Player, card: Card) {

    }

    companion object {
        const val NAME: String = "Way of the Butterfly"
    }

}