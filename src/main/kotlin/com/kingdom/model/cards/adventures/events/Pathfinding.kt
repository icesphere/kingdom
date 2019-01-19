package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChooseCardActionCard
import com.kingdom.model.players.Player

class Pathfinding : AdventuresEvent(NAME, 6), ChooseCardActionCard {

    init {
        special = "Move your +1 Card token to an Action Supply pile. (When you play a card from that pile, you first get +1 Card.)"
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.chooseCardFromSupply("Choose which supply pile to put your +1 Card token on", this, { c -> c.isAction })
    }

    override fun onCardChosen(player: Player, card: Card, info: Any?) {
        player.plusCardTokenSupplyPile = card.pileName
    }

    companion object {
        const val NAME: String = "Pathfinding"
    }
}