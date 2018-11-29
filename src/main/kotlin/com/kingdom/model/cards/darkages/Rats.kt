package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.players.Player

class Rats : DarkAgesCard(NAME, CardType.Action, 4), AfterCardTrashedListenerForSelf {

    init {
        addCards = 1
        addActions = 1
        special = "Gain a Rats. Trash a card from your hand other than a Rats (or reveal a hand of all Rats). When you trash this, +1 Card."
        textSize = 79
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainSupplyCard(Rats(), true)

        if (player.hand.isNotEmpty()) {
            if (player.hand.all { it is Rats }) {
                player.revealHand()
            } else {
                player.trashCardFromHand(false, { c -> c !is Rats })
            }
        } else {
            player.addEventLogWithUsername("'s hand was empty")
        }
    }

    override fun afterCardTrashed(player: Player) {
        player.drawCard()
    }

    companion object {
        const val NAME: String = "Rats"
    }
}

