package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Poacher : BaseCard(NAME, CardType.Action, 4) {
    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard a card per empty Supply pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val emptySupplyPiles = player.game.emptyPiles
        if (emptySupplyPiles > 0) {
            player.discardCardsFromHand(emptySupplyPiles, false)
        }
    }

    companion object {
        const val NAME: String = "Poacher"
    }
}

