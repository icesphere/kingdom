package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Moat : Card() {
    init {
        name = "Moat"
        type = CardType.ActionReaction
        cost = 2
        addCards = 2
        special = "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."
    }
}

