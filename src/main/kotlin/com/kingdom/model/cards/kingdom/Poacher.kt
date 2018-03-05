package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Poacher : Card() {
    init {
        name = "Poacher"
        type = CardType.Action
        cost = 4
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard a card per empty Supply pile."
    }
}

