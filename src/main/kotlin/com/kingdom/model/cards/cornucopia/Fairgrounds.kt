package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Fairgrounds : CornucopiaCard(NAME, CardType.Victory, 6), VictoryPointsCalculator {

    init {
        special = "Worth 2VP for every 5 differently named cards in your deck (round down)."
        fontSize = 11
    }

    override fun calculatePoints(player: Player): Int {
        return (player.allCards.groupBy { it.name }.size / 5) * 2
    }

    companion object {
        const val NAME: String = "Fairgrounds"
    }
}

