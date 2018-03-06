package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Moat : KingdomCard(NAME, CardType.ActionReaction, 2) {
    init {
        addCards = 2
        special = "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."
    }

    companion object {
        const val NAME: String = "Moat"
    }
}

