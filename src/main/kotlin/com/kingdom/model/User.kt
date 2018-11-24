package com.kingdom.model

import com.kingdom.service.LoggedInUsers
import java.util.*

const val SOUND_DEFAULT_ON = 1
const val SOUND_DEFAULT_OFF = 2

class User {

    var userId: String = UUID.randomUUID().toString()

    var username = ""

    var admin: Boolean = false

    var guest: Boolean = false

    var gameId: String? = null

    var soundDefault = SOUND_DEFAULT_ON

    var player2Default = "computer_bmu"

    var player3Default = "computer_hard"

    var player4Default = "none"

    var player5Default = "none"

    var player6Default = "none"

    var baseChecked = true

    var intrigueChecked = true

    var seasideChecked = true

    var prosperityChecked = true

    var cornucopiaChecked = true

    var hinterlandsChecked = true

    var darkAgesChecked = true

    var promoChecked = false

    var baseWeight = 3

    var intrigueWeight = 3

    var seasideWeight = 3

    var prosperityWeight = 3

    var cornucopiaWeight = 3

    var hinterlandsWeight = 3

    var darkAgesWeight = 3

    var promoWeight = 3

    var alwaysPlayTreasureCards: Boolean = false

    var showVictoryPoints: Boolean = false

    var identicalStartingHands: Boolean = false

    var excludedCards = ""

    val excludedCardNames: List<String>
        get() = Arrays.asList(*excludedCards.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

    var lastActivity = Date()

    var lastRefresh: Date? = Date()

    var status = ""

    val refreshLobby = RefreshLobby()

    var isMobile: Boolean = false

    val isExpired: Boolean
        get() {
            if (gameId != null) {
                return false
            }
            val thirtyMinutes = (60 * 1000 * 30).toLong()
            val expired = lastRefresh == null || lastRefresh!!.time + thirtyMinutes < System.currentTimeMillis()
            if (expired) {
                LoggedInUsers.userLoggedOut(this)
                LoggedInUsers.refreshLobbyPlayers()
            }
            return expired
        }

    val isIdle: Boolean
        get() {
            if (gameId != null) {
                return false
            }
            val threeMinutes = (60 * 1000 * 3).toLong()
            return lastActivity.time + threeMinutes < System.currentTimeMillis()
        }

    val idleTime: String
        get() {
            val timeDifference = System.currentTimeMillis() - lastActivity.time
            var minutes = (timeDifference / 1000 / 60).toInt()
            var hours = 0
            if (minutes > 60) {
                hours = minutes / 60
                minutes = minutes - hours * 60
            }
            val sb = StringBuilder()
            if (hours > 0) {
                sb.append(hours).append("h ")
            }
            sb.append(minutes).append("m")
            return sb.toString()
        }

    fun setPlayerDefault(i: Int, selection: String) {
        when (i) {
            2 -> player2Default = selection
            3 -> player3Default = selection
            4 -> player4Default = selection
            5 -> player5Default = selection
            6 -> player6Default = selection
        }
    }

    fun toggleSoundDefault() {
        if (soundDefault == SOUND_DEFAULT_ON) {
            soundDefault = SOUND_DEFAULT_OFF
        } else {
            soundDefault = SOUND_DEFAULT_ON
        }
    }
}
