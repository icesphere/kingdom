package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Palace : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 3 VP per set you have of Copper-Silver-Gold."
    }

    override fun calculatePoints(player: Player): Int {
        val coppers = player.cardCountByName(Copper.NAME)
        val silvers = player.cardCountByName(Silver.NAME)
        val golds = player.cardCountByName(Gold.NAME)

        val least = listOf(coppers, silvers, golds).min()!!

        return least * 3
    }

    companion object {
        const val NAME: String = "Palace"
    }
}