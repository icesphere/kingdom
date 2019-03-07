package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class City : ProsperityCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 2
        special = "If there are one or more empty Supply piles, +1 Card. If there are two or more, +\$1 and +1 Buy."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val emptyPiles = player.game.numEmptyPiles

        if (emptyPiles >= 1) {
            player.drawCard()
        }

        if (emptyPiles >= 2) {
            player.addCoins(1)
            player.addBuys(1)
        }
    }

    companion object {
        const val NAME: String = "City"
    }
}

