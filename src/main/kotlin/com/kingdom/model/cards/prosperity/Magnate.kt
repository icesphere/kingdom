package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Magnate : ProsperityCard(NAME, CardType.Action, 5) {

    init {
        special = "Reveal your hand. +1 Card per Treasure in it."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val treasuresInHand = player.hand.count { it.isTreasure }
        player.revealHand()
        player.drawCards(treasuresInHand)
    }

    companion object {
        const val NAME: String = "Magnate"
    }
}
