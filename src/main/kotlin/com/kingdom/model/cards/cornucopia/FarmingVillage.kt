package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class FarmingVillage : CornucopiaCard(NAME, CardType.Action, 4) {

    init {
        addActions = 2
        special = "Reveal cards from the top of your deck until you reveal an Action or Treasure card. Put that card into your hand and discard the rest."
        textSize = 102
    }

    override fun cardPlayedSpecialAction(player: Player) {

        val revealedCards = mutableListOf<Card>()

        var card = player.removeTopCardOfDeck()

        while (card != null && !card.isAction && !card.isTreasure) {
            revealedCards.add(card)
            card = player.removeTopCardOfDeck()
        }

        if (revealedCards.isNotEmpty()) {
            player.addUsernameGameLog("revealed ${revealedCards.groupedString}")
            player.addCardsToDiscard(revealedCards)
        }

        if (card != null) {
            player.addUsernameGameLog("added ${card.cardNameWithBackgroundColor} to their hand")
            player.addCardToHand(card)
        }
    }

    companion object {
        const val NAME: String = "Farming Village"
    }
}

