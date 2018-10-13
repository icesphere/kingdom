package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Militia : KingdomCard(NAME, CardType.ActionAttack, 4), AttackCard {

    init {
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents
                .forEach { p ->
                    if (p.hand.size > 3) {
                        p.discardCardsFromHand(p.hand.size - 3, false)
                    }
                }
    }

    companion object {
        const val NAME: String = "Militia"
    }
}

