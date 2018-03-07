package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType

class Bandit : KingdomCard(NAME, CardType.ActionAttack, 5) {
    init {
        special = "Gain a Gold. Each other player reveals the top two cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."
    }

    companion object {
        const val NAME: String = "Bandit"
    }
}

