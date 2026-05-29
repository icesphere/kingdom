package com.kingdom.model.cards.hinterlands

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Cartographer : HinterlandsCard(NAME, CardType.Action, 5), ChoiceActionCard {

    private val cardsToResolve = mutableListOf<Card>()
    private val cardsToPutBack = mutableListOf<Card>()

    init {
        addCards = 1
        addActions = 1
        special = "Look at the top 4 cards of your deck. Discard any number of them, then put the rest back in any order."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        val cards = player.removeTopCardsOfDeck(4, revealCards = false)
        cardsToResolve.addAll(cards)

        cards.forEach { card ->
            player.makeChoiceWithInfo(this,
                    "Top card of your deck: ${card.cardNameWithBackgroundColor}",
                    card,
                    Choice(1, "Discard"),
                    Choice(2, "Put back"))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        val card = info as Card
        cardsToResolve.remove(card)

        if (choice == 1) {
            player.discardCard(card, showLog = true)
        } else {
            cardsToPutBack.add(card)
        }

        if (cardsToResolve.isEmpty() && cardsToPutBack.isNotEmpty()) {
            val cards = cardsToPutBack.toList()
            cardsToPutBack.clear()
            if (cards.size == 1) {
                player.addCardToTopOfDeck(cards.first(), false)
            } else {
                player.putCardsOnTopOfDeckInAnyOrder(cards)
            }
        }
    }

    override fun removedFromPlay(player: Player) {
        cardsToResolve.clear()
        cardsToPutBack.clear()
        super.removedFromPlay(player)
    }

    companion object {
        const val NAME: String = "Cartographer"
    }
}
