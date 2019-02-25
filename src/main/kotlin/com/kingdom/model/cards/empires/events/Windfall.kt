package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Windfall : EmpiresEvent(NAME, 5) {

    init {
        special = "If your deck and discard pile are empty, gain 3 Golds."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && player.deck.isEmpty() && player.cardsInDiscard.isEmpty()
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Gold(), true)
        player.gainSupplyCard(Gold(), true)
        player.gainSupplyCard(Gold(), true)
    }

    companion object {
        const val NAME: String = "Windfall"
    }
}