package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class SilkRoad : HinterlandsCard(NAME, CardType.Victory, 4), VictoryPointsCalculator {

    init {
        special = "Worth 1 Victory Point per 4 Victory cards you have (round down)."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.isVictory } / 4
    }

    companion object {
        const val NAME: String = "Silk Road"
    }
}

