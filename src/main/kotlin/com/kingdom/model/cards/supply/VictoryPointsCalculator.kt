package com.kingdom.model.cards.supply

import com.kingdom.model.players.Player

interface VictoryPointsCalculator {

    fun calculatePoints(player: Player): Int
}