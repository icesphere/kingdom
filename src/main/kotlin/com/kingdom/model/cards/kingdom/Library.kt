package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Library : KingdomCard(NAME, CardType.Action, 5) {
    init {
        special = "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Library"
    }
}

