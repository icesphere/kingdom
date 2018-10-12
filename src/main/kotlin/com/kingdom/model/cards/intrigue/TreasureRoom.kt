package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType

class TreasureRoom : IntrigueCard(NAME, CardType.TreasureVictory, 6) {

    init {
        addCoins = 2
        victoryPoints = 2
        fontSize = 9
    }

    companion object {
        const val NAME: String = "Treasure Room"
    }
}

