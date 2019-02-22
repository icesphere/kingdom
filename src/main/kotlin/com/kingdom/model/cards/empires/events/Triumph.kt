package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.supply.Estate
import com.kingdom.model.players.Player

class Triumph : EmpiresEvent(NAME, 0, 5) {

    init {
        special = "Gain an Estate. If you did, +1 VP per card you’ve gained this turn."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val estate = Estate()
        if (player.game.isCardAvailableInSupply(estate)) {
            player.gainSupplyCard(estate, true)
            val cardsGainedThisTurn = player.currentTurnSummary.cardsGained.size
            player.addVictoryCoins(cardsGainedThisTurn)
            player.addEventLogWithUsername("gained +$cardsGainedThisTurn VP")
        }
    }

    companion object {
        const val NAME: String = "Triumph"
    }
}