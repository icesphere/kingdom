package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class WorkersVillage : ProsperityCard(NAME, CardType.Action, 4) {

    init {
        addCards = 1
        addActions = 2
        addBuys = 1
        fontSize = 9
    }

    companion object {
        const val NAME: String = "Workers Village"
    }
}

