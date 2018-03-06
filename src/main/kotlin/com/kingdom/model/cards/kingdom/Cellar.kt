package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Cellar : KingdomCard(NAME, CardType.Action, 2) {
    init {
        addActions = 1
        special = "Discard any number of cards, then draw that many."
    }

    companion object {
        const val NAME: String = "Cellar"
    }
}

