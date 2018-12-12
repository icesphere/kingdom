package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.players.Player

class Teacher : AdventuresCard(NAME, CardType.ActionReserve, 6), TavernCard {

    init {
        special = "Put this on your Tavern mat. At the start of your turn, you may call this, to move your +1 Card, +1 Action, +1 Buy, or +1\$ token to an Action Supply pile you have no tokens on. (When you play a card from that pile, you first get that bonus.) (This is not in the Supply.)"
        textSize = 110
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        return player.isStartOfTurn
    }

    override fun onTavernCardCalled(player: Player) {
        //todo
    }

    companion object {
        const val NAME: String = "Teacher"
    }
}

