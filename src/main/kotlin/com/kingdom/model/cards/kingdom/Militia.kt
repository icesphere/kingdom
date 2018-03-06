package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Militia : KingdomCard(NAME, CardType.ActionAttack, 4) {
    init {
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }

    companion object {
        const val NAME: String = "Militia"
    }
}

