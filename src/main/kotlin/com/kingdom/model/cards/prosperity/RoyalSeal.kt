package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class RoyalSeal : ProsperityCard(NAME, CardType.Treasure, 5) {

    init {
        addCoins = 2
        special = "While this is in play, when you gain a card, you may put that card onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.numCardGainedMayPutOnTopOfDeck++
    }

    override fun removedFromPlay(player: Player) {
        player.numCardGainedMayPutOnTopOfDeck--
    }

    companion object {
        const val NAME: String = "Royal Seal"
    }
}

