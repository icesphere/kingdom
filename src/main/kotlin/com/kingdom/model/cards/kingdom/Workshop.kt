package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Workshop : Card() {
    init {
        name = "Workshop"
        type = CardType.Action
        cost = 3
        special = "Gain a card costing up to \$4."
    }
}

