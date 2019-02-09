package com.kingdom.model.cards.empires.castles

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.empires.EmpiresCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class CrumblingCastle : EmpiresCard(NAME, CardType.VictoryCastle, 4), AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        victoryPoints = 1
        special = "When you gain or trash this, +1 VP and gain a Silver."
        fontSize = 9
    }

    override fun afterCardGained(player: Player) {
        player.addVictoryCoins(1)
        player.gainSupplyCard(Silver(), true)
    }

    override fun afterCardTrashed(player: Player) {
        player.addVictoryCoins(1)
        player.gainSupplyCard(Silver(), true)
    }

    override val pileName: String
        get() = Castles.NAME

    companion object {
        const val NAME: String = "Crumbling Castle"
    }
}

