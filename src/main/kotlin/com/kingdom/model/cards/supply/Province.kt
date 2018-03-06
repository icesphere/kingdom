package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Province : SupplyCard(NAME, CardType.Victory, 8) {
    init {
        victoryPoints = 6
    }

    companion object {
        const val NAME: String = "Province"
    }
}

