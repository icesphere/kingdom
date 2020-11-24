package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheOwl : MenagerieWay(NAME) {

    init {
        special = "Draw until you have 6 cards in hand."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return player.hand.size < 6
    }

    override fun waySpecialAction(player: Player, card: Card) {
        if (player.hand.size < 6) {
            player.drawCards(6 - player.hand.size)
        }
    }

    companion object {
        const val NAME: String = "Way of the Owl"
    }

}