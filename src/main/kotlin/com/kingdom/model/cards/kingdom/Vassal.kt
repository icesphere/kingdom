package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Vassal : KingdomCard(NAME, CardType.Action, 3) {
    init {
        special = "Discard the top card of your deck. If it’s an Action card, you may play it."
    }

    companion object {
        const val NAME: String = "Vassal"
    }
}

