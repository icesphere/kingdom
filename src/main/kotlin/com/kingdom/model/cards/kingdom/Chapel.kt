package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Chapel: KingdomCard(NAME, CardType.Action, 2) {
    init {
        special = "Trash up to 4 cards from your hand."
    }

    companion object {
        const val NAME: String = "Chapel"
    }
}