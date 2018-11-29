package com.kingdom.model

import com.kingdom.model.players.Player

class PlayerTurn(player: Player) {
    val userId: String = player.userId
    var username = player.username
    val lastTurnSummary = player.lastTurnSummary

    val eventLogs = mutableListOf<String>()
    val infoLogs = mutableListOf<String>()
    val allLogs = mutableListOf<String>()

    val recentEvents: List<String>
        get() {
            if (eventLogs.isEmpty()) {
                return emptyList()
            }

            var numLogsToShow = 3
            if (eventLogs.size < numLogsToShow) {
                numLogsToShow = eventLogs.size
            }

            return eventLogs.reversed().subList(0, numLogsToShow).reversed()
        }

    init {
        val sb = StringBuilder()
        sb.append("<span class='historyLabel'>")
        sb.append(username).append("'s Turn ").append(player.turns + 1)
        sb.append("</span>")
        addInfoLog(sb.toString())
    }

    fun addEventLog(log: String) {
        eventLogs.add(log)
        allLogs.add(log)
    }

    fun addInfoLog(log: String) {
        infoLogs.add(log)
        allLogs.add(log)
    }
}
