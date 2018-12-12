package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.PermanentDuration
import com.kingdom.model.cards.listeners.CardPlayedListenerForCardsInPlay
import com.kingdom.model.cards.listeners.DurationBeforeAttackListener
import com.kingdom.model.players.Player

class Champion : AdventuresCard(NAME, CardType.ActionDuration, 6), PermanentDuration, DurationBeforeAttackListener, CardPlayedListenerForCardsInPlay {

    init {
        addActions = 1
        special = "For the rest of the game, when another player plays an attack card, it doesn't affect you, and when you play an Action, +1 Action. (This stays in play. This is not in the supply.)"
        textSize = 95
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.addEventLogWithUsername("'s ${this.cardNameWithBackgroundColor} prevented attack from ${attacker.username}'s ${attackCard.cardNameWithBackgroundColor}")
        attackCard.playersExcludedFromCardEffects.add(player)
    }

    override fun onCardPlayed(card: Card, player: Player) {
        player.addActions(1)
    }

    companion object {
        const val NAME: String = "Champion"
    }
}

