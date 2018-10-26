package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.players.Player

class Tactician : SeasideCard(NAME, CardType.ActionDuration, 5), StartOfTurnDurationAction {

    var gainBonus: Boolean = false

    init {
        special = "If you have at least one card in hand, discard your hand, and at the start of your next turn, +5 Cards, +1 Action, and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.discardHand()
            gainBonus = true
        } else {
            gainBonus = false
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        if (gainBonus) {
            player.drawCards(5)
            player.addActions(1)
            player.addBuys(1)
        }
    }

    companion object {
        const val NAME: String = "Tactician"
    }
}

