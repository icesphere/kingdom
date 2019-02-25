package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.players.Player

class RoyalCarriage : AdventuresCard(NAME, CardType.ActionReserve, 5), TavernCard {

    init {
        addActions = 1
        special = "Put this on your Tavern mat. Directly after you finish playing an Action card, if it’s still in play, you may call this, to replay that Action."
        fontSize = 10
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        val lastInPlay = player.inPlay.lastOrNull()
        val lastPlayed = player.cardsPlayed.lastOrNull()
        return lastInPlay != null && lastPlayed != null && lastInPlay == lastPlayed && lastInPlay.isAction && !player.isBuyPhase
    }

    override fun onTavernCardCalled(player: Player) {
        val lastPlayed = player.cardsPlayed.last()
        player.addActions(1)
        player.playCard(lastPlayed, repeatedAction = true)
    }

    companion object {
        const val NAME: String = "Royal Carriage"
    }
}

