package com.kingdom.model.cards.renaissance.artifacts

class Horn : RenaissanceArtifact(NAME) {

    init {
        special = "Once per turn, when you discard a Border Guard from play, you may put it onto your deck."
    }

    companion object {
        const val NAME: String = "Horn"
    }
}