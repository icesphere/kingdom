package com.kingdom.model.cards.supply

class Province : VictoryCard(NAME, 8) {

    init {
        victoryPoints = 6
    }

    companion object {
        const val NAME: String = "Province"
    }
}

