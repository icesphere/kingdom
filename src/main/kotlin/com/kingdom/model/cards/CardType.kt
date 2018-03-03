package com.kingdom.model.cards

enum class CardType(val typeId: Int) {
    Action(1),
    ActionAttack(2),
    Victory(3),
    ActionReaction(4),
    Treasure(5),
    Curse(6),
    ActionVictory(7),
    TreasureVictory(8),
    ActionDuration(9),
    TreasureCurse(10),
    VictoryReaction(11),
    DurationVictory(12),
    Leader(13),
    TreasureReaction(14),
    ActionSummon(15);

    companion object {
        fun fromCardTypeId(typeId: Int): CardType {
            for (cardType in CardType.values()) {
                if (cardType.typeId == typeId) {
                    return cardType
                }
            }

            throw IllegalArgumentException("Card type not found for $typeId")
        }
    }
}