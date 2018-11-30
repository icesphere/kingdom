package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.CardType

class CandlestickMaker : GuildsCard(NAME, CardType.Action, 2) {

    init {
        addActions = 1
        addBuys = 1
        addCoffers = 1
        nameLines = 2
        fontSize = 9
    }

    companion object {
        const val NAME: String = "Candlestick Maker"
    }
}

