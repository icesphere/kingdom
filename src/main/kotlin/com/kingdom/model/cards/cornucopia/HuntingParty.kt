package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class HuntingParty : CornucopiaCard(NAME, CardType.Action, 5) {

    init {
        addCards = 1
        addActions = 1
        special = "Reveal your hand. Reveal cards from your deck until you reveal one that isn’t a copy of one in your hand. Put it into your hand and discard the rest."
        fontSize = 10
        textSize = 72
    }

    override fun cardPlayedSpecialAction(player: Player) {
        if (player.hand.isNotEmpty()) {
            player.revealHand()

            val handCardNames = player.hand.distinctBy { it.name }.map { it.name }

            val revealedCards = mutableListOf<Card>()

            var card = player.removeTopCardOfDeck()

            while (card != null && !handCardNames.contains(card.name)) {
                revealedCards.add(card)
                card = player.removeTopCardOfDeck()
            }

            if (revealedCards.isNotEmpty()) {
                player.addUsernameGameLog("revealed ${revealedCards.groupedString}")
                player.addCardsToDiscard(revealedCards)
            }

            if (card != null) {
                player.addCardToHand(card, true)
            }
        }
    }

    companion object {
        const val NAME: String = "Hunting Party"
    }
}

