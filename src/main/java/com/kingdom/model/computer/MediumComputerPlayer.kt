package com.kingdom.model.computer

import com.kingdom.model.Card
import com.kingdom.model.Game
import com.kingdom.model.Player

class MediumComputerPlayer(player: Player, game: Game) : ComputerPlayer(player, game) {
    init {
        difficulty = 2
    }

    override fun setupStartingStrategies() {
        if (hasGardens && hasExtraBuys) {
            isGardensStrategy = true
        } else if (kingdomCardMap.containsKey("Chapel")) {
            chapelStrategy = true
        } else if (hasDuke) {
            dukeStrategy = true
        }
    }

    override fun excludeCard(card: Card): Boolean {
        return excludeCardMedium(card)
    }
}
