package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Conquest : EmpiresEvent(NAME, 6) {

    init {
        special = "Gain 2 Silvers. +1 VP per Silver you’ve gained this turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Silver(), true)
        player.gainSupplyCard(Silver(), true)

        val silversGained = player.currentTurnSummary.cardsGained.count { it.isSilver }

        player.addVictoryCoins(silversGained, true)
    }

    companion object {
        const val NAME: String = "Conquest"
    }
}