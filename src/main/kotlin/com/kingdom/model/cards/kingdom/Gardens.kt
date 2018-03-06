package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Gardens : KingdomCard(NAME, CardType.Victory, 4) {
    init {
        special = "Worth 1 Victory for every 10 cards in your deck (rounded down)."
    }

    companion object {
        const val NAME: String = "Gardens"
    }
}

