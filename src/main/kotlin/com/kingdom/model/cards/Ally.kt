package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Ally(name: String, deck: Deck, val favorCost: Int) : Card(name, deck, CardType.Ally, 0) {

    open fun isAllyActionable(player: Player): Boolean {
        return favorCost > 0 && player.favors >= favorCost
    }

    open fun useAlly(player: Player) {
        if (favorCost > 0 && !player.spendFavors(favorCost)) {
            return
        }
        allySpecialAction(player)
    }

    abstract fun allySpecialAction(player: Player)
}
