package com.kingdom.model.cards.adventures.events

import com.kingdom.model.players.Player

class Borrow : AdventuresEvent(NAME, 0, true) {

    init {
        special = "Once per turn: +1 Buy. If your -1 Card token isn’t on your deck, put it there and +\$1."
    }

    override fun isEventActionable(player: Player): Boolean {
        return super.isEventActionable(player) && !player.isMinusCardTokenOnDeck
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addBuys(1)
        player.isMinusCardTokenOnDeck = true
        player.refreshPlayerHandArea()
        player.addCoins(1)
    }

    companion object {
        const val NAME: String = "Borrow"
    }
}