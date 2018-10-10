package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Colony : SupplyCard(NAME, CardType.Victory, 11) {

    init {
        victoryPoints = 10
    }

    companion object {
        const val NAME: String = "Colony"
    }
}

