package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Merchant : KingdomCard(NAME, CardType.Action, 3) {
    init {
        addCards = 1
        addActions = 1
        special = "The first time you play a Silver this turn, +\$1."
    }

    companion object {
        const val NAME: String = "Merchant"
    }
}

