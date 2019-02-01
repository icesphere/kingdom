package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class Cultist : DarkAgesCard(NAME, CardType.ActionAttackLooter, 5), AttackCard, ChoiceActionCard, AfterCardTrashedListenerForSelf {

    init {
        addCards = 2
        special = "Each other player gains a Ruins. You may play a Cultist from your hand. When you trash this, +3 Cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        for (opponent in affectedOpponents) {
            val ruins = opponent.gainRuins()
            if (ruins != null) {
                opponent.showInfoMessage("You gained ${ruins.cardNameWithBackgroundColor} from ${player.username}'s ${this.cardNameWithBackgroundColor}")
            }
        }

        if (player.hand.any { it is Cultist }) {
            player.yesNoChoice(this, "Play a Cultist from your hand?")
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addActions(1)
            val cultist = player.hand.first { it is Cultist }
            player.playCard(cultist)
        }
    }

    override fun afterCardTrashed(player: Player) {
        player.drawCards(3)
    }

    companion object {
        const val NAME: String = "Cultist"
    }
}

