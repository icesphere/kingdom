package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.actions.TavernCard
import com.kingdom.model.players.Player

class CoinOfTheRealm : AdventuresCard(NAME, CardType.TreasureReserve, 2), TavernCard {

    init {
        addCoins = 1
        isTreasureExcludedFromAutoPlay = true
        special = "When you play this, put it on your Tavern mat. Directly after you finish playing an Action card, you may call this, for +2 Actions."
        fontSize = 8
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.moveCardInPlayToTavern(this)
    }

    override fun isTavernCardActionable(player: Player): Boolean {
        return player.isYourTurn && player.numActionsPlayed > 0 && !player.isCardsBought
    }

    override fun onTavernCardCalled(player: Player) {
        player.addActions(2)
    }

    companion object {
        const val NAME: String = "Coin of the Realm"
    }
}

