package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Market : KingdomCard(NAME, CardType.Action, 5) {
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

