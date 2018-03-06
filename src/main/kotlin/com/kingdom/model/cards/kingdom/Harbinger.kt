package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Harbinger : KingdomCard(NAME, CardType.Action, 3) {
    init {
        addCards = 1
        addActions = 1
        special = "Look through your discard pile. You may put a card from it onto your deck."
    }

    companion object {
        const val NAME: String = "Harbinger"
    }
}

