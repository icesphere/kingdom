package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Bazaar : SeasideCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 2
        addCoins = 1
    }

    companion object {
        const val NAME: String = "Bazaar"
    }
}

