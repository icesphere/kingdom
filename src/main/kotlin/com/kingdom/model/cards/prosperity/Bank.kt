package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Bank : ProsperityCard(NAME, CardType.Treasure, 7) {

    init {
        isTreasureExcludedFromAutoPlay = true
        special = "When you play this, it’s worth \$1 per Treasure card you have in play (counting this)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val numTreasuresInPlay = player.inPlay.count { it.isTreasure }
        player.addCoins(numTreasuresInPlay)
        player.addEventLogWithUsername("gained +\$$numTreasuresInPlay from ${this.cardNameWithBackgroundColor}")
    }

    companion object {
        const val NAME: String = "Bank"
    }
}

