package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Laboratory : KingdomCard(NAME, CardType.Action, 5) {
    init {
        addCards = 2
        addActions = 1
        fontSize = 11
    }

    companion object {
        const val NAME: String = "Laboratory"
    }
}

