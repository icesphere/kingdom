package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Embassy : HinterlandsCard(NAME, CardType.Action, 5), AfterCardGainedListenerForSelf {

    init {
        addCards = 5
        special = "Discard 3 cards. When you gain this, each other player gains a Silver."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.discardCardsFromHand(3, false)
    }

    override fun afterCardGained(player: Player) {
        for (opponent in player.opponentsInOrder) {
            opponent.showInfoMessage("You gained a ${Silver().cardNameWithBackgroundColor} when ${player.username} gained an $cardNameWithBackgroundColor")
            opponent.gainSupplyCard(Silver(), showLog = true)
        }
    }

    companion object {
        const val NAME: String = "Embassy"
    }
}

