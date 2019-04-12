package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.AfterCardGainedListener
import com.kingdom.model.players.Player

class Academy : RenaissanceProject(NAME, 5), AfterCardGainedListener {

    init {
        special = "When you gain an Action card, +1 Villager."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isAction) {
            player.addVillagers(1)
            player.addEventLogWithUsername("gained +1 Villager from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Academy"
    }
}