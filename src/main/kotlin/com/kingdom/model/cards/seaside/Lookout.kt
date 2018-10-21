package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Lookout : SeasideCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addActions = 1
        special = "Look at the top 3 cards of your deck. Trash one of them. Discard one of them. Put the other one back on to your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Lookout"
    }
}

