package com.kingdom.model.cards.intrigue

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.players.Player

class Minion : IntrigueCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addActions = 1
        special = "Choose one: +\$2; or discard your hand, +4 Cards, and each other player with at least 5 cards in hand discards their hand and draws 4 cards."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        player.makeChoiceWithInfo(this, special, affectedOpponents, Choice(1, "+\$2"), Choice(2, "Discard hands"))
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addEventLogWithUsername("chose +\$2")
            player.addCoins(2)
        } else {
            player.addEventLogWithUsername("chose to discard hands")

            player.discardHand()

            player.drawCards(4)

            @Suppress("UNCHECKED_CAST")
            val affectedOpponents = info as List<Player>

            affectedOpponents
                    .forEach { opponent ->
                        opponent.discardHand()
                        opponent.drawCards(4)
                        opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded your hand and you drew 4 new Cards")
                    }
        }
    }

    companion object {
        const val NAME: String = "Minion"
    }
}

