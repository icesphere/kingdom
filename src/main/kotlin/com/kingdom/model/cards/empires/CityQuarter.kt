package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class CityQuarter : EmpiresCard(NAME, CardType.Action, 0, 8) {

    init {
        addActions = 2
        special = "Reveal your hand. +1 Card per Action card revealed."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()
        player.drawCards(player.hand.count { it.isAction })
    }

    companion object {
        const val NAME: String = "City Quarter"
    }
}

