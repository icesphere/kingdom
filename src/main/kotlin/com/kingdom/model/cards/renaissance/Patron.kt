package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardRevealedListenerForSelf
import com.kingdom.model.players.Player

class Patron : RenaissanceCard(NAME, CardType.ActionReaction, 4), AfterCardRevealedListenerForSelf {

    init {
        addVillagers = 1
        addCoins = 2
        special = "When something causes you to reveal this (using the word “reveal”), +1 Coffers."
    }

    override fun afterCardRevealed(player: Player) {
        player.addCoffers(1)
        player.addEventLogWithUsername("gained 1 Coffers from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Patron"
    }
}