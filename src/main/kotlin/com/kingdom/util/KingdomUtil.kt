package com.kingdom.util

import com.kingdom.model.Card
import com.kingdom.model.User
import com.kingdom.service.LoggedInUsers
import com.kingdom.service.UAgentInfo
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.core.util.MultivaluedMapImpl
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.core.MultivaluedMap
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*

object KingdomUtil {
    fun getArticleWithWord(word: String): String {
        return if (word.toUpperCase().startsWith("A") || word.toUpperCase().startsWith("E") || word.toUpperCase().startsWith("I") || word.toUpperCase().startsWith("O") || word.toUpperCase().startsWith("U")) {
            "an " + word
        } else "a " + word
    }

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

    fun getWordWithBackgroundColor(word: String, color: String): String {
        return if (color == Card.ACTION_AND_VICTORY_IMAGE || color == Card.TREASURE_AND_VICTORY_IMAGE || color == Card.TREASURE_AND_CURSE_IMAGE || color == Card.VICTORY_AND_REACTION_IMAGE || color == Card.DURATION_AND_VICTORY_IMAGE || color == Card.TREASURE_REACTION_IMAGE) {
            "<span class=\"cardColor\" style=\"background-image:url(images/$color);background-repeat:repeat-x;\">$word</span>"
        } else {
            "<span class=\"cardColor\" style=\"background-color:$color\">$word</span>"
        }
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

    @JvmOverloads
    fun getCardNames(cards: List<Card>?, addColor: Boolean = true): String {
        if (cards == null || cards.isEmpty()) {
            return ""
        }
        if (cards.size == 1) {
            return if (addColor) {
                getCardWithBackgroundColor(cards[0])
            } else {
                cards[0].name
            }
        }
        val sb = StringBuffer()
        for (i in cards.indices) {
            if (i != 0) {
                sb.append(", ")
            }
            if (i == cards.size - 1) {
                sb.append("and ")
            }
            if (addColor) {
                sb.append(getCardWithBackgroundColor(cards[i]))
            } else {
                sb.append(cards[i].name)
            }
        }
        return sb.toString()
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
                        sb.append(getWordWithBackgroundColor(getPlural(numOfEach, currentCard.name), currentCard.backgroundColor))
                    } else {
                        sb.append(getCardWithBackgroundColor(currentCard))
                    }
                } else {
                    if (addNumbers) {
                        sb.append(getPlural(numOfEach, currentCard.name))
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
                sb.append(getWordWithBackgroundColor(getPlural(numOfEach, currentCard.name), currentCard.backgroundColor))
            } else {
                sb.append(getCardWithBackgroundColor(currentCard))
            }
        } else {
            if (addNumbers) {
                sb.append(getPlural(numOfEach, currentCard.name))
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
            days > 1 -> gameTime.append(KingdomUtil.getPlural(days.toInt(), "day"))
            hours > 1 -> gameTime.append(KingdomUtil.getPlural(hours.toInt(), "hour"))
            minutes > 1 -> gameTime.append(KingdomUtil.getPlural(minutes.toInt(), "minute"))
            else -> gameTime.append(KingdomUtil.getPlural(seconds.toInt(), "second"))
        }

        gameTime.append(" ago")
        return gameTime.toString()
    }

    fun uniqueCardList(list: List<Card>): MutableList<Card> {
        val set = HashSet(list)
        return ArrayList(set)
    }

    fun getLocation(ipAddress: String): String {
        try {
            val client = Client.create()
            val webResource = client.resource("http://api.ipinfodb.com/v3/ip-city/")
            val queryParams = MultivaluedMapImpl()
            queryParams.add("key", "d2453b713dc82cdc1fddbb28550ef197f8666017107cecfc65ab56311bb69a96")
            queryParams.add("ip", ipAddress)
            val result = webResource.queryParams(queryParams).get(String::class.java)

            val locationValues = result.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var location = ""
            location += locationValues[6] + ", " + locationValues[5] + ", " + locationValues[4]

            return location
        } catch (e: Exception) {
            return "Error"
        }

    }
}
