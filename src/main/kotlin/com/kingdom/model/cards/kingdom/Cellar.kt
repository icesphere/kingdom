package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Cellar : Card() {
    init {
        name = "Cellar"
        type = CardType.Action
        cost = 2
        addActions = 1
        special = "Discard any number of cards, then draw that many."
    }
}

