package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.modifiers.CardCostModifierForCardsInPlay
import com.kingdom.model.players.Player

class Quarry : ProsperityCard(NAME, CardType.Treasure, 4), CardCostModifierForCardsInPlay {

    init {
        isPlayTreasureCardsRequired = true
        addCoins = 1
        special = "While this is in play, Action cards cost \$2 less, but not less than \$0."
    }

    override fun getChangeToCardCost(card: Card, player: Player): Int {
        if (card.isAction) {
            return -2
        }
        return 0
    }

    companion object {
        const val NAME: String = "Quarry"
    }
}

