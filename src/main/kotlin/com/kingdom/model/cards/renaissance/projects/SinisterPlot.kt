package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.Choice
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.players.Player

class SinisterPlot : RenaissanceProject(NAME, 4), StartOfTurnProject, ChoiceActionCard {

    init {
        special = "At the start of your turn, add a token here, or remove your tokens here for +1 Card each."
        fontSize = 10
    }

    override fun onStartOfTurn(player: Player) {
        if (player.sinisterPlotTokens == 0) {
            player.sinisterPlotTokens = player.sinisterPlotTokens + 1
            player.refreshPlayerHandArea()
            player.addEventLogWithUsername("added a token to $cardNameWithBackgroundColor")
        } else {
            var removeTokensChoiceText = "Remove tokens from Sinister Plot for +${player.sinisterPlotTokens} Card"
            if (player.sinisterPlotTokens > 1) {
                removeTokensChoiceText += "s"
            }
            player.makeChoice(this, Choice(1, "Add token to Sinister Plot"), Choice(2, removeTokensChoiceText))
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.sinisterPlotTokens = player.sinisterPlotTokens + 1
            player.addEventLogWithUsername("added a token to $cardNameWithBackgroundColor")
        } else {
            var removeTokensLog = "removed tokens from $cardNameWithBackgroundColor for +${player.sinisterPlotTokens} Card"
            if (player.sinisterPlotTokens > 1) {
                removeTokensLog += "s"
            }
            player.addEventLogWithUsername(removeTokensLog)
            player.drawCards(player.sinisterPlotTokens)
            player.sinisterPlotTokens = 0
        }
        player.refreshPlayerHandArea()
    }

    companion object {
        const val NAME: String = "Sinister Plot"
    }
}