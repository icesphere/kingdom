package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.DurationBeforeAttackListener
import com.kingdom.model.players.Player

class Lighthouse : SeasideCard(NAME, CardType.ActionDuration, 2), StartOfTurnDurationAction, DurationBeforeAttackListener {

    init {
        addActions = 1
        special = "Now and at the start of your next turn: +\$1. While this is in play, when another player plays an Attack card, it doesn’t affect you."
        textSize = 103
    }

    override val isDefense: Boolean = true

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins(1)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(1)
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.addGameLog("${player.username}'s ${this.cardNameWithBackgroundColor} prevented attack from ${attacker.username}'s ${attackCard.cardNameWithBackgroundColor}")
        attackCard.playersExcludedFromCardEffects.add(player)
    }

    companion object {
        const val NAME: String = "Lighthouse"
    }
}

