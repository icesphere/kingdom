package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.AttackCard
import com.kingdom.model.cards.supply.Curse
import com.kingdom.model.players.Player

class SeaHag : SeasideCard(NAME, CardType.ActionAttack, 4), AttackCard {

    init {
        special = "Each other player discards the top card of their deck, then gains a Curse onto their deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.triggerAttack(this)
    }

    override fun resolveAttack(player: Player, affectedOpponents: List<Player>) {
        for (opponent in affectedOpponents) {
            opponent.discardTopCardOfDeck()
            opponent.acquireFreeCardFromSupply(Curse(), false, CardLocation.Deck)
        }
    }

    companion object {
        const val NAME: String = "Sea Hag"
    }
}

