package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Merchant : Card() {
    init {
        name = "Merchant"
        type = CardType.Action
        cost = 3
        addCards = 1
        addActions = 1
        special = "The first time you play a Silver this turn, +\$1."
    }
}

