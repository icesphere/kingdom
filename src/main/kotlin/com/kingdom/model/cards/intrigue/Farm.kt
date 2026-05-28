package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType

class Farm : IntrigueCard(NAME, CardType.TreasureVictory, 6) {

    init {
        addCoins = 2
        victoryPoints = 2
    }

    companion object {
        const val NAME: String = "Farm"
    }
}

