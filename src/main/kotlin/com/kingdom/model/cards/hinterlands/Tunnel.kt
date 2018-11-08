package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Tunnel : HinterlandsCard(NAME, CardType.VictoryReaction, 3) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Tunnel"
    }
}

