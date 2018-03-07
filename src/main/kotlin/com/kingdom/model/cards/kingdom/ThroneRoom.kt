package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class ThroneRoom : KingdomCard(NAME, CardType.Action, 4) {
    init {
        special = "You may play an Action card from your hand twice."
    }

    companion object {
        const val NAME: String = "Throne Room"
    }
}

