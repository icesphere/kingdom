package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Orchard : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 4 VP per differently named Action card you have 3 or more copies of."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.distinctBy { it.name }
                .filter { it.isAction && player.cardCountByName(it.name) >= 3  }
                .size * 4
    }

    companion object {
        const val NAME: String = "Orchard"
    }
}