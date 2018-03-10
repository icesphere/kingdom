package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Witch : KingdomCard(NAME, CardType.ActionAttack, 5) {
    init {
        addCards = 2
        special = "Each other player gains a Curse card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.opponents.forEach { it.acquireFreeCardFromSupply(Curse()) }
    }

    companion object {
        const val NAME: String = "Witch"
    }
}

