package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.players.Player

class Haggler : HinterlandsCard(NAME, CardType.Action, 5), AfterCardBoughtListenerForCardsInPlay {

    init {
        testing = true
        addCoins = 2
        special = "While this is in play, when you buy a card, gain a cheaper non-Victory card."
    }

    override fun afterCardBought(card: Card, player: Player) {
        player.acquireFreeCard(player.getCardCostWithModifiers(card) - 1, { c -> !c.isVictory })
    }

    companion object {
        const val NAME: String = "Haggler"
    }
}

