package com.kingdom.model

import com.kingdom.service.LoggedInUsers

import javax.persistence.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date

@Table(name = "users")
@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    var userId: Int = 0

    var username = ""

    var password = ""

    var admin: Boolean = false

    var guest: Boolean = false

    var gender = MALE

    @Column(name = "last_login")
    var lastLogin: Date? = null
        get() {
            if (field == null) {
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                try {
                    this.lastLogin = sdf.parse("09/09/1999")
                } catch (e: ParseException) {
                }

            }
            return field
        }

    var email = ""

    var invisible: Boolean = false

    @Column(name = "creation_date")
    var creationDate: Date? = null

    var logins: Int = 0

    @Column(name = "sound_default")
    var soundDefault = SOUND_DEFAULT_ON

    @Column(name = "change_password")
    var changePassword: Boolean = false

    @Column(name = "player2_default")
    var player2Default = "computer_bmu"

    @Column(name = "player3_default")
    var player3Default = "computer_hard"

    @Column(name = "player4_default")
    var player4Default = "none"

    @Column(name = "player5_default")
    var player5Default = "none"

    @Column(name = "player6_default")
    var player6Default = "none"

    @Column(name = "base_checked")
    var baseChecked = true

    @Column(name = "intrigue_checked")
    var intrigueChecked = true

    @Column(name = "seaside_checked")
    var seasideChecked = true

    @Column(name = "alchemy_checked")
    var alchemyChecked = true

    @Column(name = "prosperity_checked")
    var prosperityChecked = true

    @Column(name = "cornucopia_checked")
    var cornucopiaChecked = true

    @Column(name = "hinterlands_checked")
    var hinterlandsChecked = true

    @Column(name = "promo_checked")
    var promoChecked = false

    @Column(name = "salvation_checked")
    var salvationChecked = false

    @Column(name = "fairy_tale_checked")
    var fairyTaleChecked = false

    @Column(name = "leaders_checked")
    var leadersChecked = false

    @Column(name = "proletariat_checked")
    var proletariatChecked = false

    @Column(name = "other_fan_cards_checked")
    var otherFanCardsChecked = false

    @Column(name = "base_weight")
    var baseWeight = 3

    @Column(name = "intrigue_weight")
    var intrigueWeight = 3

    @Column(name = "seaside_weight")
    var seasideWeight = 3

    @Column(name = "alchemy_weight")
    var alchemyWeight = 3

    @Column(name = "prosperity_weight")
    var prosperityWeight = 3

    @Column(name = "cornucopia_weight")
    var cornucopiaWeight = 3

    @Column(name = "hinterlands_weight")
    var hinterlandsWeight = 3

    @Column(name = "promo_weight")
    var promoWeight = 3

    @Column(name = "salvation_weight")
    var salvationWeight = 3

    @Column(name = "fairy_tale_weight")
    var fairyTaleWeight = 3

    @Column(name = "proletariat_weight")
    var proletariatWeight = 3

    @Column(name = "fan_weight")
    var fanWeight = 3

    @Column(name = "always_play_treasure_cards")
    var alwaysPlayTreasureCards: Boolean = false

    @Column(name = "show_victory_points")
    var showVictoryPoints: Boolean = false

    @Column(name = "identical_starting_hands")
    var identicalStartingHands: Boolean = false

    var active: Boolean = false

    @Column(name = "excluded_cards")
    var excludedCards = ""

    @Column(name = "user_agent")
    var userAgent = ""

    @Column(name = "ipaddress")
    var ipAddress = ""

    var location = ""

    @Transient
    var gameId: Int = 0

    @Transient
    var lastActivity = Date()

    @Transient
    var lastRefresh: Date? = Date()

    @Transient
    var status = ""

    @Transient
    var stats: PlayerStats? = null

    @Transient
    val refreshLobby = RefreshLobby()

    @Transient
    var isMobile: Boolean = false

    val isExpired: Boolean
        get() {
            if (gameId > 0) {
                return false
            }
            val thirtyMinutes = (60 * 1000 * 30).toLong()
            val expired = lastRefresh == null || lastRefresh!!.time + thirtyMinutes < System.currentTimeMillis()
            if (expired) {
                LoggedInUsers.instance.userLoggedOut(this)
                LoggedInUsers.instance.refreshLobbyPlayers()
            }
            return expired
        }

    val isIdle: Boolean
        get() {
            if (gameId > 0) {
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

    val excludedCardNames: List<String>
        get() = Arrays.asList(*excludedCards.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

    fun incrementLogins() {
        logins++
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

    companion object {
        const val MALE = "M"
        const val FEMALE = "F"
        const val UNKNOWN = "U"
        const val COMPUTER = "C"

        const val SOUND_DEFAULT_ON = 1
        const val SOUND_DEFAULT_OFF = 2
    }
}
