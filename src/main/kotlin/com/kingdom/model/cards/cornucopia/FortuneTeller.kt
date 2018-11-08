package com.kingdom.model.cards.cornucopia

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player
import com.kingdom.util.groupedString

class FortuneTeller : CornucopiaCard(NAME, CardType.ActionAttack, 3), AttackCard {

    init {
        addCoins = 2
        special = "Each other player reveals cards from the top of their deck until they reveal a Victory card or Curse. They put it on top and discard the rest."
        textSize = 100
        fontSize = 9
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            val revealedCards = mutableListOf<Card>()

            var card = opponent.removeTopCardOfDeck()

            while (card != null && !card.isVictory && !card.isCurse) {
                revealedCards.add(card)
                card = opponent.removeTopCardOfDeck()
            }

            if (revealedCards.isNotEmpty()) {
                opponent.addUsernameGameLog("revealed ${revealedCards.groupedString}")
                opponent.addCardsToDiscard(revealedCards)
            }

            if (card != null) {
                opponent.addCardToTopOfDeck(card)
            }
        }
    }

    companion object {
        const val NAME: String = "Fortune Teller"
    }
}

