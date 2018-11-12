package com.kingdom.model.cards.darkages.ruins

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.darkages.DarkAgesCard

class RuinedMarket : DarkAgesCard(NAME, CardType.ActionRuins, 0) {

    init {
        addBuys = 1
    }

    companion object {
        const val NAME: String = "Ruined Market"
    }
}

