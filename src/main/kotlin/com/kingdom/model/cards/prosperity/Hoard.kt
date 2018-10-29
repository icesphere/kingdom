package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.CardBoughtListenerForCardsInPlay
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Hoard : ProsperityCard(NAME, CardType.Treasure, 6), CardBoughtListenerForCardsInPlay {

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        addCoins = 2
        special = "While this is in play, when you buy a Victory card, gain a Gold."
    }

    override fun onCardBought(card: Card, player: Player): Boolean {
        if (card.isVictory) {
            player.acquireFreeCardFromSupply(Gold(), true)
        }
        return false
    }

    companion object {
        const val NAME: String = "Hoard"
    }
}

