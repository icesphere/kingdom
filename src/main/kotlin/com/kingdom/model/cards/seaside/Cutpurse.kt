package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Cutpurse : SeasideCard(NAME, CardType.ActionAttack, 4), AttackCard {

    init {
        addCoins = 2
        special = "Each other player discards a Copper (or reveals a hand with no Copper)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            if (opponent.hand.any { it.isCopper }) {
                opponent.discardCardFromHand(opponent.hand.first { it.isCopper })
            } else {
                opponent.revealHand()
            }
        }
    }

    companion object {
        const val NAME: String = "Cutpurse"
    }
}

