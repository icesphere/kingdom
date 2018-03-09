package com.kingdom.model

import java.util.ArrayList

class PlayerTurn(player: OldPlayer) {
    val userId: Int = player.userId
    var username = player.username
    val history = ArrayList<String>()

    init {
        val sb = StringBuilder()
        sb.append("<span style='font-weight:bold'>")
        sb.append(username).append("'s Turn ").append(player.turns + 1)
        history.add(sb.toString())
    }

    fun addHistory(message: String) {
        history.add(message)
    }
}
