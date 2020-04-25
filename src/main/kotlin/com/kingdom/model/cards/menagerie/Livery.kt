package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForCardsInPlay
import com.kingdom.model.players.Player

class Livery : MenagerieCard(NAME, CardType.Action, 5), AfterCardGainedListenerForCardsInPlay, UsesHorses {

    init {
        addCoins = 3
        special = "This turn, when you gain a card costing \$4 or more, gain a Horse."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (player.getCardCostWithModifiers(card) >= 4) {
            player.gainHorse()
        }
    }

    companion object {
        const val NAME: String = "Livery"
    }
}

