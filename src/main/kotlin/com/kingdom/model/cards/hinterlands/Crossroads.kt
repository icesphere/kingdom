package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Crossroads : HinterlandsCard(NAME, CardType.Action, 2) {

    init {
        testing = true
        special = ""
        fontSize = 11
    }

    companion object {
        const val NAME: String = "Crossroads"
    }
}

