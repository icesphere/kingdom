package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Cartographer : HinterlandsCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        special = ""
        fontSize = 11
    }

    companion object {
        const val NAME: String = "Cartographer"
    }
}

