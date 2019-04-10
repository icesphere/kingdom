package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.players.Player

class Pageant : RenaissanceProject(NAME, 3), StartOfCleanupListener, ChoiceActionCard {

    init {
        special = "At the end of your Buy phase, you may pay \$1 for +1 Coffers."
    }

    override fun onStartOfCleanup(player: Player) {
        if (player.availableCoins > 0) {
            player.yesNoChoice(this, "Pay \$1 for +1 Coffers?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1 && player.availableCoins > 0) {
            player.addCoins(-1, false)
            player.addCoffers(1)
            player.addEventLogWithUsername("Paid \$1 for +1 Coffers")
        }
    }

    companion object {
        const val NAME: String = "Pageant"
    }
}