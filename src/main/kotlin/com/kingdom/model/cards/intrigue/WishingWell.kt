package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class WishingWell : IntrigueCard(NAME, CardType.Action, 3) {

    init {
        disabled = true
        addActions = 1
        addCards = 1
        special = "Name a card, then reveal the top card of your deck. If you name it, put it in your hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Wishing Well"
    }
}

