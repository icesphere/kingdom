package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Poacher : KingdomCard(NAME, CardType.Action, 4) {
    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard a card per empty Supply pile."
    }

    companion object {
        const val NAME: String = "Poacher"
    }
}

