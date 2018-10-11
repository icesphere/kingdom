package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Duchy
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Duke : IntrigueCard(NAME, CardType.Victory, 5), VictoryPointsCalculator {

    init {
        testing = true
        special = "Worth 1 Victory Point per Duchy you have."
    }

    override fun calculatePoints(player: Player): Int {
        return player.cardCountByName(Duchy.NAME)
    }

    companion object {
        const val NAME: String = "Duke"
    }
}

