package com.kingdom.model.cards

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class CardTypeConverter : AttributeConverter<CardType, Int> {

    override fun convertToDatabaseColumn(cardType: CardType): Int {
        return cardType.typeId
    }

    override fun convertToEntityAttribute(i: Int): CardType {
        return CardType.fromCardTypeId(i)!!
    }
}
