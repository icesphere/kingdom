package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Oasis : HinterlandsCard(NAME, CardType.Action, 3) {

    init {
        testing = true
        addCards = 1
        addActions = 1
        addCoins = 1
        special = "Discard a card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardFromHand()
    }

    companion object {
        const val NAME: String = "Oasis"
    }
}

