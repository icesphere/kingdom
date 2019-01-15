package com.kingdom.model.cards.base

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Artisan : BaseCard(NAME, CardType.Action, 6) {
    init {
        special = "Gain a card to your hand costing up to \$5. Put a card from your hand onto your deck."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainToHandWithMaxCost(5)
        player.addCardFromHandToTopOfDeck()
    }

    companion object {
        const val NAME: String = "Artisan"
    }
}

