package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.CardPlayedListenerForCardsInSupply
import com.kingdom.model.players.Player

class GrandMarket : ProsperityCard(NAME, CardType.Action, 6), CardPlayedListenerForCardsInSupply {

    init {
        addCards = 1
        addActions = 1
        addBuys = 1
        addCoins = 2
        isPlayTreasureCardsRequired = true
        special = "You can’t buy this if you have any Coppers in play."
        fontSize = 9
        textSize = 30
    }

    override fun onCardPlayed(card: Card, player: Player) {
        if (card.isCopper) {
            player.cardsUnavailableToBuyThisTurn.add(this)
        }
    }

    companion object {
        const val NAME: String = "Grand Market"
    }
}

