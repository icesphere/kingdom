package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.cards.supply.VictoryPointsCalculator
import com.kingdom.model.players.Player

class DistantLands : AdventuresCard(NAME, CardType.ActionReserveVictory, 5), TavernCard, VictoryPointsCalculator {

    init {
        special = "Put this on your Tavern mat. Worth 4 VP if on your Tavern mat at the end of the game (otherwise worth 0 VP)."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean = false

    override fun onTavernCardCalled(player: Player) {
        //not applicable
    }

    override fun calculatePoints(player: Player): Int {
        return if (player.tavernCards.contains(this)) 4 else 0
    }

    companion object {
        const val NAME: String = "Distant Lands"
    }
}

