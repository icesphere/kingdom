package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Event(name: String,
                     deck: Deck,
                     cost: Int,
                     private val isOncePerTurnEvent: Boolean = false) : Card(name, deck, CardType.Event, cost) {

    open fun isEventActionable(player: Player): Boolean {
        return player.buys > 0 && player.debt == 0 && player.availableCoins >= this.cost && (!this.isOncePerTurnEvent || player.eventsBought.none { it.name == this.name })
    }
}