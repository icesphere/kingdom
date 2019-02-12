package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Fortune : EmpiresCard(NAME, CardType.Treasure, 8, 8), AfterCardGainedListenerForSelf {

    init {
        addBuys = 1
        special = "When you play this, double your \$ if you haven’t yet this turn. When you gain this, gain a Gold per Gladiator you have in play. (Fortune is the bottom half of the Gladiator pile.)"
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (!player.isMoneyDoubledThisTurn) {
            player.doubleMoney()
        } else {
            player.showInfoMessage("Money was already doubled this turn")
        }
    }

    override fun afterCardGained(player: Player) {
        val numGladiatorsInPlay = player.inPlay.count { it is Gladiator }
        repeat(numGladiatorsInPlay) {
            player.gainSupplyCard(Gold(), true)
        }
    }

    override val pileName: String
        get() = Gladiator.NAME

    companion object {
        const val NAME: String = "Fortune"
    }
}

