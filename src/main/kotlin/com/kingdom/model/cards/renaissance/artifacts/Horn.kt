package com.kingdom.model.cards.renaissance.artifacts

import com.kingdom.model.cards.renaissance.RenaissanceArtifact

class Horn : RenaissanceArtifact(NAME) {

    init {
        special = "Once per turn, when you discard a Border Guard from play, you may put it onto your deck."
    }

    companion object {
        const val NAME: String = "Horn"
    }
}