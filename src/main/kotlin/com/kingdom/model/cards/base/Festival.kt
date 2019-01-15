package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType

class Festival : BaseCard(NAME, CardType.Action, 5) {

    init {
        addActions = 2
        addBuys = 1
        addCoins = 2
    }

    companion object {
        const val NAME: String = "Festival"
    }
}

