package com.kingdom.model.cards

enum class CardColor(val color: String, val mobileColor: String = color, val isImage: Boolean = false) {
    Treasure("#F6DC51"),
    Curse("#A17FBC"),
    Victory("#80B75A"),
    ActionReaction("#7FAED8"),
    ActionDuration("#F09954"),
    ActionDurationReaction("orange_blue.png", "orange_blue_mobile.png", true),
    ActionReserve("#C3AA7D"),
    ActionReserveVictory("tan_green.png", "tan_green_mobile.png", true),
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
    VictoryShelter("green_red.png", "green_red_mobile.png", true),
    TreasureReserve("gold_tan.png", "gold_tan_mobile.png", true),
    ActionTreasure("grey_gold.png", "grey_gold_mobile.png", true),
    Artifact("#E8A65C"),
    Project("#F09290")
}