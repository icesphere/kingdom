package com.kingdom.model.cards

enum class CardColor(val color: String, val mobileColor: String = color, val isImage: Boolean = false) {
    Treasure("#F6DC51"),
    Curse("#A17FBC"),
    Victory("#80B75A"),
    ActionReaction("#7FAED8"),
    ActionDuration("#F09954"),
    Action("#CBC6B3"),
    ActionVictory("grey_green.gif", "grey_green_mobile.gif", true),
    TreasureVictory("gold_green.gif", "gold_green_mobile.gif", true),
    VictoryReaction("green_blue.gif", "green_blue_mobile.gif", true),
    TreasureCurse("gold_purple.gif", "gold_purple_mobile.gif", true),
    DurationVictory("orange_green.gif", "orange_green_mobile.gif", true),
    TreasureReaction("gold_blue.gif", "gold_blue_mobile.gif", true),
    Ruins("#B68C59"),
    ActionShelter("grey_red.png", "grey_red_mobile.png", true),
    ReactionShelter("blue_red.png", "blue_red_mobile.png", true),
    VictoryShelter("green_red.png", "green_red_mobile.png", true)
}