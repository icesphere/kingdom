package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class Cathedral : RenaissanceProject(NAME, 3), StartOfTurnProject {

    init {
        special = "At the start of your turn, trash a card from your hand."
    }

    override fun onStartOfTurn(player: Player) {
        player.trashCardFromHand(false)
    }

    companion object {
        const val NAME: String = "Cathedral"
    }
}