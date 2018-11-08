package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class NomadCamp : HinterlandsCard(NAME, CardType.Action, 4) {

    init {
        testing = true
        special = ""
        fontSize = 11
    }

    companion object {
        const val NAME: String = "Nomad Camp"
    }
}

