package com.kingdom.model.cards.darkages.ruins

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.darkages.DarkAgesCard

class RuinedVillage : DarkAgesCard(NAME, CardType.ActionRuins, 0) {

    init {
        addActions = 1
    }

    companion object {
        const val NAME: String = "Ruined Village"
    }
}

