package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Tower : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 1 VP per non-Victory card you have from an empty Supply pile."
    }

    override fun calculatePoints(player: Player): Int {

        val emptyPileNames = player.game.emptyPileNames

        return player.allCards.filter { !it.isVictory && emptyPileNames.contains(it.name) }.size
    }

    companion object {
        const val NAME: String = "Tower"
    }
}