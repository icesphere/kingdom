package com.kingdom.model

import java.text.SimpleDateFormat
import java.util.Date

class LobbyChat(val username: String, val message: String, val time: Date) {
    var userId: Int = 0

    val chat: String
        get() {
            val sdf = SimpleDateFormat("h:mm:ss")
            val sb = StringBuilder()
            sb.append("<span class='lobbyChat'>")
            sb.append(sdf.format(time))
            if (userId > 0) {
                sb.append(" Private message from")
            }
            sb.append(" ").append(username).append(": ")
            sb.append("</span>")
            sb.append(message)
            return sb.toString()
        }

    val isExpired: Boolean
        get() {
            val minute = (60 * 1000).toLong()
            return time.time + 15 * minute < System.currentTimeMillis()
        }
}
