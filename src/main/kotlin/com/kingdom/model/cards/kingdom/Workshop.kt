package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Workshop : KingdomCard(NAME, CardType.Action, 3) {
    init {
        special = "Gain a card costing up to \$4."
    }

    companion object {
        const val NAME: String = "Workshop"
    }
}

