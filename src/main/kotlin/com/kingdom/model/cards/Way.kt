package com.kingdom.model.cards

import com.kingdom.model.players.Player

abstract class Way(name: String,
                   deck: Deck) : Card(name, deck, CardType.Way, 0) {

    open fun isWayActionable(player: Player, card: Card): Boolean {
        return true
    }

    fun playActionAsWay(player: Player, card: Card) {
        player.addActions(-1)
        addCardBonuses(this, player)
        waySpecialAction(player, card)
    }

    abstract fun waySpecialAction(player: Player, card: Card)
}