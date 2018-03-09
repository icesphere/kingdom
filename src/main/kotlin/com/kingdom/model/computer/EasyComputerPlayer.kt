package com.kingdom.model.computer

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer

class EasyComputerPlayer(player: OldPlayer, game: OldGame) : ComputerPlayer(player, game) {
    init {
        difficulty = 1
    }

    override fun setupStartingStrategies() {}

    override fun excludeCard(card: Card): Boolean {
        return excludeCardEasy(card)
    }
}
