package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Harbinger : Card() {
    init {
        name = "Harbinger"
        type = CardType.Action
        cost = 3
        addCards = 1
        addActions = 1
        special = "Look through your discard pile. You may put a card from it onto your deck."
    }
}

