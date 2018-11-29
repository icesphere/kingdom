package com.kingdom.model

import com.kingdom.model.players.Player
import java.util.ArrayList

class PlayerTurn(player: Player) {
    val userId: String = player.userId
    var username = player.username
    val history = ArrayList<String>()
    val lastTurnSummary = player.lastTurnSummary

    @Suppress("unused")
    val reversedHistory
        get() = history.reversed()

    init {
        val sb = StringBuilder()
        sb.append("<span class='historyLabel'>")
        sb.append(username).append("'s Turn ").append(player.turns + 1)
        sb.append("</span>")
        history.add(sb.toString())
    }

    fun addHistory(message: String) {
        history.add(message)
    }
}
