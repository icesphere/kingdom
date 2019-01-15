package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.cards.supply.Gold
import com.kingdom.model.players.Player

class Soothsayer : GuildsCard(NAME, CardType.ActionAttack, 5), AttackCard {

    init {
        special = "Gain a Gold. Each other player gains a Curse, and if they did, draws a card."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Gold(), true)
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            val curse = Curse()
            if (player.game.isCardAvailableInSupply(curse)) {
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor gave you a ${curse.cardNameWithBackgroundColor} and you drew 1 Card")
                opponent.gainSupplyCard(curse, true)
                opponent.drawCard()
            }
        }
    }

    companion object {
        const val NAME: String = "Soothsayer"
    }
}

