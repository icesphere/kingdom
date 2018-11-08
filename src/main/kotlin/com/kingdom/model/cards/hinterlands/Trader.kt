package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Trader : HinterlandsCard(NAME, CardType.ActionReaction, 4) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Trader"
    }
}

