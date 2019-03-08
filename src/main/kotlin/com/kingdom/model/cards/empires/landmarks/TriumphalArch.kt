package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class TriumphalArch : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, 3 VP per copy you have of the 2nd most common Action card among your cards (if it’s a tie, count either)."
        fontSize = 9
    }

    override fun calculatePoints(player: Player): Int {

        val numEachActionMap = player.allCards.filter { it.isAction }.groupBy { it.name }.mapValues { it.value.size }

        if (numEachActionMap.size < 2) {
            return 0
        }

        val sortedActionsByNum = numEachActionMap.toList().sortedByDescending { (_, value) -> value }

        return sortedActionsByNum[1].second * 3
    }

    companion object {
        const val NAME: String = "Triumphal Arch"
    }
}