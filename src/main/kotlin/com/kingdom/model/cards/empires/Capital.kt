package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.CardDiscardedFromPlayListener
import com.kingdom.model.players.Player

class Capital : EmpiresCard(NAME, CardType.Treasure, 5), CardDiscardedFromPlayListener {

    init {
        addCoins = 6
        addBuys = 1
        special = "When you discard this from play, take 6 debt, and then you may pay off debt."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun onCardDiscarded(player: Player) {
        player.addDebt(6, false)
    }

    companion object {
        const val NAME: String = "Capital"
    }
}

