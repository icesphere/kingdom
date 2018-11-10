package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Margrave : HinterlandsCard(NAME, CardType.ActionAttack, 5), AttackCard {

    init {
        addCards = 3
        addBuys = 1
        special = "Each other player draws a card, then discards down to 3 cards in hand."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            opponent.drawCard()
            if (opponent.hand.size > 3) {
                opponent.discardCardsFromHand(opponent.hand.size - 3, false)
            }
        }
    }

    companion object {
        const val NAME: String = "Margrave"
    }
}

