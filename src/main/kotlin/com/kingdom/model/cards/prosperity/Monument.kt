package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class Monument : ProsperityCard(NAME, CardType.Action, 4) {

    init {
        addCoins = 2
        addVictoryCoins = 1
    }

    companion object {
        const val NAME: String = "Monument"
    }
}

