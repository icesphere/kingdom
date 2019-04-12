package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class Fair : RenaissanceProject(NAME, 4), StartOfTurnProject {

    init {
        special = "At the start of your turn, +1 Buy."
    }

    override fun onStartOfTurn(player: Player) {
        player.addBuys(1)
    }

    companion object {
        const val NAME: String = "Fair"
    }
}