package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Contraband : ProsperityCard(NAME, CardType.Treasure, 5) {

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 3
        special = "When you play this, the player to your left names a card. You can’t buy that card this turn."
        fontSize = 11
        textSize = 81
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Contraband"
    }
}

