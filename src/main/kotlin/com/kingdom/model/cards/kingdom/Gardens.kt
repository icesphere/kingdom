package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Gardens : Card() {
    init {
        name = "Gardens"
        type = CardType.Victory
        cost = 4
        special = "Worth 1 Victory for every 10 cards in your deck (rounded down)."
    }
}

