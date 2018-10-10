package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class TreasureRoom : IntrigueCard(NAME, CardType.TreasureVictory, 6) {

    init {
        testing = true
        addCoins = 2
        victoryPoints = 2
    }

    companion object {
        const val NAME: String = "Treasure Room"
    }
}

