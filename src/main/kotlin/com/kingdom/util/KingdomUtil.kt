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

fun List<Card>?.toCardNames(addColor: Boolean = true): String {
    if (this == null || this.isEmpty()) {
        return ""
    }

    if (this.size == 1) {
        return if (addColor) {
            KingdomUtil.getCardWithBackgroundColor(this[0])
        } else {
            this[0].name
        }
    }
    val sb = StringBuffer()
    for (i in this.indices) {
        if (i != 0) {
            sb.append(", ")
        }
        if (i == this.size - 1) {
            sb.append("and ")
        }
        if (addColor) {
            sb.append(KingdomUtil.getCardWithBackgroundColor(this[i]))
        } else {
            sb.append(this[i].name)
        }
    }
    return sb.toString()
}

object KingdomUtil {
    fun getArticleWithCardName(card: Card): String {
        val cardName = card.name
        val cardNameString = getCardWithBackgroundColor(card)
        if (cardName == "Goons" || cardName == "Nobles") {
            return cardNameString
        }
        if (cardName == "University") {
            return "a " + cardNameString
        }
        return if (cardName.toUpperCase().startsWith("A") || cardName.toUpperCase().startsWith("E") || cardName.toUpperCase().startsWith("I") || cardName.toUpperCase().startsWith("O") || cardName.toUpperCase().startsWith("U") || cardName == "Herbalist") {
            "an " + cardNameString
        } else "a " + cardNameString
    }

    fun getWordWithBackgroundColor(word: String, color: CardColor): String {
        if (color.isImage) {
            return "<span class=\"cardColor\" style=\"background-image:url(images/${color.color});background-repeat:repeat-x;background-position: center;\">$word</span>"
        }
        return "<span class=\"cardColor\" style=\"background-color:${color.color}\">$word</span>"
    }

    @JvmOverloads
    fun getCardWithBackgroundColor(card: Card, addArticle: Boolean = false): String {
        val cardName: String
        if (addArticle) {
            cardName = KingdomUtil.getArticleWithCardName(card)
        } else {
            cardName = card.name
        }
        return getWordWithBackgroundColor(cardName, card.backgroundColor)
    }

    fun getPlural(num: Int, word: String): String {
        var wordCopy = word
        if (num == 1 || num == -1 || wordCopy.endsWith("s")) {
            return num.toString() + " " + wordCopy
        } else {
            if (wordCopy == "Envoy") {
                return num.toString() + " " + "Envoys"
            } else if (wordCopy.endsWith("y")) {
                wordCopy = wordCopy.substring(0, wordCopy.length - 1) + "ies"
                return num.toString() + " " + wordCopy
            } else return if (wordCopy == "Witch") {
                num.toString() + " " + "Witches"
            } else if (wordCopy == "Golden Touch") {
                num.toString() + " " + "Golden Touches"
            } else {
                num.toString() + " " + wordCopy + "s"
            }
        }
    }

    fun getPlural(num: Double, word: String): String {
        var wordCopy = word
        if (num == 1.0) {
            return "1 " + wordCopy
        } else {
            val sb = StringBuffer()
            if (num * 10 % 10 == 0.0) {
                sb.append(num.toInt())
            } else {
                sb.append(num)
            }
            if (wordCopy.endsWith("y")) {
                wordCopy = wordCopy.substring(0, wordCopy.length - 1) + "ies"
                sb.append(" ").append(wordCopy)
            } else {
                sb.append(" ").append(wordCopy)
                if (!wordCopy.endsWith("s")) {
                    sb.append("s")
                }
            }
            return sb.toString()
        }
    }

    fun getCommaSeparatedCardNames(cards: List<Card>): String {
        val cardNames = ArrayList<String>(cards.size)
        for (card in cards) {
            cardNames.add(card.name)
        }
        return KingdomUtil.implode(cardNames, ",")
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

    @JvmOverloads
    fun groupCards(cards: List<Card>?, addColor: Boolean, addNumbers: Boolean = true): String {
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
                    if (addNumbers) {
                        sb.append(currentCard.getNumberPlusNameWithBackgroundColor(numOfEach))
                    } else {
                        sb.append(getCardWithBackgroundColor(currentCard))
                    }
                } else {
                    if (addNumbers) {
                        sb.append(currentCard.name.plural(numOfEach))
                    } else {
                        sb.append(currentCard.name)
                    }
                }
                numOfEach = 0
                currentCard = card
            }
            numOfEach++
        }
        if (sb.isNotEmpty()) {
            sb.append(", and ")
        }
        if (addColor) {
            if (addNumbers) {
                sb.append(currentCard.getNumberPlusNameWithBackgroundColor(numOfEach))
            } else {
                sb.append(getCardWithBackgroundColor(currentCard))
            }
        } else {
            if (addNumbers) {
                sb.append(currentCard.name.plural(numOfEach))
            } else {
                sb.append(currentCard.name)
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
        } else request.session.getAttribute("user") as User?
    }

    fun isMobile(request: HttpServletRequest): Boolean {
        if (request.session == null || request.session.getAttribute("mobile") == null) {
            val agentInfo = UAgentInfo(request.getHeader("User-Agent"), request.getHeader("Accept"))
            return agentInfo.detectSmartphone()
        }
        return request.session.getAttribute("mobile") as Boolean
    }

    fun isTablet(request: HttpServletRequest): Boolean {
        if (request.session == null || request.session.getAttribute("tablet") == null) {
            val agentInfo = UAgentInfo(request.getHeader("User-Agent"), request.getHeader("Accept"))
            return agentInfo.detectTierTablet()
        }
        return request.session.getAttribute("tablet") as Boolean
    }

    fun getAccessModelAndView(request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView("access")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    fun getLoginModelAndView(request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView("login")
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return min + (Math.random() * (max - min + 1)).toInt()
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

    fun uniqueCardList(list: List<Card>): MutableList<Card> {
        val set = HashSet(list)
        return ArrayList(set)
    }

    fun addUsernameCookieToResponse(username: String?, response: HttpServletResponse) {
        val usernameCookie = Cookie("kingdomusername", username)
        usernameCookie.maxAge = 3 * 60 * 60 //3 hours
        response.addCookie(usernameCookie)
    }
}
