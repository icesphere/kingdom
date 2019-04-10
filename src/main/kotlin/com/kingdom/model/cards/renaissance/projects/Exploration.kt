package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.listeners.StartOfCleanupListener
import com.kingdom.model.players.Player

class Exploration : RenaissanceProject(NAME, 4), StartOfCleanupListener {

    init {
        special = "At the end of your Buy phase, if you didn’t buy any cards, +1 Coffers and +1 Villager."
    }

    override fun onStartOfCleanup(player: Player) {
        if (player.cardsBought.isEmpty()) {
            player.addCoffers(1, false)
            player.addVillagers(1, false)
            player.addEventLogWithUsername("gained +1 Coffers and +1 Villager from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Exploration"
    }
}