package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Astrolabe : SeasideCard(NAME, CardType.TreasureDuration, 3), StartOfTurnDurationAction {

    init {
        addBuys = 1
        addCoins = 1
        special = "At the start of your next turn: +1 Buy and +\$1."
        isAddCoinsCard = true
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addBuys(1)
        player.addCoins(1)
        player.showInfoMessage("Gained +1 Buy and +\$1 from ${cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Astrolabe"
    }
}
