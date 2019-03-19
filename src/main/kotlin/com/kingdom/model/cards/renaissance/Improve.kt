package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType

class Improve : RenaissanceCard(NAME, CardType.Action, 3) {

    //todo figure out how to do this one

    init {
        disabled = true
        addCoins = 2
        special = "At the start of Clean-up, you may trash an Action card you would discard from play this turn, to gain a card costing exactly \$1 more than it."
    }

    companion object {
        const val NAME: String = "Improve"
    }
}