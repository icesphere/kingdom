package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class CouncilRoom : BaseCard(NAME, CardType.Action, 5) {
    init {
        addCards = 4
        addBuys = 1
        special = "Each other player draws a card."
        fontSize = 10
        nameLines = 2
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.opponentsInOrder.forEach { it.drawCard() }
    }

    companion object {
        const val NAME: String = "Council Room"
    }
}

