package com.kingdom.model

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class LobbyChat(val username: String, val message: String, val time: Date) {
    var userId: String? = null

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)

    val chat: String
        get() {
            val sb = StringBuilder()
            sb.append("<span class='lobbyChat'>")
            sb.append(time.toInstant().atZone(ZoneId.of(ZoneId.getAvailableZoneIds().first { it.contains("Mountain") })).format(timeFormatter))
            if (userId != null) {
                sb.append(" Private message from")
            }
            sb.append(" ").append(username).append(": ")
            sb.append("</span>")
            sb.append(message)
            return sb.toString()
        }

    @Suppress("unused")
    val isExpired: Boolean
        get() {
            val minute = (60 * 1000).toLong()
            return time.time + 15 * minute < System.currentTimeMillis()
        }
}
