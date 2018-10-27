package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class Hoard : ProsperityCard(NAME, CardType.Treasure, 6) {

    //todo while in play

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        addCoins = 2
        special = "While this is in play, when you buy a Victory card, gain a Gold."
    }

    companion object {
        const val NAME: String = "Hoard"
    }
}

