package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Silver : SupplyCard(NAME, CardType.Treasure, 3) {
    init {
        addCoins = 2
    }

    companion object {
        const val NAME: String = "Silver"
    }
}

