package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Plan : AdventuresEvent(NAME, 3), ChooseCardActionCard {

    init {
        special = "Move your Trashing token to an Action Supply pile (when you buy a card from that pile, you may trash a card from your hand.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Choose which supply pile to put your Trashing token on", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.trashingTokenSupplyPile = card.pileName
    }

    companion object {
        const val NAME: String = "Plan"
    }
}