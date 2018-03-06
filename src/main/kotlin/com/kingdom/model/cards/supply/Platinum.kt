package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Platinum : SupplyCard(NAME, CardType.Treasure, 9) {
    init {
        addCoins = 5
    }

    companion object {
        const val NAME: String = "Platinum"
    }
}

