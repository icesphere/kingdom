package com.kingdom.model

import com.kingdom.model.players.Player
import java.util.ArrayList

class PlayerTurn(player: Player) {
    val userId: String = player.userId
    var username = player.username
    val history = ArrayList<String>()
    val lastTurnSummary = player.lastTurnSummary

    val recentLogs: List<String>
        get() {
            val events = history.filter { !it.startsWith("<span class='historyLabel'>") && !it.startsWith("Deck:") && it.isNotBlank() }

            if (events.isEmpty()) {
                return emptyList()
            }

            var numLogsToShow = 3
            if (events.size < numLogsToShow) {
                numLogsToShow = events.size
            }

            return events.reversed().subList(0, numLogsToShow).reversed()
        }

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
