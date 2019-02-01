package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Militia : BaseCard(NAME, CardType.ActionAttack, 4), AttackCard {

    init {
        addCoins = 2
        special = "Each other player discards down to 3 cards in their hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    companion object {
        const val NAME: String = "Militia"
    }
}

