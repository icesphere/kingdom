package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.kingdom.KingdomCard
import com.kingdom.model.players.Player

class Courtyard : IntrigueCard(NAME, CardType.Action, 2) {
    init {
        testing = true
        addCards = 3
        special = "Put a card from your hand onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCardFromHandToTopOfDeck()
    }

    companion object {
        const val NAME: String = "Courtyard"
    }
}

