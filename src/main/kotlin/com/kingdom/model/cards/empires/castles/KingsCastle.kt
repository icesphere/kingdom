package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class KingsCastle : EmpiresCard(NAME, CardType.VictoryCastle, 10), VictoryPointsCalculator {

    init {
        special = "Worth 2 VP per Castle you have."
        fontSize = 10
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.isCastle } * 2
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Kings Castle"
    }
}

