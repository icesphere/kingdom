package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class Barracks : RenaissanceProject(NAME, 6), StartOfTurnProject {

    init {
        special = "At the start of your turn, +1 Action."
    }

    override fun onStartOfTurn(player: Player) {
        player.addActions(1)
        player.showInfoMessage("Gained +1 Action from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Barracks"
    }
}