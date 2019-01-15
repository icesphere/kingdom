package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType

class Laboratory : BaseCard(NAME, CardType.Action, 5) {
    init {
        addCards = 2
        addActions = 1
        fontSize = 11
    }

    companion object {
        const val NAME: String = "Laboratory"
    }
}

