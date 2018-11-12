package com.kingdom.model.cards

enum class CardColor(val color: String, val isImage: Boolean = false) {
    Treasure("#F6DC51"),
    Curse("#A17FBC"),
    Victory("#80B75A"),
    ActionReaction("#7FAED8"),
    ActionDuration("#F09954"),
    Action("#CBC6B3"),
    ActionVictory("grey_green.gif", true),
    TreasureVictory("gold_green.gif", true),
    VictoryReaction("green_blue.gif", true),
    TreasureCurse("gold_purple.gif", true),
    DurationVictory("orange_green.gif", true),
    TreasureReaction("gold_blue.gif", true),
    Ruins("#9C633F"),
    ActionShelter("grey_red.png", true),
    ReactionShelter("blue_red.png", true),
    VictoryShelter("green_red.png", true)
}