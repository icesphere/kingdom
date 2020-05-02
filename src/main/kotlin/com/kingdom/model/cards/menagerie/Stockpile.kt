package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Stockpile : MenagerieCard(NAME, CardType.Treasure, 3), UsesExileMat {

    init {
        addCoins = 3
        addBuys = 1
        special = "When you play this, Exile it."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.exileCardFromInPlay(this)
    }

    companion object {
        const val NAME: String = "Stockpile"
    }
}

