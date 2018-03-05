package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Moneylender : Card() {
    init {
        name = "Moneylender"
        type = CardType.Action
        cost = 4
        special = "You may trash a Copper from your hand. If you do, +\$3."
    }
}

