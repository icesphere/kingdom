package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class JackOfAllTrades : HinterlandsCard(NAME, CardType.Action, 4) {

    init {
        testing = true
        special = ""
        fontSize = 9
    }

    companion object {
        const val NAME: String = "Jack of All Trades"
    }
}

