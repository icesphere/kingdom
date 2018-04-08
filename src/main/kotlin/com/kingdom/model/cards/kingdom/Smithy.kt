package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Smithy : KingdomCard(NAME, CardType.Action, 4) {

    init {
        addCards = 3
    }

    companion object {
        const val NAME: String = "Smithy"
    }
}

