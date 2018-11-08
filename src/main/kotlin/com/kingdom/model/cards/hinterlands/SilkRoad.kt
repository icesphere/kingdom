package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class SilkRoad : HinterlandsCard(NAME, CardType.Victory, 4), VictoryPointsCalculator {

    init {
        special = ""
    }

    override fun calculatePoints(player: Player): Int {
        return 0 //todo
    }

    companion object {
        const val NAME: String = "Silk Road"
    }
}

