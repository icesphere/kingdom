package com.kingdom.util

import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardColor
import com.kingdom.service.LoggedInUsers
import com.kingdom.service.UAgentInfo
import org.springframework.web.servlet.ModelAndView
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val USERNAME_COOKIE = "kingdomusername"

fun String.removeSpaces(): String {
    return replace("\\s".toRegex(), "")
}

fun List<Card>?.toCardNames(addColor: Boolean = true): String {
    if (this == null || this.isEmpty()) {
        return ""
    }

    if (this.size == 1) {
        return if (addColor) {
            this[0].cardNameWithBackgroundColor
        } else {
            this[0].name
        }
    }
    val sb = StringBuffer()
    for (i in this.indices) {
        if (i != 0) {
            sb.append(", ")
        }
        if (i == this.lastIndex) {
            sb.append("and ")
        }
        if (addColor) {
            sb.append(this[i].cardNameWithBackgroundColor)
        } else {
            sb.append(this[i].name)
        }
    }
    return sb.toString()
}

object KingdomUtil {

    fun getWordWithBackgroundColor(word: String, color: CardColor): String {
        if (color.isImage) {
            return "<span class=\"cardColor\" style=\"background-image:url(images/${color.color});background-repeat:repeat-x;background-position: center;\">$word</span>"
        }
        return "<span class=\"cardColor\" style=\"background-color:${color.color}\">$word</span>"
    }

    fun getCommaSeparatedCardNames(cards: List<Card>): String {
        val cardNames = ArrayList<String>(cards.size)
        for (card in cards) {
            cardNames.add(card.name)
        }
        return implode(cardNames, ",")
    }

    fun getRequestBoolean(request: HttpServletRequest, name: String): Boolean {
        val param = request.getParameter(name)
        return param != null && param == "true"
    }

    fun getRequestInt(request: HttpServletRequest, name: String, defaultValue: Int): Int {
        val param = request.getParameter(name)
        if (param != null) {
            return try {
                Integer.parseInt(param)
            } catch (e: NumberFormatException) {
                defaultValue
            }

        }
        return defaultValue
    }

    fun implode(strings: List<String>?, separator: String): String {
        if (strings == null || strings.isEmpty()) {
            return ""
        }
        val sb = StringBuffer()
        for (i in strings.indices) {
            val s = strings[i]
            if (i != 0) {
                sb.append(separator)
            }
            sb.append(s)
        }
        return sb.toString()
    }

    fun groupCards(cards: List<Card>): List<Card> {
        val cnc = CardNameComparator()
        Collections.sort(cards, cnc)
        return cards
    }

    fun groupCards(cards: List<Card>?, addColor: Boolean): String {
        if (cards == null || cards.isEmpty()) {
            return ""
        }
        val cnc = CardNameComparator()
        Collections.sort(cards, cnc)
        val sb = StringBuffer()
        var numOfEach = 0
        var currentCard = cards[0]
        for (card in cards) {
            if (card.name != currentCard.name) {
                if (sb.isNotEmpty()) {
                    sb.append(", ")
                }
                if (addColor) {
                    if (numOfEach == 1) {
                        sb.append(currentCard.cardNameWithBackgroundColor)
                    } else {
                        sb.append(currentCard.getNumberPlusNameWithBackgroundColor(numOfEach))
                    }
                } else {
                    if (numOfEach == 1) {
                        sb.append(currentCard.name)
                    } else {
                        sb.append(currentCard.name.plural(numOfEach))
                    }
                }
                numOfEach = 0
                currentCard = card
            }
            numOfEach++
        }
        if (sb.isNotEmpty()) {
            sb.append(", ")
        }
        if (addColor) {
            if (numOfEach == 1) {
                sb.append(currentCard.cardNameWithBackgroundColor)
            } else {
                sb.append(currentCard.getNumberPlusNameWithBackgroundColor(numOfEach))
            }
        } else {
            if (numOfEach == 1) {
                sb.append(currentCard.name)
            } else {
                sb.append(currentCard.name.plural(numOfEach))
            }
        }
        return sb.toString()
    }

    fun getStackTrace(throwable: Throwable): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        throwable.printStackTrace(printWriter)
        return result.toString()
    }

    fun getUser(request: HttpServletRequest): User? {
        return if (request.session == null) {
            null
        } else {
            val user = request.session.getAttribute("user") as User?
            if (user?.gameId != null) {
                val loggedInUser = LoggedInUsers.getUser(user.userId)
                if (loggedInUser?.gameId == null) {
                    request.session.removeAttribute("gameId")
                }
            }
            user
        }
    }

    fun isMobile(request: HttpServletRequest): Boolean {
        if (request.session == null || request.session.getAttribute("mobile") == null) {
            val agentInfo = UAgentInfo(request.getHeader("User-Agent"), request.getHeader("Accept"))
            return agentInfo.detectSmartphone()
        }
        return request.session.getAttribute("mobile") as Boolean
    }

    fun getAccessModelAndView(request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView("access")
        modelAndView.addObject("mobile", isMobile(request))
        return modelAndView
    }

    fun getLoginModelAndView(request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView("login")
        modelAndView.addObject("mobile", isMobile(request))
        return modelAndView
    }

    fun logoutUser(user: User?, request: HttpServletRequest) {
        if (user != null) {
            LoggedInUsers.userLoggedOut(user)
            LoggedInUsers.refreshLobbyPlayers()
        }
        val session = request.getSession(false)
        if (session != null) {
            session.removeAttribute("user")
            session.removeAttribute("gameId")
            session.invalidate()
        }
    }

    fun getTimeAgo(date: Date): String {
        val gameTime = StringBuffer("")
        // Get the represented date in milliseconds
        val createTime = date.time
        val currentTime = System.currentTimeMillis()
        val diff = (currentTime - createTime) * 1.0
        val seconds = diff / 1000
        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)

        when {
            days > 1 -> gameTime.append("day".plural(days.toInt()))
            hours > 1 -> gameTime.append("hour".plural(hours.toInt()))
            minutes > 1 -> gameTime.append("minute".plural(minutes.toInt()))
            else -> gameTime.append("second".plural(seconds.toInt()))
        }

        gameTime.append(" ago")
        return gameTime.toString()
    }

    fun addUsernameCookieToResponse(username: String, response: HttpServletResponse) {
        val usernameCookie = Cookie(USERNAME_COOKIE, username.removeSpaces().trim())
        usernameCookie.maxAge = 3 * 60 * 60 //3 hours
        response.addCookie(usernameCookie)
    }
}
