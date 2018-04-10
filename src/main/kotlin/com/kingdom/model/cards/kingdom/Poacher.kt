package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Poacher : KingdomCard(NAME, CardType.Action, 4) {
    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard a card per empty Supply pile."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val emptySupplyPiles = player.game.emptyPiles
        if (emptySupplyPiles > 0) {
            player.discardCardsFromHand(emptySupplyPiles)
        }
    }

    companion object {
        const val NAME: String = "Poacher"
    }
}

