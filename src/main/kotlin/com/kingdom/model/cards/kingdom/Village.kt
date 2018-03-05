package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Village : Card() {
    init {
        name = "Village"
        type = CardType.Action
        cost = 3
        addCards = 1
        addActions = 2
    }
}

