package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Potion : SupplyCard(NAME, CardType.Treasure, 4) {

    companion object {
        const val NAME: String = "Potion"
    }
}

