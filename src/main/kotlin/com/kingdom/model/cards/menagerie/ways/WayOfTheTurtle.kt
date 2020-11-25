package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class WayOfTheTurtle : MenagerieWay(NAME) {

    init {
        special = "Set this aside. If you did, play it at the start of your next turn."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return player.inPlay.contains(card)
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.removeCardInPlay(card, CardLocation.SetAside)
        player.cardsToPlayAtStartOfNextTurn.add(card)
    }

    companion object {
        const val NAME: String = "Way of the Turtle"
    }

}