package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class BanditFort : EmpiresLandmark(NAME), VictoryPointsCalculator {

    init {
        special = "When scoring, -2 VP for each Silver and each Gold you have."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.isSilver || it.isGold } * -2
    }

    companion object {
        const val NAME: String = "Bandit Fort"
    }
}