package com.kingdom.model.cards.menagerie.ways

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.players.Player

class WayOfTheHorse : MenagerieWay(NAME) {

    init {
        addCards = 2
        addActions = 1
        special = "Return this to its pile."
    }

    override fun isWayActionable(player: Player, card: Card): Boolean {
        return !player.game.isCardNotInSupply(card)
    }

    override fun waySpecialAction(player: Player, card: Card) {
        player.removeCardInPlay(card, CardLocation.Supply)
        player.game.returnCardToSupply(card)
    }

    companion object {
        const val NAME: String = "Way of the Horse"
    }

}