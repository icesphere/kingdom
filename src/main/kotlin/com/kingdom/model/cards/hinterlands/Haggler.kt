package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.players.Player

class Haggler : HinterlandsCard(NAME, CardType.Action, 5), AfterCardBoughtListenerForCardsInPlay {

    init {
        addCoins = 2
        special = "While this is in play, when you buy a card, gain a cheaper non-Victory card."
    }

    override fun afterCardBought(card: Card, player: Player) {
        player.chooseSupplyCardToGain(player.getCardCostWithModifiers(card) - 1, { c -> !c.isVictory }, "Gain a non-Victory card from the supply")
    }

    companion object {
        const val NAME: String = "Haggler"
    }
}

