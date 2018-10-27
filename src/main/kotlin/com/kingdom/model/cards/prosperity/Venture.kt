package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class Venture : ProsperityCard(NAME, CardType.Treasure, 5) {

    init {
        testing = true
        isPlayTreasureCardsRequired = true
        isTreasureExcludedFromAutoPlay = true
        addCoins = 1
        special = "When you play this, reveal cards from your deck until you reveal a Treasure. Discard the rest, then play the Treasure."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        var treasureFound = false

        val revealedCards = mutableListOf<Card>()

        while(!treasureFound) {
            val card = player.removeTopCardOfDeck()
            if (card != null) {
                revealedCards.add(card)
                if (card.isTreasure) {
                    treasureFound = true
                    player.playCard(card)
                }
            } else {
                break
            }
        }

        if (revealedCards.isNotEmpty()) {
            player.addUsernameGameLog("revealed: ${revealedCards.groupedString}")
        }
    }

    companion object {
        const val NAME: String = "Venture"
    }
}

