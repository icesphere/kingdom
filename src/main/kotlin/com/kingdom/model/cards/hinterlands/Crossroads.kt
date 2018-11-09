package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Crossroads : HinterlandsCard(NAME, CardType.Action, 2) {

    init {
        special = "Reveal your hand. +1 Card per Victory card revealed. If this is the first time you played a Crossroads this turn, +3 Actions."
        fontSize = 11
    }

    override val isTerminalAction: Boolean = false

    override fun cardPlayedSpecialAction(player: Player) {
        player.revealHand()
        player.drawCards(player.hand.count { it.isVictory })
        if (!player.playedCrossroadsThisTurn) {
            player.addActions(3)
        }
        player.playedCrossroadsThisTurn = true
    }

    companion object {
        const val NAME: String = "Crossroads"
    }
}

