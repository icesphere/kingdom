package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class CouncilRoom : KingdomCard(NAME, CardType.Action, 5) {
    init {
        addCards = 4
        addBuys = 1
        special = "Each other player draws a card."
        fontSize = 10
        nameLines = 2
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.opponents.forEach { it.drawCard() }
    }

    companion object {
        const val NAME: String = "Council Room"
    }
}

