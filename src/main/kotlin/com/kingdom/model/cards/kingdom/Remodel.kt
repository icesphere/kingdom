package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Remodel : KingdomCard(NAME, CardType.Action, 4) {
    init {
        special = "Trash a card from your hand. Gain a card costing up to \$2 more than the trashed card."
    }

    companion object {
        const val NAME: String = "Remodel"
    }
}

