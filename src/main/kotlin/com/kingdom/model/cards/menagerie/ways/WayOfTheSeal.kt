package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

class WayOfTheSeal : MenagerieWay(NAME) {

    init {
        addCoins = 1
        special = "This turn, when you gain a card, you may put it onto your deck."
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.numCardGainedMayPutOnTopOfDeck++
    }

    companion object {
        const val NAME: String = "Way of the Seal"
    }

}