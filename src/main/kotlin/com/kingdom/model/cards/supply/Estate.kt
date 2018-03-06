package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Estate : SupplyCard(NAME, CardType.Victory, 2) {
    init {
        victoryPoints = 1
    }

    companion object {
        const val NAME: String = "Estate"
    }
}

