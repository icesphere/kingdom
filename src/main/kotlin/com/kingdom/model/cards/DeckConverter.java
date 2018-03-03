package com.kingdom.model.cards;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DeckConverter implements AttributeConverter<Deck, String> {
    @Override
    public String convertToDatabaseColumn(Deck deck) {
        return deck.toString();
    }

    @Override
    public Deck convertToEntityAttribute(String s) {
        return Deck.valueOf(s);
    }
}
