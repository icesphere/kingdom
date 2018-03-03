package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck

object FanSpecialActionHandler {

    fun handleSpecialAction(game: Game, card: Card) {
        val player = game.currentPlayer

        when (card.name) {
            "Archivist" -> {
                val cardAction = CardAction(CardAction.TYPE_CHOICES)
                cardAction.deck = Deck.Fan
                cardAction.cardName = card.name
                cardAction.instructions = "Choose one: Draw until you have 6 cards in hand; or +$1 and discard 1 or more cards."
                cardAction.choices.add(CardActionChoice("Draw", "draw"))
                cardAction.choices.add(CardActionChoice("+$1 and discard", "discard"))
                game.setPlayerCardAction(player!!, cardAction)
            }
            "Museum" -> {
                val cards = player!!.hand
                        .filterNot { player.museumCards.contains(it) }
                        .toSet()
                if (!cards.isEmpty()) {
                    val museumCardAction = CardAction(CardAction.TYPE_CHOOSE_UP_TO)
                    museumCardAction.deck = Deck.Fan
                    museumCardAction.cardName = card.name
                    museumCardAction.cards.addAll(cards)
                    museumCardAction.numCards = 1
                    museumCardAction.instructions = "Select a card to be added to your Museum mat and then click Done, or just click Done if you don't want to add a card."
                    museumCardAction.buttonValue = "Done"
                    game.setPlayerCardAction(player, museumCardAction)
                } else if (player.museumCards.size >= 4) {
                    val museumCardAction = CardAction(CardAction.TYPE_YES_NO)
                    museumCardAction.deck = Deck.Fan
                    museumCardAction.cardName = "Museum Trash Cards"
                    museumCardAction.instructions = "Do you want to trash 4 cards from your Museum mat to gain a Prize and a Duchy?"
                    game.setPlayerCardAction(player, museumCardAction)
                }
            }
        }
    }
}
