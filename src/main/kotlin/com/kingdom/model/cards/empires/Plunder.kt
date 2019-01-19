package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType

class Plunder : EmpiresCard(NAME, CardType.Treasure, 5) {

    init {
        addCoins = 2
        addVictoryCoins = 1
        special = "(Plunder is the bottom half of the Encampment pile.)"
    }

    override val pileName: String
        get() = Encampment.NAME

    companion object {
        const val NAME: String = "Plunder"
    }
}

