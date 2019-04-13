package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.listeners.AfterCardGainedListener
import com.kingdom.model.players.Player

class Guildhall : RenaissanceProject(NAME, 5), AfterCardGainedListener {

    init {
        special = "When you gain a Treasure, +1 Coffers."
    }

    override fun afterCardGained(card: Card, player: Player) {
        if (card.isTreasure) {
            player.addCoffers(1)
            player.addEventLogWithUsername("gained +1 Coffers from $cardNameWithBackgroundColor")
        }
    }

    companion object {
        const val NAME: String = "Guildhall"
    }
}