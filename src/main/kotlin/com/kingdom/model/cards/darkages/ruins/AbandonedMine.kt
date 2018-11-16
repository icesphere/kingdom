package com.kingdom.model.cards.darkages.ruins

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.darkages.DarkAgesCard

class AbandonedMine : DarkAgesCard(NAME, CardType.ActionRuins, 0) {

    init {
        addCoins = 1
        fontSize = 9
    }

    companion object {
        const val NAME: String = "Abandoned Mine"
    }
}

