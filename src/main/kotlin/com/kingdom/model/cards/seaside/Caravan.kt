package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Caravan : SeasideCard(NAME, CardType.ActionDuration, 4), StartOfTurnDurationAction {

    init {
        addCards = 1
        addActions = 1
        special = "At the start of your next turn, +1 Card."
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCard()
        player.showInfoMessage("Gained +1 Card from ${this.cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Caravan"
    }
}

