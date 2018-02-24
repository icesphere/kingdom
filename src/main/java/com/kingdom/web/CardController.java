package com.kingdom.web;

import com.kingdom.model.Card;
import com.kingdom.model.User;
import com.kingdom.service.CardManager;
import com.kingdom.service.UserManager;
import com.kingdom.util.KingdomUtil;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CardController {

    CardManager cardManager;
    UserManager userManager;

    public CardController(CardManager cardManager, UserManager userManager) {
        this.cardManager = cardManager;
        this.userManager = userManager;
    }

    @RequestMapping("/listCards.html")
    public ModelAndView listCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("cards");
        modelAndView.addObject("kingdomCards", cardManager.getCards(Card.DECK_KINGDOM, true));
        modelAndView.addObject("intrigueCards", cardManager.getCards(Card.DECK_INTRIGUE, true));
        modelAndView.addObject("seasideCards", cardManager.getCards(Card.DECK_SEASIDE, true));
        modelAndView.addObject("alchemyCards", cardManager.getCards(Card.DECK_ALCHEMY, true));
        modelAndView.addObject("prosperityCards", cardManager.getCards(Card.DECK_PROSPERITY, true));
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Card.DECK_CORNUCOPIA, true));
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Card.DECK_HINTERLANDS, true));
        modelAndView.addObject("prizeCards", cardManager.getPrizeCards());
        modelAndView.addObject("promoCards", cardManager.getCards(Card.DECK_PROMO, true));
        modelAndView.addObject("salvationCards", cardManager.getCards(Card.DECK_SALVATION, true));
        modelAndView.addObject("fairyTaleCards", cardManager.getCards(Card.DECK_FAIRYTALE, true));
        modelAndView.addObject("leadersCards", cardManager.getCards(Card.DECK_LEADERS, true));
        modelAndView.addObject("proletariatCards", cardManager.getCards(Card.DECK_PROLETARIAT, true));
        modelAndView.addObject("fanCards", cardManager.getCards(Card.DECK_FAN, true));
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/saveCard.html")
    public ModelAndView saveCard(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        Card card;
        String id = request.getParameter("id");
        if (id.equals("0")) {
            card = new Card();
        } else {
            card = cardManager.getCard(Integer.parseInt(id));
        }
        card.setName(request.getParameter("name"));
        card.setDeck(request.getParameter("deck"));
        card.setType(Integer.parseInt(request.getParameter("type")));
        card.setCost(Integer.parseInt(request.getParameter("cost")));
        card.setCostIncludesPotion(KingdomUtil.getRequestBoolean(request, "costIncludesPotion"));
        card.setSpecial(request.getParameter("special"));
        card.setAddActions(Integer.parseInt(request.getParameter("addActions")));
        card.setAddCoins(Integer.parseInt(request.getParameter("addCoins")));
        card.setAddCards(Integer.parseInt(request.getParameter("addCards")));
        card.setAddBuys(Integer.parseInt(request.getParameter("addBuys")));
        card.setAddVictoryCoins(Integer.parseInt(request.getParameter("addVictoryCoins")));
        card.setVictoryPoints(Integer.parseInt(request.getParameter("victoryPoints")));
        card.setSins(Integer.parseInt(request.getParameter("sins")));
        card.setFruitTokens(Integer.parseInt(request.getParameter("fruitTokens")));
        card.setCattleTokens(Integer.parseInt(request.getParameter("cattleTokens")));
        card.setTesting(KingdomUtil.getRequestBoolean(request, "testing"));
        card.setDisabled(KingdomUtil.getRequestBoolean(request, "disabled"));
        card.setPlayTreasureCards(KingdomUtil.getRequestBoolean(request, "playTreasureCards"));
        card.setPrizeCard(KingdomUtil.getRequestBoolean(request, "prizeCard"));
        card.setFontSize(Integer.parseInt(request.getParameter("fontSize")));
        card.setNameLines(Integer.parseInt(request.getParameter("nameLines")));
        card.setTextSize(Integer.parseInt(request.getParameter("textSize")));
        if (card.getDeck().equals(Card.DECK_SALVATION) || card.getDeck().equals(Card.DECK_FAIRYTALE) || card.getDeck().equals(Card.DECK_LEADERS) || card.getDeck().equals(Card.DECK_PROLETARIAT)) {
            card.setFanExpansionCard(true);
        }
        cardManager.saveCard(card);
        return listCards(request, response);
    }

    @RequestMapping("/showCard.html")
    public ModelAndView showCard(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String id = request.getParameter("id");
        Card card;
        if (id.equals("0")) {
            card = new Card();
        } else {
            card = cardManager.getCard(Integer.parseInt(id));
        }
        ModelAndView modelAndView = new ModelAndView("card");
        BeansWrapper bw = new BeansWrapper();
        Map<Integer, Integer> supply = new HashMap<Integer, Integer>();
        if (card.isVictory()) {
            supply.put(Integer.parseInt(id), 12);
        } else {
            supply.put(Integer.parseInt(id), 10);
        }
        try {
            modelAndView.addObject("supply", bw.wrap(supply));
        } catch (TemplateModelException e) {
            e.printStackTrace();
        }

        modelAndView.addObject("card", card);
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    public void setCardManager(CardManager manager) {
        this.cardManager = manager;
    }

    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    private User getUser(HttpServletRequest request) {
        return KingdomUtil.getUser(request);
    }
}
