package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Collection : ProsperityCard(NAME, CardType.Treasure, 5), AfterCardGainedListenerForCardsInPlay {

    init {
        addCoins = 2
        special = "This turn, when you gain an Action card, +1 VP."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAction) {
            player.addVictoryCoins(1)
            player.addEventLogWithUsername("gained +1 VP from ${this.cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Collection"
    }
}
