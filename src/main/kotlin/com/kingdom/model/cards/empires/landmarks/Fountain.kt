package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Fountain : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 15 VP if you have at least 10 Coppers."
    }

    override fun calculatePoints(player: Player): Int {
        return if (player.allCards.count { it.isCopper } >= 10) 15 else 0
    }

    companion object {
        const val NAME: String = "Fountain"
    }
}