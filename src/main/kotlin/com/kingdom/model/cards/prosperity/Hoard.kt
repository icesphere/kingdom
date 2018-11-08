package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Hoard : ProsperityCard(NAME, CardType.Treasure, 6), AfterCardBoughtListenerForCardsInPlay {

    init {
        isPlayTreasureCardsRequired = true
        addCoins = 2
        special = "While this is in play, when you buy a Victory card, gain a Gold."
    }

    override fun afterCardBought(card: Card, player: Player) {
        if (card.isVictory) {
            player.acquireFreeCardFromSupply(Gold(), true)
        }
    }

    companion object {
        const val NAME: String = "Hoard"
    }
}

