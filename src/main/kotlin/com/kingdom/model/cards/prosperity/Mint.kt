package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Mint : ProsperityCard(NAME, CardType.Action, 5) {

    //todo when you buy

    init {
        testing = true
        special = "You may reveal a Treasure card from your hand. Gain a copy of it. When you buy this, trash all Treasures you have in play."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Mint"
    }
}

