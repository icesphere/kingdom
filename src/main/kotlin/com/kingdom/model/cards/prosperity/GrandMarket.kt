package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType

class GrandMarket : ProsperityCard(NAME, CardType.Action, 6) {

    init {
        testing = true
        addCards = 1
        addActions = 1
        addBuys = 1
        addCoins = 2
        isPlayTreasureCardsRequired = true
        special = "You can’t buy this if you have any Coppers in play."
        fontSize = 9
        textSize = 30
    }

    companion object {
        const val NAME: String = "Grand Market"
    }
}

