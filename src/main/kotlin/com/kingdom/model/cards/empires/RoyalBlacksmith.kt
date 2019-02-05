package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class RoyalBlacksmith : EmpiresCard(NAME, CardType.Action, 0, 8) {

    init {
        addCards = 5
        special = "Reveal your hand; discard the Coppers."
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()
        val numCoppersInHand = player.hand.count { it.isCopper }
        if (numCoppersInHand > 0) {
            player.hand.filter { it.isCopper }.forEach { player.discardCardFromHand(it, false) }
            player.addEventLogWithUsername("discarded $numCoppersInHand ${Copper().cardNameWithBackgroundColor}")
        }
    }

    companion object {
        const val NAME: String = "Royal Blacksmith"
    }
}

