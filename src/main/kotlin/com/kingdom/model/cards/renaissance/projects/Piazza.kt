package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class Piazza : RenaissanceProject(NAME, 5), StartOfTurnProject {

    init {
        special = "At the start of your turn, reveal the top card of your deck. If it’s an Action, play it."
    }

    override fun onStartOfTurn(player: Player) {
        val card = player.revealTopCardOfDeck()
        if (card != null) {
            if (card.isAction) {
                player.removeTopCardOfDeck()
                player.addActions(1)
                player.addEventLogWithUsername("'s $cardNameWithBackgroundColor played ${card.cardNameWithBackgroundColor}")
                player.showInfoMessage("$cardNameWithBackgroundColor played ${card.cardNameWithBackgroundColor}")
                player.playCard(card, showLog = false)
            } else {
                player.showInfoMessage("$cardNameWithBackgroundColor revealed ${card.cardNameWithBackgroundColor}")
            }
        }
    }

    companion object {
        const val NAME: String = "Piazza"
    }
}