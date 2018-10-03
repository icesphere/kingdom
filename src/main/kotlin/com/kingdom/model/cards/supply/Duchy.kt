package com.kingdom.model.cards.supply

class Duchy : VictoryCard(NAME, 5) {

    init {
        victoryPoints = 3
    }

    companion object {
        const val NAME: String = "Duchy"
    }
}

