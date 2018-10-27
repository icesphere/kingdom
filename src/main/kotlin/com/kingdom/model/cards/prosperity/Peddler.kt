package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class Peddler : ProsperityCard(NAME, CardType.Action, 8) {

    //todo

    init {
        testing = true
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "During your Buy phase, this costs \$2 less per Action card you have in play, but not less than \$0."
        textSize = 65
    }

    companion object {
        const val NAME: String = "Peddler"
    }
}

