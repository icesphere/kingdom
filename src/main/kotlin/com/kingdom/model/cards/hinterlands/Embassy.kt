package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Embassy : HinterlandsCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Embassy"
    }
}

