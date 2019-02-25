package com.kingdom.model.cards.empires.landmarks

class Aqueduct : EmpiresLandmark(NAME) {

    init {
        special = "When you gain a Treasure, move 1 VP from its pile to this. When you gain a Victory card, take the VP from this. Setup: Put 8 VP on the Silver and Gold piles."
    }

    companion object {
        const val NAME: String = "Aqueduct"
    }
}