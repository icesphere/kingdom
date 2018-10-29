package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Bridge : IntrigueCard(NAME, CardType.Action, 4), CardCostModifier {

    init {
        addBuys = 1
        addCoins = 1
        special = "This turn, cards (everywhere) cost \$1 less, but not less than \$0."
    }

    override fun cardPlayedSpecialAction(player: Player) {
        player.game.currentPlayerCardCostModifiers.add(this)
        player.game.refreshSupply()
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        return -1
    }

    companion object {
        const val NAME: String = "Bridge"
    }
}

