package com.kingdom.model.cards.supply

import com.kingdom.model.cards.CardType
import com.kingdom.model.players.Player

abstract class VictoryCard(name: String, cost: Int) : SupplyCard(name, CardType.Victory, cost) {

    open fun calculatePoints(player: Player): Int {
        return victoryPoints
    }
}