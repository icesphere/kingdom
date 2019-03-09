package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Game
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class Obelisk : EmpiresLandmark(NAME), GameSetupModifier, VictoryPointsCalculator {

    init {
        special = "When scoring, 2 VP per card you have from the chosen pile. Setup: Choose a random Action Supply pile."
    }

    var chosenPile: String? = null

    override fun modifyGameSetup(game: Game) {
        chosenPile = game.kingdomCards.filter { it.isAction }.shuffled().first().pileName
        special = "When scoring, 2 VP per card you have from the $chosenPile pile."
    }

    override fun calculatePoints(player: Player): Int {
        return player.allCards.count { it.pileName == chosenPile } * 2
    }

    companion object {
        const val NAME: String = "Obelisk"
    }
}