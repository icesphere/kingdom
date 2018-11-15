package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Feodum : DarkAgesCard(NAME, CardType.Victory, 4), VictoryPointsCalculator, AfterCardTrashedListenerForSelf {

    init {
        special = "Worth 1 VP per 3 Silvers you have (round down). When you trash this, gain 3 Silvers."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.isSilver } / 3
    }

    override fun afterCardTrashed(player: Player) {
        player.gainSupplyCard(Silver(), true)
        player.gainSupplyCard(Silver(), true)
        player.gainSupplyCard(Silver(), true)
    }

    companion object {
        const val NAME: String = "Feodum"
    }
}

