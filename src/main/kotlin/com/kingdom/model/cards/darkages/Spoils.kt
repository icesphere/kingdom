package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Spoils : DarkAgesCard(NAME, CardType.Treasure, 0) {

    init {
        addCoins = 3
        special = "When you play this, return it to the pile."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.removeCardInPlay(this)
        player.game.returnCardToSupply(this)
    }

    companion object {
        const val NAME: String = "Spoils"
    }
}

