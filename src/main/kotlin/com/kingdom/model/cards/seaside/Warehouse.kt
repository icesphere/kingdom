package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Warehouse : SeasideCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addCards = 3
        addActions = 1
        special = "Discard 3 cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(3, false)
    }

    companion object {
        const val NAME: String = "Warehouse"
    }
}

