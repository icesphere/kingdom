package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Mine : KingdomCard(NAME, CardType.Action, 5) {
    init {
        special = "You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to \$3 more than it."
    }

    companion object {
        const val NAME: String = "Mine"
    }
}

