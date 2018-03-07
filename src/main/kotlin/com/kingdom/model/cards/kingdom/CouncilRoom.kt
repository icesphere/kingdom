package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class CouncilRoom : KingdomCard(NAME, CardType.Action, 5) {
    init {
        addCards = 4
        addBuys = 1
        special = "Each other player draws a card."
    }

    companion object {
        const val NAME: String = "Council Room"
    }
}

