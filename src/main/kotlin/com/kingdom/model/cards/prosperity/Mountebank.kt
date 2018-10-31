package com.kingdom.model.cards.prosperity

import com.kingdom.model.Choice
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Mountebank : ProsperityCard(NAME, CardType.ActionAttack, 5), AttackCard, ChoiceActionCard {

    init {
        addCoins = 2
        special = "Each other player may discard a Curse. If they don’t, they gain a Curse and a Copper."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            if (opponent.hand.any { it.isCurse }) {
                opponent.makeChoice(this, Choice(1, "Discard Curse"), Choice(2, "Gain Curse and Copper"))
            } else {
                opponent.acquireFreeCardFromSupply(Curse(), showLog = true)
                opponent.acquireFreeCardFromSupply(Copper(), showLog = true)
            }
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int) {
        if (choice == 1) {
            player.discardCardFromHand(player.hand.first { it.isCurse })
        } else {
            player.acquireFreeCardFromSupply(Curse(), showLog = true)
            player.acquireFreeCardFromSupply(Copper(), showLog = true)
        }
    }

    companion object {
        const val NAME: String = "Mountebank"
    }
}

