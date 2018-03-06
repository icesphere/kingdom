package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType

class Curse : SupplyCard(NAME, CardType.Curse, 0) {
    init {
        victoryPoints = -1
    }

    companion object {
        const val NAME: String = "Curse"
    }
}

