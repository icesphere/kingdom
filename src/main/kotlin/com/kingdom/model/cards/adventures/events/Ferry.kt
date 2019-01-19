package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Ferry : AdventuresEvent(NAME, 3), ChooseCardActionCard {

    init {
        special = "Move your -\$2 cost token to an Action Supply pile. (Cards from that pile cost \$2 less on your turns, but not less than \$0.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Choose which supply pile to put your -\$2 cost token on", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.minusTwoCostTokenSupplyPile = card.pileName
    }

    companion object {
        const val NAME: String = "Ferry"
    }
}