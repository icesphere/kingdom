package com.kingdom.util.specialaction

import com.kingdom.model.*
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck

object GainCardsReactionHandler {

    fun getCardAction(action: String, game: OldGame, player: OldPlayer, card: Card, destination: CardLocation): OldCardAction? {

        when (action) {
            "Royal Seal" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_YES_NO)
                cardAction.isGainCardAction = true
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = "Royal Seal"
                cardAction.associatedCard = card
                if (destination == CardLocation.Hand) {
                    cardAction.instructions = "Do you want to put this card on top of your deck instead of in your hand?"
                } else {
                    cardAction.instructions = "Do you want to put this card on top of your deck instead of in your discard pile?"
                }
                cardAction.destination = destination
                cardAction.cards.add(card)
                return cardAction
            }
            "Trader" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.isGainCardAction = true
                cardAction.deck = Deck.Hinterlands
                cardAction.cardName = "Trader"
                cardAction.associatedCard = card
                cardAction.instructions = "Do you want to reveal your Trader to gain a Silver instead?"
                cardAction.destination = destination
                cardAction.cards.add(card)
                cardAction.choices.add(CardActionChoice("Don't Reveal", "no_reveal"))
                cardAction.choices.add(CardActionChoice("Gain Silver", "silver"))
                return cardAction
            }
            "Watchtower" -> {
                val cardAction = OldCardAction(OldCardAction.TYPE_CHOICES)
                cardAction.isGainCardAction = true
                cardAction.deck = Deck.Prosperity
                cardAction.cardName = "Watchtower"
                cardAction.associatedCard = card
                cardAction.instructions = "Do you want to reveal your Watchtower to trash this card, or put this card on top of your deck?"
                cardAction.destination = destination
                cardAction.cards.add(card)
                cardAction.choices.add(CardActionChoice("Don't Reveal", "no_reveal"))
                cardAction.choices.add(CardActionChoice("Trash Card", "trash"))
                cardAction.choices.add(CardActionChoice("Top of Deck", "deck"))
                return cardAction
            }
            else -> return null
        }
    }
}
