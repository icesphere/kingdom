package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Oasis : HinterlandsCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Oasis"
    }
}

