package com.kingdom.model.cards.renaissance.artifacts

import com.kingdom.model.cards.renaissance.RenaissanceArtifact

class Key : RenaissanceArtifact(NAME) {

    init {
        special = "At the start of your turn, +\$1."
    }

    companion object {
        const val NAME: String = "Key"
    }
}