package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType

class BorderGuard : RenaissanceCard(NAME, CardType.Action, 2) {

    init {
        special = "Reveal the top 2 cards of your deck. Put one into your hand and discard the other. If both were Actions, take the Lantern or Horn."
    }

    companion object {
        const val NAME: String = "Border Guard"
    }
}