package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Village : KingdomCard(NAME, CardType.Action, 3) {

    init {
        addCards = 1
        addActions = 2
    }

    companion object {
        const val NAME: String = "Village"
    }
}

