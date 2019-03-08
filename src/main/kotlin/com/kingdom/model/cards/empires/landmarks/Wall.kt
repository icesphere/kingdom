package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Wall : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, -1 VP per card you have after the first 15."
    }

    override fun calculatePoints(player: Player): Int {
        val cardCount = player.allCards.size
        return if (cardCount >= 15) (cardCount - 15) * -1 else 0
    }

    companion object {
        const val NAME: String = "Wall"
    }
}