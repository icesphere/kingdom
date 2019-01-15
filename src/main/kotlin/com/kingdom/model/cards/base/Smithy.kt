package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType

class Smithy : BaseCard(NAME, CardType.Action, 4) {

    init {
        addCards = 3
    }

    companion object {
        const val NAME: String = "Smithy"
    }
}

