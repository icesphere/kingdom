package com.kingdom.model.cards.supply

class Colony : VictoryCard(NAME, 11) {

    init {
        victoryPoints = 10
    }

    companion object {
        const val NAME: String = "Colony"
    }
}

