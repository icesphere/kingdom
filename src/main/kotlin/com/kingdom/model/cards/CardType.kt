package com.kingdom.model.cards

@Suppress("unused")
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
    TreasureReaction(14),
    ActionRuins(15),
    ActionShelter(16),
    ReactionShelter(17),
    VictoryShelter(18);
}