package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackResolver
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Witch : KingdomCard(NAME, CardType.ActionAttack, 5), AttackResolver {

    init {
        addCards = 2
        special = "Each other player gains a Curse card."
        fontSize = 13
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { it.acquireFreeCardFromSupply(Curse()) }
    }

    companion object {
        const val NAME: String = "Witch"
    }
}

