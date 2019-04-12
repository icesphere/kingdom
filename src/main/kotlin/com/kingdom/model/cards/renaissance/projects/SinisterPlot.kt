package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class SinisterPlot : RenaissanceProject(NAME, 4), StartOfTurnProject {

    init {
        disabled = true
        special = "At the start of your turn, add a token here, or remove your tokens here for +1 Card each."
        fontSize = 10
    }

    override fun onStartOfTurn(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Sinister Plot"
    }
}