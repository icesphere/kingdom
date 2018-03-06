package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Duchy : SupplyCard(NAME, CardType.Victory, 5) {
    init {
        victoryPoints = 3
    }

    companion object {
        const val NAME: String = "Duchy"
    }
}

