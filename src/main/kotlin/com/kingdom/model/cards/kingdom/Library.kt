package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Library : KingdomCard(NAME, CardType.Action, 5) {
    init {
        special = "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards."
    }

    companion object {
        const val NAME: String = "Library"
    }
}

