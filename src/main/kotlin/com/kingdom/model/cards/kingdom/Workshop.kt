package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Workshop : KingdomCard(NAME, CardType.Action, 3) {

    init {
        special = "Gain a card costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGain(4)
    }

    companion object {
        const val NAME: String = "Workshop"
    }
}

