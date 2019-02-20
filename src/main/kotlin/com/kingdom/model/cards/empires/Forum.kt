package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Forum : EmpiresCard(NAME, CardType.Action, 5), AfterCardBoughtListenerForSelf {

    init {
        addCards = 3
        addActions = 1
        special = "Discard 2 cards. When you buy this, +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(2, false)
    }

    override fun afterCardBought(player: Player) {
        player.addBuys(1)
    }

    companion object {
        const val NAME: String = "Forum"
    }
}

