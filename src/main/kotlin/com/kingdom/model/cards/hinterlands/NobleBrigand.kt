package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class NobleBrigand : HinterlandsCard(NAME, CardType.ActionAttack, 4) {

    init {
        testing = true
        special = ""
        fontSize = 10
    }

    companion object {
        const val NAME: String = "Noble Brigand"
    }
}

