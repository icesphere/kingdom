package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForLandmark
import com.kingdom.model.players.Player

class Tomb : EmpiresLandmark(NAME), AfterCardTrashedListenerForLandmark {

    init {
        special = "When you trash a card, +1 VP."
    }

    override fun afterCardTrashed(card: Card, player: Player) {
        player.addVictoryCoins(1)
        player.addEventLogWithUsername("gained 1 VP from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Tomb"
    }
}