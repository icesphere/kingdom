package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Gold : SupplyCard(NAME, CardType.Treasure, 6) {
    init {
        addCoins = 3
    }

    companion object {
        const val NAME: String = "Gold"
    }
}

