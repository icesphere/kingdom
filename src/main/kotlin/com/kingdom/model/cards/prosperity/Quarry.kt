package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class Quarry : ProsperityCard(NAME, CardType.Treasure, 4) {

    //todo while in play

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        addCoins = 1
        special = "While this is in play, Action cards cost \$2 less, but not less than \$0."
    }

    companion object {
        const val NAME: String = "Quarry"
    }
}

