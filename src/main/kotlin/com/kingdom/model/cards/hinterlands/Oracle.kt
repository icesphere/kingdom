package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class Oracle : HinterlandsCard(NAME, CardType.ActionAttack, 3) {

    init {
        testing = true
        special = ""
    }

    companion object {
        const val NAME: String = "Oracle"
    }
}

