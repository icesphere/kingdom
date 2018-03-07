package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Artisan : KingdomCard(NAME, CardType.Action, 6) {
    init {
        special = "Gain a card to your hand costing up to \$5. Put a card from your hand onto your deck."
    }

    companion object {
        const val NAME: String = "Artisan"
    }
}

