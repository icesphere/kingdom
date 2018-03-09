package com.kingdom.model.computer

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer

class MediumComputerPlayer(player: OldPlayer, game: OldGame) : ComputerPlayer(player, game) {
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
