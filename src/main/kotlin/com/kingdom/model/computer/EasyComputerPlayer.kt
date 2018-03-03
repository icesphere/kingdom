package com.kingdom.model.computer

import com.kingdom.model.cards.Card
import com.kingdom.model.Game
import com.kingdom.model.Player

class EasyComputerPlayer(player: Player, game: Game) : ComputerPlayer(player, game) {
    init {
        difficulty = 1
    }

    override fun setupStartingStrategies() {}

    override fun excludeCard(card: Card): Boolean {
        return excludeCardEasy(card)
    }
}
