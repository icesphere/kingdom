package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class SnowyVillage : MenagerieCard(NAME, CardType.Action, 3) {

    init {
        addCards = 1
        addActions = 4
        addBuys = 1
        special = "Ignore any further +Actions you get this turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.ignoreAddActionsUntilEndOfTurn = true
    }

    companion object {
        const val NAME: String = "Snowy Village"
    }
}

