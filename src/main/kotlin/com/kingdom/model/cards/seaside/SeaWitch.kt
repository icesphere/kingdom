package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.StartOfTurnDurationAction
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class SeaWitch : SeasideCard(NAME, CardType.ActionAttackDuration, 5), AttackCard, StartOfTurnDurationAction {

    init {
        addCards = 2
        special = "Each other player gains a Curse. At the start of your next turn, +2 Cards, then discard 2 cards."
        isCurseGiver = true
        fontSize = 11
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            opponent.gainSupplyCard(Curse(), showLog = true)
            opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor made you gain a ${Curse().cardNameWithBackgroundColor}")
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
        player.discardCardsFromHand(2, false)
    }

    companion object {
        const val NAME: String = "Sea Witch"
    }
}
