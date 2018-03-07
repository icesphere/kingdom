package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Witch : KingdomCard(NAME, CardType.ActionAttack, 5) {
    init {
        addCards = 2
        special = "Each other player gains a Curse card."
    }

    companion object {
        const val NAME: String = "Witch"
    }
}

