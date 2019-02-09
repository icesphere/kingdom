package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class HumbleCastle : EmpiresCard(NAME, CardType.TreasureVictoryCastle, 3), VictoryPointsCalculator {

    init {
        addCoins = 1
        special = "Worth 1 VP per Castle you have."
        fontSize = 9
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.isCastle }
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Humble Castle"
    }
}

