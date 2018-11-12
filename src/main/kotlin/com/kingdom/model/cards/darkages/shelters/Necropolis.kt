package com.kingdom.model.cards.darkages.shelters

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.darkages.DarkAgesCard

class Necropolis : DarkAgesCard(NAME, CardType.ActionShelter, 1) {

    init {
        addActions = 2
    }

    companion object {
        const val NAME: String = "Necropolis"
    }
}

