package com.kingdom.model.cards.renaissance.artifacts

import com.kingdom.model.cards.renaissance.RenaissanceArtifact

class Flag : RenaissanceArtifact(NAME) {

    init {
        special = "When drawing your hand, +1 Card."
    }

    companion object {
        const val NAME: String = "Flag"
    }
}