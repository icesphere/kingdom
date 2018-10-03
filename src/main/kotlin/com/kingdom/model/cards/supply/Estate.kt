package com.kingdom.model.cards.supply

class Estate : VictoryCard(NAME, 2) {

    init {
        victoryPoints = 1
    }

    companion object {
        const val NAME: String = "Estate"
    }
}

