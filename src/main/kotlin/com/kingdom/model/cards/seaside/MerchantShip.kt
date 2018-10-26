package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class MerchantShip : SeasideCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    init {
        special = "Now and at the start of your next turn: +\$2."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins(2)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(2)
    }

    companion object {
        const val NAME: String = "Merchant Ship"
    }
}

