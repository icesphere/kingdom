package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class CityGate : RenaissanceProject(NAME, 3), StartOfTurnProject {

    init {
        special = "At the start of your turn, +1 Card, then put a card from your hand onto your deck."
    }

    override fun onStartOfTurn(player: Player) {
        player.drawCard()
        player.addCardFromHandToTopOfDeck()
    }

    companion object {
        const val NAME: String = "City Gate"
    }
}