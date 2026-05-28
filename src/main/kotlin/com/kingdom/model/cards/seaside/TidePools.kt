package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class TidePools : SeasideCard(NAME, CardType.ActionDuration, 4), StartOfTurnDurationAction {

    init {
        addCards = 3
        addActions = 1
        special = "At the start of your next turn, discard 2 cards."
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.discardCardsFromHand(2, false)
    }

    companion object {
        const val NAME: String = "Tide Pools"
    }
}
