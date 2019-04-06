package com.kingdom.model.cards.renaissance.artifacts

class Flag : RenaissanceArtifact(NAME) {

    init {
        special = "When drawing your hand, +1 Card."
    }

    companion object {
        const val NAME: String = "Flag"
    }
}