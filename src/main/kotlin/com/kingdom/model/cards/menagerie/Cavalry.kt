package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.players.Player

class Cavalry : MenagerieCard(NAME, CardType.Action, 4), AfterCardGainedListenerForSelf, UsesHorses {

    init {
        special = "Gain 2 Horses. When you gain this, +2 Cards, +1 Buy, and if it’s your Buy phase return to your Action phase."
        isPreventAutoEndTurnWhenBought = true
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.gainHorse()
        player.gainHorse()
    }

    override fun afterCardGained(player: Player) {
        player.drawCards(2)
        player.addBuys(1)
        player.returnToActionPhaseIfBuyPhase()
    }

    companion object {
        const val NAME: String = "Cavalry"
    }
}

