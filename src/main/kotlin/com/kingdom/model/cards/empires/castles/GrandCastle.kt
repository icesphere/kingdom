package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class GrandCastle : EmpiresCard(NAME, CardType.VictoryCastle, 9), AfterCardGainedListenerForSelf {

    init {
        victoryPoints = 5
        special = "When you gain this, reveal your hand. +1 VP per Victory card in your hand and/or in play."
        fontSize = 10
        textSize = 73
    }

    override fun afterCardGained(player: Player) {
        player.revealHand()
        val numVictoryCards = (player.hand + player.inPlay).count { it.isVictory }
        if (numVictoryCards > 0) {
            player.addVictoryCoins(numVictoryCards)
            player.addEventLogWithUsername("gained +$numVictoryCards VP from $cardNameWithBackgroundColor")
            player.showInfoMessage("You gained +$numVictoryCards VP from $cardNameWithBackgroundColor")
        }
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Grand Castle"
    }
}

