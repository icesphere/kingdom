package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class Fortress : DarkAgesCard(NAME, CardType.Action, 4), AfterCardTrashedListenerForSelf {

    init {
        addCards = 1
        addActions = 2
        special = "When you trash this, put it into your hand."
    }

    override fun afterCardTrashed(player: Player) {
        player.game.trashedCards.remove(this)
        player.addCardToHand(this, true)
    }

    companion object {
        const val NAME: String = "Fortress"
    }
}

