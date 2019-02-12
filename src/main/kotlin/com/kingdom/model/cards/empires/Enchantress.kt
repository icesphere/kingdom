package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.StartOfTurnDurationAction
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.players.Player

class Enchantress : EmpiresCard(NAME, CardType.ActionAttackDuration, 3), StartOfTurnDurationAction, AttackCard {

    init {
        addActions = 1
        special = "Until your next turn, the first time each other player plays an Action card on their turn, they get +1 Card and +1 Action instead of following its instructions. At the start of your next turn, +2 Cards."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            opponent.nextActionEnchanted = true
            opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor will cause your next action to get +1 Card and +1 Action instead of following its instructions")
        }
    }

    override fun durationStartOfTurnAction(player: Player) {
        player.drawCards(2)
        player.showInfoMessage("Gained +2 Cards from $cardNameWithBackgroundColor")
    }

    companion object {
        const val NAME: String = "Enchantress"
    }
}

