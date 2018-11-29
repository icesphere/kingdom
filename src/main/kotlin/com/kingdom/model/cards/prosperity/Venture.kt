package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Venture : ProsperityCard(NAME, CardType.Treasure, 5) {

    init {
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 1
        special = "When you play this, reveal cards from your deck until you reveal a Treasure. Discard the rest, then play the Treasure."
        textSize = 75
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val card = player.revealFromDeckUntilCardFoundAndDiscardOthers { c -> c.isTreasure }

        if (card != null) {
            player.playCard(card)
        } else {
            val message = "No treasures found"
            player.addEventLogWithUsername(message)
            player.showInfoMessage(message)
        }
    }

    companion object {
        const val NAME: String = "Venture"
    }
}

