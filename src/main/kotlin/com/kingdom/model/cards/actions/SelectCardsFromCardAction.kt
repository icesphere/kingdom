package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

abstract class SelectCardsFromCardAction(text: String,
                                         cards: List<Card>,
                                         numCardsToSelect: Int = 1,
                                         optional: Boolean = false,
                                         cardActionableExpression: ((card: Card) -> Boolean)? = null)
    : SelectCardsAction(text, numCardsToSelect, optional, cardActionableExpression) {

    init {
        cardChoices = cards
    }

    override val cardsToSelectFrom: List<Card> = cards

    override val selectFromLocation = CardLocation.CardAction
}