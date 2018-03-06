package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Moneylender : KingdomCard(NAME, CardType.Action, 4) {
    init {
        special = "You may trash a Copper from your hand. If you do, +\$3."
    }

    companion object {
        const val NAME: String = "Moneylender"
    }
}

