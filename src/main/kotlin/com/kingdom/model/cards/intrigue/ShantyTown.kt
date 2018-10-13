package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class ShantyTown : IntrigueCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addActions = 2
        special = "Reveal your hand. If you have no Action cards in hand, +2 Cards."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()
        if (player.hand.count { it.isAction } == 0) {
            player.addGameLog("${player.username} gained +2 cards from ${this.cardNameWithBackgroundColor}")
            player.drawCards(2)
        }
    }

    companion object {
        const val NAME: String = "Shanty Town"
    }
}

