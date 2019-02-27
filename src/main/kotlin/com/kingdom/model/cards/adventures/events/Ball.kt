package com.kingdom.model.cards.adventures.events

import com.kingdom.model.players.Player

class Ball : AdventuresEvent(NAME, 5) {

    init {
        special = "Take your -\$1 token. Gain 2 cards each costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isMinusCoinTokenInFrontOfPlayer = true
        player.chooseSupplyCardToGainWithMaxCost(4)
        player.chooseSupplyCardToGainWithMaxCost(4)
    }

    companion object {
        const val NAME: String = "Ball"
    }
}