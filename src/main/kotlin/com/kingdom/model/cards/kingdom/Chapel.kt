package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Chapel: Card() {
    init {
        name = "Chapel"
        type = CardType.Action
        cost = 2
        special = "Trash up to 4 cards from your hand."
    }
}