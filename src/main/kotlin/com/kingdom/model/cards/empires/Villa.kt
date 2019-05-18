package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Villa : EmpiresCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf {

    init {
        addActions = 2
        addBuys = 1
        addCoins = 1
        special = "When you gain this, put it into your hand, +1 Action, and if it’s your Buy phase return to your Action phase."
        isPreventAutoEndTurnWhenBought = true
    }

    override fun afterCardGained(player: Player) {
        if (!player.hand.contains(this)) {
            player.removeCardFromDiscard(this)
            player.removeCardFromDeck(this)
            player.addActions(1)
            player.addCardToHand(this, true)
            if (player.isBuyPhase) {
                player.isReturnToActionPhase = true
                player.isTreasuresPlayable = true
                player.refreshCardsBought()
                player.refreshSupply()
                player.addEventLogWithUsername("returned to Action phase")
            }
        }
    }

    companion object {
        const val NAME: String = "Villa"
    }
}

