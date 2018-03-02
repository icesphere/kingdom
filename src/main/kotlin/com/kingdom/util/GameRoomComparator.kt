package com.kingdom.util

import com.kingdom.model.GameRoom
import java.util.*

class GameRoomComparator : Comparator<GameRoom> {
    override fun compare(g1: GameRoom, g2: GameRoom): Int {
        return g2.game.creationTime.compareTo(g1.game.creationTime)
    }
}
