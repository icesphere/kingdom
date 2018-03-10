package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class ThroneRoom : KingdomCard(NAME, CardType.Action, 4) {
    init {
        special = "You may play an Action card from your hand twice."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Throne Room"
    }
}

