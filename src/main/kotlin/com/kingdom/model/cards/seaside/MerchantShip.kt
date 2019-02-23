package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class MerchantShip : SeasideCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    init {
        special = "Now and at the start of your next turn: +\$2."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins(2)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(2)
        player.showInfoMessage("Gained +\$2 from ${this.cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Merchant Ship"
    }
}

