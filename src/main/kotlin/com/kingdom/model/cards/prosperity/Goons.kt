package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.listeners.AfterCardBoughtListenerForCardsInPlay
import com.kingdom.model.players.Player

class Goons : ProsperityCard(NAME, CardType.ActionAttack, 6), AttackCard, AfterCardBoughtListenerForCardsInPlay {

    init {
        addBuys = 1
        addCoins = 2
        special = "Each other player discards down to 3 cards in hand. While this is in play, when you buy a card, +1 VP token."
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

    override fun afterCardBought(card: Card, player: Player) {
        player.addVictoryCoins(1)
    }

    companion object {
        const val NAME: String = "Goons"
    }
}

