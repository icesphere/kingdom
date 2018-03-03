package com.kingdom.web

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Deck
import com.kingdom.model.User
import com.kingdom.model.cards.CardType
import com.kingdom.service.CardManager
import com.kingdom.util.KingdomUtil
import freemarker.ext.beans.BeansWrapper
import freemarker.template.TemplateModelException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class CardController(private var cardManager: CardManager) {

    @RequestMapping("/listCards.html")
    fun listCards(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        return ModelAndView("cards").apply {
            addObject("kingdomCards", cardManager.getCards(Deck.Kingdom, true))
            addObject("intrigueCards", cardManager.getCards(Deck.Intrigue, true))
            addObject("seasideCards", cardManager.getCards(Deck.Seaside, true))
            addObject("alchemyCards", cardManager.getCards(Deck.Alchemy, true))
            addObject("prosperityCards", cardManager.getCards(Deck.Prosperity, true))
            addObject("cornucopiaCards", cardManager.getCards(Deck.Cornucopia, true))
            addObject("hinterlandsCards", cardManager.getCards(Deck.Hinterlands, true))
            addObject("prizeCards", cardManager.prizeCards)
            addObject("promoCards", cardManager.getCards(Deck.Promo, true))
            addObject("salvationCards", cardManager.getCards(Deck.Salvation, true))
            addObject("fairyTaleCards", cardManager.getCards(Deck.FairyTale, true))
            addObject("leadersCards", cardManager.getCards(Deck.Leaders, true))
            addObject("proletariatCards", cardManager.getCards(Deck.Proletariat, true))
            addObject("fanCards", cardManager.getCards(Deck.Fan, true))
            addObject("mobile", KingdomUtil.isMobile(request))
        }
    }

    @RequestMapping("/saveCard.html")
    fun saveCard(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val id = request.getParameter("id")
        val card = if (id == "0") {
            Card()
        } else {
            cardManager.getCard(Integer.parseInt(id))
        }

        with (card) {
            name = request.getParameter("name")
            deck = Deck.valueOf(request.getParameter("deck"))
            type = CardType.fromCardTypeId(request.getParameter("type").toInt())
            cost = Integer.parseInt(request.getParameter("cost"))
            costIncludesPotion = KingdomUtil.getRequestBoolean(request, "costIncludesPotion")
            special = request.getParameter("special")
            addActions = Integer.parseInt(request.getParameter("addActions"))
            addCoins = Integer.parseInt(request.getParameter("addCoins"))
            addCards = Integer.parseInt(request.getParameter("addCards"))
            addBuys = Integer.parseInt(request.getParameter("addBuys"))
            addVictoryCoins = Integer.parseInt(request.getParameter("addVictoryCoins"))
            victoryPoints = Integer.parseInt(request.getParameter("victoryPoints"))
            sins = Integer.parseInt(request.getParameter("sins"))
            fruitTokens = Integer.parseInt(request.getParameter("fruitTokens"))
            cattleTokens = Integer.parseInt(request.getParameter("cattleTokens"))
            testing = KingdomUtil.getRequestBoolean(request, "testing")
            disabled = KingdomUtil.getRequestBoolean(request, "disabled")
            playTreasureCards = KingdomUtil.getRequestBoolean(request, "playTreasureCards")
            prizeCard = KingdomUtil.getRequestBoolean(request, "prizeCard")
            fontSize = Integer.parseInt(request.getParameter("fontSize"))
            nameLines = Integer.parseInt(request.getParameter("nameLines"))
            textSize = Integer.parseInt(request.getParameter("textSize"))
        }

        if (card.deck == Deck.Salvation || card.deck == Deck.FairyTale || card.deck == Deck.Leaders || card.deck == Deck.Proletariat) {
            card.fanExpansionCard = true
        }

        cardManager.saveCard(card)

        return listCards(request, response)
    }

    @RequestMapping("/showCard.html")
    fun showCard(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
        val user = getUser(request)
        if (user == null || !user.admin) {
            return KingdomUtil.getLoginModelAndView(request)
        }
        val id = request.getParameter("id")
        val card = if (id == "0") {
            Card()
        } else {
            cardManager.getCard(Integer.parseInt(id))
        }
        val modelAndView = ModelAndView("card")
        val bw = BeansWrapper()
        val supply = HashMap<Int, Int>()
        if (card.isVictory) {
            supply[Integer.parseInt(id)] = 12
        } else {
            supply[Integer.parseInt(id)] = 10
        }
        try {
            modelAndView.addObject("supply", bw.wrap(supply))
        } catch (e: TemplateModelException) {
            e.printStackTrace()
        }

        modelAndView.addObject("card", card)
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request))
        return modelAndView
    }

    private fun getUser(request: HttpServletRequest): User? {
        return KingdomUtil.getUser(request)
    }
}
