package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Vassal : Card() {
    init {
        name = "Vassal"
        type = CardType.Action
        cost = 3
        special = "Discard the top card of your deck. If it’s an Action card, you may play it."
    }
}

