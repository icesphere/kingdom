package com.kingdom.model.cards.renaissance.projects

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.modifiers.CardCostModifier
import com.kingdom.model.players.Player

class Canal : RenaissanceProject(NAME, 7), CardCostModifier {

    init {
        special = "During your turns, cards cost \$1 less, but not less than \$0."
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int = -1

    companion object {
        const val NAME: String = "Canal"
    }
}