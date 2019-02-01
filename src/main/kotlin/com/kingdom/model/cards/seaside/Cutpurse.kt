package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Copper
import com.kingdom.model.players.Player

class Cutpurse : SeasideCard(NAME, CardType.ActionAttack, 4), AttackCard {

    init {
        addCoins = 2
        special = "Each other player discards a Copper (or reveals a hand with no Copper)."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>, info: Any?) {
        affectedOpponents.forEach { opponent ->
            if (opponent.hand.any { it.isCopper }) {
                opponent.discardCardFromHand(opponent.hand.first { it.isCopper })
                opponent.showInfoMessage("${player.username}'s $cardNameWithBackgroundColor discarded a ${Copper().cardNameWithBackgroundColor} from your hand")
            } else {
                opponent.revealHand()
            }
        }
    }

    companion object {
        const val NAME: String = "Cutpurse"
    }
}

