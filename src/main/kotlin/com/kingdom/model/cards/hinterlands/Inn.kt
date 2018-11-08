package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Inn : HinterlandsCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf {

    init {
        testing = true
        addCards = 2
        addActions = 2
        special = "Discard 2 cards. When you gain this, look through your discard pile, reveal any number of Action cards from it (which can include this), and shuffle them into your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(2, false)
    }

    override fun afterCardGained(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Inn"
    }
}

