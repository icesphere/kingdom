package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Groundskeeper : EmpiresCard(NAME, CardType.Action, 5), AfterCardBoughtListenerForCardsInPlay {

    init {
        addCards = 1
        addActions = 1
        special = "While this is in play, when you gain a Victory card, +1 VP."
        fontSize = 9
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (card.isVictory) {
            player.addVictoryCoins(1)
            player.addEventLogWithUsername("gained 1 VP from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Groundskeeper"
    }
}

