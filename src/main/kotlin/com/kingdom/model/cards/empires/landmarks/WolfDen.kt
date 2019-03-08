package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class WolfDen : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, -3 VP per card you have exactly one copy of."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.groupBy { it.name }.filterValues { it.size == 1 }.size * -3
    }

    companion object {
        const val NAME: String = "Wolf Den"
    }
}