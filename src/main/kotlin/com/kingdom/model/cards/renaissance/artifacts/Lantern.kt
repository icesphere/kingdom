package com.kingdom.model.cards.renaissance.artifacts

import com.kingdom.model.cards.renaissance.RenaissanceArtifact

class Lantern : RenaissanceArtifact(NAME) {

    init {
        special = "Your Border Guards reveal 3 cards and discard 2. (It takes all 3 being Actions to take the Horn.)"
    }

    companion object {
        const val NAME: String = "Lantern"
    }
}