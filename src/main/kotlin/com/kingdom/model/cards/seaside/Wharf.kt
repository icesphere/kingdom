package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Wharf : SeasideCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    init {
        special = "Now and at the start of your next turn: +2 Cards and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.drawCards(2)
        player.addBuys(1)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
        player.addBuys(1)
        player.showInfoMessage("Gained +2 Cards and +1 Buy from ${this.cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Wharf"
    }
}

