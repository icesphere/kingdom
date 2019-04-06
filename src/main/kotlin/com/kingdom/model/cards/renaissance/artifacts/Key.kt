package com.kingdom.model.cards.renaissance.artifacts

class Key : RenaissanceArtifact(NAME) {

    init {
        special = "At the start of your turn, +\$1."
    }

    companion object {
        const val NAME: String = "Key"
    }
}