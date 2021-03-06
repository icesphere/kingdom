package com.kingdom.model.cards.base

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Sentry : BaseCard(NAME, CardType.Action, 5), ChoiceActionCard {

    private val cardsForAction = mutableListOf<Card>()

    private val cardsToPutOnTopOfDeck = mutableListOf<Card>()

    init {
        addCards = 1
        addActions = 1
        special = "Look at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val topCards = mutableListOf(player.removeTopCardOfDeck())
        topCards.add(player.removeTopCardOfDeck())

        topCards.filterNotNull().forEachIndexed { index, card ->
            val numText = if (index == 0) "1st" else "2nd"
            cardsForAction.add(card)
            player.makeChoice(this,
                    "$numText card on top of deck: ${card.cardNameWithBackgroundColor}",
                    Choice(1, "Trash"),
                    Choice(2, "Discard"),
                    Choice(3, "Top of Deck"))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = cardsForAction.removeAt(0)
        when (choice) {
            1 -> {
                player.addEventLogWithUsername("trashed ${card.cardNameWithBackgroundColor}")
                player.cardTrashed(card)
            }
            2 -> {
                player.addEventLogWithUsername("discarded ${card.cardNameWithBackgroundColor}")
                player.addCardToDiscard(card)
            }
            else -> cardsToPutOnTopOfDeck.add(card)
        }

        if (cardsForAction.isEmpty() && cardsToPutOnTopOfDeck.isNotEmpty()) {
            if (cardsToPutOnTopOfDeck.size == 1) {
                player.addCardToTopOfDeck(cardsToPutOnTopOfDeck[0], false)
            } else {
                player.putCardsOnTopOfDeckInAnyOrder(cardsToPutOnTopOfDeck)
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        cardsForAction.clear()
        cardsToPutOnTopOfDeck.clear()
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME: String = "Sentry"
    }
}

