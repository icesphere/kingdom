package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Supplies : MenagerieCard(NAME, CardType.Treasure, 2), UsesHorses {

    init {
        addCoins = 1
        special = "When you play this, gain a Horse onto your deck."
        isTreasureExcludedFromAutoPlay = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToTopOfDeck = true
        player.gainHorse(false)
    }

    companion object {
        const val NAME: String = "Supplies"
    }
}

