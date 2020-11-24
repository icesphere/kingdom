package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheSquirrel : MenagerieWay(NAME) {

    init {
        special = "+2 Cards at the end of this turn."
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.numExtraCardsToDrawAtEndOfTurn += 2
    }

    companion object {
        const val NAME: String = "Way of the Squirrel"
    }

}