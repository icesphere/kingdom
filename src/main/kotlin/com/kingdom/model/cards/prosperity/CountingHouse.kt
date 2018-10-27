package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class CountingHouse : ProsperityCard(NAME, CardType.Action, 5) {

    init {
        testing = true
        special = "Look through your discard pile, reveal any number of Coppers from it, and put them into your hand."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Counting House"
    }
}

