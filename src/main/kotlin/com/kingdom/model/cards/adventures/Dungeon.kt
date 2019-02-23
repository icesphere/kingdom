package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Dungeon : AdventuresCard(NAME, CardType.ActionDuration, 3), StartOfTurnDurationAction {

    init {
        addActions = 1
        special = "Now and at the start of your next turn: +2 Cards, then discard 2 cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        doAction(player)
    }

    override fun durationStartOfTurnAction(player: Player) {
        doAction(player)
    }

    private fun doAction(player: Player) {
        player.drawCards(2)
        player.discardCardsFromHand(2, false)
    }

    companion object {
        const val NAME: String = "Dungeon"
    }
}

