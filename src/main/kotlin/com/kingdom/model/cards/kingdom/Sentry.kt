package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Sentry : KingdomCard(NAME, CardType.Action, 5) {
    init {
        addCards = 1
        addActions = 1
        special = "Look at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order."
    }

    companion object {
        const val NAME: String = "Sentry"
    }
}

