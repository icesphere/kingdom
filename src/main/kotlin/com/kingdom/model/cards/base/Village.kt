package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType

class Village : BaseCard(NAME, CardType.Action, 3) {

    init {
        addCards = 1
        addActions = 2
    }

    companion object {
        const val NAME: String = "Village"
    }
}

