package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class FishingVillage : SeasideCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction {

    init {
        addActions = 2
        addCoins = 1
        special = "At the start of your next turn: +1 Action and +\$1."
        fontSize = 9
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addActions(1)
        player.addCoins(1)
    }

    companion object {
        const val NAME: String = "Fishing Village"
    }
}

