package com.kingdom.model.cards.renaissance.artifacts

import com.kingdom.model.cards.renaissance.RenaissanceArtifact

class TreasureChest : RenaissanceArtifact(NAME) {

    init {
        special = "At the start of your Buy phase, gain a Gold."
        fontSize = 10
    }

    companion object {
        const val NAME: String = "Treasure Chest"
    }
}