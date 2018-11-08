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
        textSize = 100
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.makeChoice(this, special, Choice(1, "+\$2"), Choice(2, "Discard hands"))
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents
                .forEach { opponent ->
                    opponent.discardHand()
                    opponent.drawCards(4)
                }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        if (choice == 1) {
            player.addUsernameGameLog("chose +\$2")
            player.addCoins(2)
        } else {
            player.addUsernameGameLog("chose to discard hands")

            player.discardHand()

            player.drawCards(4)

            player.triggerAttack(this)
        }
    }

    companion object {
        const val NAME: String = "Minion"
    }
}

