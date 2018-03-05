package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Militia : Card() {
    init {
        name = "Militia"
        type = CardType.ActionAttack
        cost = 4
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }
}

