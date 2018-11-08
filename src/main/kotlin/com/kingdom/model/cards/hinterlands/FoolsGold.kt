package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class FoolsGold : HinterlandsCard(NAME, CardType.TreasureReaction, 2) {

    init {
        testing = true
        special = ""
        fontSize = 10
    }

    companion object {
        const val NAME: String = "Fools Gold"
    }
}

