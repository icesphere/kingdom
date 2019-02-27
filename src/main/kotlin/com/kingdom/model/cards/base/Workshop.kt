package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Workshop : BaseCard(NAME, CardType.Action, 3) {

    init {
        special = "Gain a card costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainWithMaxCost(4)
    }

    companion object {
        const val NAME: String = "Workshop"
    }
}

