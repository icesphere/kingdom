package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

class Armory : DarkAgesCard(NAME, CardType.Action, 4) {

    init {
        special = "Gain a card onto your deck costing up to \$4."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseSupplyCardToGainToTopOfDeck(4)
    }

    companion object {
        const val NAME: String = "Armory"
    }
}

