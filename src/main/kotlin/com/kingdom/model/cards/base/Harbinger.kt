package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Harbinger : BaseCard(NAME, CardType.Action, 3) {
    init {
        addCards = 1
        addActions = 1
        special = "Look through your discard pile. You may put a card from it onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCardFromDiscardToTopOfDeck(null)
    }

    companion object {
        const val NAME: String = "Harbinger"
    }
}

