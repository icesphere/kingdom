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
        var treasureFound = false

        while(!treasureFound) {
            val card = player.removeTopCardOfDeck()
            if (card != null) {
                if (card.isTreasure) {
                    treasureFound = true
                    player.playCard(card)
                } else {
                    player.addCardToDiscard(card, showLog = true)
                }
            } else {
                break
            }
        }

        if (!treasureFound) {
            player.addUsernameGameLog("No treasures found")
            player.game.showInfoMessage(player, "No treasures found")
        }
    }

    companion object {
        const val NAME: String = "Venture"
    }
}

