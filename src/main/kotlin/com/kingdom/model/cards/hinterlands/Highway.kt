package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Highway : HinterlandsCard(NAME, CardType.Action, 5), CardCostModifier {

    init {
        testing = true
        addCards = 1
        addActions = 1
        special = "While this is in play, cards cost \$1 less, but not less than \$0."
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        return -1
    }

    companion object {
        const val NAME: String = "Highway"
    }
}

