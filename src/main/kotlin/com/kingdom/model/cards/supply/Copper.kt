package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Copper : SupplyCard(NAME, CardType.Treasure, 0) {
    init {
        addCoins = 1
    }

    companion object {
        const val NAME: String = "Copper"
    }
}

