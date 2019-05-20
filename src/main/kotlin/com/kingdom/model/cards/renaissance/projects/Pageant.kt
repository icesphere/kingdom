package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.StartOfTurnProject
import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.players.Player

class Pageant : RenaissanceProject(NAME, 3), StartOfCleanupListener, StartOfTurnProject {

    init {
        special = "At the end of your Buy phase, you may pay \$1 for +1 Coffers."
    }

    var usedThisTurn: Boolean = false

    override fun onStartOfTurn(player: Player) {
        usedThisTurn = false
    }

    override fun onStartOfCleanup(player: Player) {
        if (usedThisTurn) {
            return
        }

        usedThisTurn = true

        if (player.availableCoins > 0) {
            player.addCoins(-1, false)
            player.addCoffers(1)
            player.addEventLogWithUsername("Paid \$1 for +1 Coffers")
            player.showInfoMessage("Gained +1 Coffers from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Pageant"
    }
}