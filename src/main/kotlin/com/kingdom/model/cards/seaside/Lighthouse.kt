package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.listeners.DurationBeforeAttackListener
import com.kingdom.model.players.Player

class Lighthouse : SeasideCard(NAME, CardType.ActionDuration, 2), StartOfTurnDurationAction, DurationBeforeAttackListener {

    init {
        addActions = 1
        special = "Now and at the start of your next turn: +\$1. While this is in play, when another player plays an Attack card, it doesn’t affect you."
        isDefense = true
        isAddCoinsCard = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.addCoins(1)
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.addCoins(1)
    }

    override fun onBeforeAttack(attackCard: Card, player: Player, attacker: Player) {
        player.addEventLogWithUsername("'s ${this.cardNameWithBackgroundColor} prevented attack from ${attacker.username}'s ${attackCard.cardNameWithBackgroundColor}")
        attacker.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor prevented attack from ${attackCard.cardNameWithBackgroundColor}")
        attackCard.playersExcludedFromCardEffects.add(player)
    }

    companion object {
        const val NAME: String = "Lighthouse"
    }
}

