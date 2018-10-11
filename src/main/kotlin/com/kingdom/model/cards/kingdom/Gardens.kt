package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Gardens : KingdomCard(NAME, CardType.Victory, 4), VictoryPointsCalculator {

    init {
        special = "Worth 1 Victory for every 10 cards in your deck (rounded down)."
    }

    companion object {
        const val NAME: String = "Gardens"
    }

    override fun calculatePoints(player: Player): Int {
        val totalCards = player.numCards
        val totalGardens = player.cardCountByName(Gardens.NAME)

        if (totalCards < 10 || totalGardens == 0) {
            return 0
        }

        return totalCards/10
    }
}

