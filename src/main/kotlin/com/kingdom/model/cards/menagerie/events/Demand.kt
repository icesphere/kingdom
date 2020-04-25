package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.menagerie.UsesHorses
import com.kingdom.model.players.Player

class Demand : MenagerieEvent(NAME, 5), UsesHorses {

    init {
        special = "Gain a Horse and a card costing up to \$4, both onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.isNextCardToTopOfDeck = true
        player.gainHorse(false)
        player.chooseSupplyCardToGainToTopOfDeck(4)
    }

    companion object {
        const val NAME: String = "Demand"
    }
}