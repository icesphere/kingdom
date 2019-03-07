package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Museum : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 2 VP per differently named card you have."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.distinctBy { it.name }.size * 2
    }

    companion object {
        const val NAME: String = "Museum"
    }
}