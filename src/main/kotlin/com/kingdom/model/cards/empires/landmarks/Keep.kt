package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Keep : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 5 VP per differently named Treasure you have, that you have more copies of than each other player, or tied for most."
    }

    override fun calculatePoints(player: Player): Int {
        val treasures = player.allCards.filter { it.isTreasure }.distinctBy { it.name }

        return treasures.sumBy { treasure ->
            if (player.opponents.none { getTreasureCount(it, treasure) > getTreasureCount(player, treasure) } ) 5 else 0
        }
    }

    private fun getTreasureCount(player: Player, treasure: Card): Int {
        return player.allCards.count { it.name == treasure.name }
    }

    companion object {
        const val NAME: String = "Keep"
    }
}