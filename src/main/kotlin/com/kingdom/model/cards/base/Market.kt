package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType

class Market : BaseCard(NAME, CardType.Action, 5) {
    init {
        addCards = 1
        addActions = 1
        addBuys = 1
        addCoins = 1
    }

    companion object {
        const val NAME: String = "Market"
    }
}

