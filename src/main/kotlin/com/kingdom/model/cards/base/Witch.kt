package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class Witch : BaseCard(NAME, CardType.ActionAttack, 5), AttackCard {

    init {
        addCards = 2
        special = "Each other player gains a Curse card."
        fontSize = 13
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        affectedOpponents.forEach { opponent ->
            val curse = Curse()
            if (opponent.game.isCardAvailableInSupply(curse)) {
                opponent.gainSupplyCard(curse, true)
                opponent.showInfoMessage("You gained a ${curse.cardNameWithBackgroundColor} from ${player.username}'s $cardNameWithBackgroundColor")
            }
        }
    }

    companion object {
        const val NAME: String = "Witch"
    }
}

