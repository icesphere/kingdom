package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheMole : MenagerieWay(NAME) {

    init {
        addActions = 1
        special = "Discard your hand. +3 Cards."
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.discardHand()
        player.drawCards(3)
    }

    companion object {
        const val NAME: String = "Way of the Mole"
    }

}