package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Sentry : KingdomCard(NAME, CardType.Action, 5) {
    init {
        addCards = 1
        addActions = 1
        special = "Look at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order."
        textSize = 70
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Sentry"
    }
}

