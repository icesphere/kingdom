package com.kingdom.model.cards

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class DeckConverter : AttributeConverter<Deck, String> {

    override fun convertToDatabaseColumn(deck: Deck): String {
        return deck.toString()
    }

    override fun convertToEntityAttribute(s: String): Deck {
        return Deck.valueOf(s)
    }
}
