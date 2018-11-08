package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Cache : HinterlandsCard(NAME, CardType.Treasure, 5) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Cache"
    }
}

