package com.kingdom.model.cards.empires

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForSelf
import com.kingdom.model.cards.listeners.AfterCardTrashedListenerForSelf
import com.kingdom.model.cards.supply.Silver
import com.kingdom.model.players.Player

class Rocks : EmpiresCard(NAME, CardType.Treasure, 4), AfterCardGainedListenerForSelf, AfterCardTrashedListenerForSelf {

    init {
        addCoins = 1
        special = "When you gain or trash this, gain a Silver; if it’s your Buy phase, put the Silver on your deck, otherwise put it into your hand. (Rocks is the bottom half of the Encampment pile.)"
        textSize = 95
    }

    override fun afterCardGained(player: Player) {
        gainSilver(player)
    }

    override fun afterCardTrashed(player: Player) {
        gainSilver(player)
    }

    private fun gainSilver(player: Player) {
        val silver = Silver()

        if (player.game.isCardAvailableInSupply(silver)) {
            if (player.isBuyPhase) {
                player.addCardToTopOfDeck(silver, true)
            } else {
                player.addCardToHand(silver, true)
            }
        }
    }

    override val pileName: String
        get() = Catapult.NAME

    companion object {
        const val NAME: String = "Rocks"
    }
}

