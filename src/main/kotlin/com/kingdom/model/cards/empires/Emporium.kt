package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Emporium : EmpiresCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf {

    init {
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "When you gain this, if you have at least 5 Action cards in play, +2 VP. (Emporium is the bottom half of the Patrician pile.)"
        textSize = 63
    }

    override val pileName: String
        get() = Patrician.NAME

    override fun afterCardGained(player: Player) {
        if (player.inPlay.count { it.isAction } >= 5) {
            player.addVictoryCoins(2)
            player.showInfoMessage("You gained +2 VP")
            player.addEventLogWithUsername("gained +2 VP from $cardNameWithBackgroundColor for gaining it with 5 or more Action cards in play")
        }
    }

    companion object {
        const val NAME: String = "Emporium"
    }
}

