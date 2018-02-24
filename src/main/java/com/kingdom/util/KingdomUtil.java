package com.kingdom.util;

import com.kingdom.model.Card;
import com.kingdom.model.User;
import com.kingdom.service.LoggedInUsers;
import com.kingdom.service.UAgentInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Jun 10, 2010
 * Time: 8:39:17 PM
 */
public class KingdomUtil {
    public static String getArticleWithWord(String word) {
        if (word.toUpperCase().startsWith("A") || word.toUpperCase().startsWith("E") || word.toUpperCase().startsWith("I") || word.toUpperCase().startsWith("O") || word.toUpperCase().startsWith("U")) {
            return "an " + word;
        }
        return "a " + word;
    }

    public static String getArticleWithCardName(Card card) {
        String cardName = card.getName();
        String cardNameString = getCardWithBackgroundColor(card);
        if (cardName.equals("Goons") || cardName.equals("Nobles")) {
            return cardNameString;
        }
        if (cardName.equals("University")) {
            return "a " + cardNameString;
        }
        if (cardName.toUpperCase().startsWith("A") || cardName.toUpperCase().startsWith("E") || cardName.toUpperCase().startsWith("I") || cardName.toUpperCase().startsWith("O") || cardName.toUpperCase().startsWith("U") || cardName.equals("Herbalist")) {
            return "an " + cardNameString;
        }
        return "a " + cardNameString;
    }

    public static String getWordWithBackgroundColor(String word, String color) {
        if (color.equals(Card.ACTION_AND_VICTORY_IMAGE) || color.equals(Card.TREASURE_AND_VICTORY_IMAGE) || color.equals(Card.TREASURE_AND_CURSE_IMAGE) || color.equals(Card.VICTORY_AND_REACTION_IMAGE) || color.equals(Card.DURATION_AND_VICTORY_IMAGE) || color.equals(Card.TREASURE_REACTION_IMAGE)) {
            return "<span class=\"cardColor\" style=\"background-image:url(images/" + color + ");background-repeat:repeat-x;\">" + word + "</span>";
        } else {
            return "<span class=\"cardColor\" style=\"background-color:" + color + "\">" + word + "</span>";
        }
    }

    public static String getCardWithBackgroundColor(Card card) {
        return getCardWithBackgroundColor(card, false);
    }

    public static String getCardWithBackgroundColor(Card card, boolean addArticle) {
        String cardName;
        if (addArticle) {
            cardName = KingdomUtil.getArticleWithCardName(card);
        } else {
            cardName = card.getName();
        }
        return getWordWithBackgroundColor(cardName, card.getBackgroundColor());
    }

    public static String getPlural(int num, String word) {
        if (num == 1 || num == -1 || word.endsWith("s")) {
            return num + " " + word;
        } else {
            if (word.equals("Envoy")) {
                return num + " " + "Envoys";
            } else if (word.endsWith("y")) {
                word = word.substring(0, word.length() - 1) + "ies";
                return num + " " + word;
            } else if (word.equals("Witch")) {
                return num + " " + "Witches";
            } else if (word.equals("Golden Touch")) {
                return num + " " + "Golden Touches";
            } else {
                return num + " " + word + "s";
            }
        }
    }

    public static String getPlural(double num, String word) {
        if (num == 1) {
            return "1 " + word;
        } else {
            StringBuffer sb = new StringBuffer();
            if (num * 10 % 10 == 0) {
                sb.append((int) num);
            } else {
                sb.append(num);
            }
            if (word.endsWith("y")) {
                word = word.substring(0, word.length() - 1) + "ies";
                sb.append(" ").append(word);
            } else {
                sb.append(" ").append(word);
                if (!word.endsWith("s")) {
                    sb.append("s");
                }
            }
            return sb.toString();
        }
    }

    public static String getCardNames(List<Card> cards) {
        return getCardNames(cards, true);
    }

    public static String getCardNames(List<Card> cards, boolean addColor) {
        if (cards == null || cards.isEmpty()) {
            return "";
        }
        if (cards.size() == 1) {
            if (addColor) {
                return getCardWithBackgroundColor(cards.get(0));
            } else {
                return cards.get(0).getName();
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cards.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            if (i == cards.size() - 1) {
                sb.append("and ");
            }
            if (addColor) {
                sb.append(getCardWithBackgroundColor(cards.get(i)));
            } else {
                sb.append(cards.get(i).getName());
            }
        }
        return sb.toString();
    }

    public static String getCommaSeparatedCardNames(List<Card> cards) {
        List<String> cardNames = new ArrayList<String>(cards.size());
        for (Card card : cards) {
            cardNames.add(card.getName());
        }
        return KingdomUtil.implode(cardNames, ",");
    }

    public static boolean getRequestBoolean(HttpServletRequest request, String name) {
        String param = request.getParameter(name);
        return param != null && param.equals("true");
    }

    public static int getRequestInt(HttpServletRequest request, String name, int defaultValue) {
        String param = request.getParameter(name);
        if (param != null) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static String implode(List<String> strings, String separator) {
        if (strings == null || strings.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (i != 0) {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static List<Card> groupCards(List<Card> cards) {
        CardNameComparator cnc = new CardNameComparator();
        Collections.sort(cards, cnc);
        return cards;
    }

    public static String groupCards(List<Card> cards, boolean addColor) {
        return groupCards(cards, addColor, true);
    }

    public static String groupCards(List<Card> cards, boolean addColor, boolean addNumbers) {
        if (cards == null || cards.isEmpty()) {
            return "";
        }
        CardNameComparator cnc = new CardNameComparator();
        Collections.sort(cards, cnc);
        StringBuffer sb = new StringBuffer();
        int numOfEach = 0;
        Card currentCard = cards.get(0);
        for (Card card : cards) {
            if (!card.getName().equals(currentCard.getName())) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                if (addColor) {
                    if (addNumbers) {
                        sb.append(getWordWithBackgroundColor(getPlural(numOfEach, currentCard.getName()), currentCard.getBackgroundColor()));
                    } else {
                        sb.append(getCardWithBackgroundColor(currentCard));
                    }
                } else {
                    if (addNumbers) {
                        sb.append(getPlural(numOfEach, currentCard.getName()));
                    } else {
                        sb.append(currentCard.getName());
                    }
                }
                numOfEach = 0;
                currentCard = card;
            }
            numOfEach++;
        }
        if (sb.length() > 0) {
            sb.append(", and ");
        }
        if (addColor) {
            if (addNumbers) {
                sb.append(getWordWithBackgroundColor(getPlural(numOfEach, currentCard.getName()), currentCard.getBackgroundColor()));
            } else {
                sb.append(getCardWithBackgroundColor(currentCard));
            }
        } else {
            if (addNumbers) {
                sb.append(getPlural(numOfEach, currentCard.getName()));
            } else {
                sb.append(currentCard.getName());
            }
        }
        return sb.toString();
    }

    public static String getStackTrace(Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

    public static User getUser(HttpServletRequest request) {
        if (request.getSession() == null) {
            return null;
        }
        return (User) request.getSession().getAttribute("user");
    }

    public static boolean isMobile(HttpServletRequest request) {
        if (request.getSession() == null || request.getSession().getAttribute("mobile") == null) {
            UAgentInfo agentInfo = new UAgentInfo(request.getHeader("User-Agent"), request.getHeader("Accept"));
            return agentInfo.detectSmartphone();
        }
        return (Boolean) request.getSession().getAttribute("mobile");
    }

    public static boolean isTablet(HttpServletRequest request) {
        if (request.getSession() == null || request.getSession().getAttribute("tablet") == null) {
            UAgentInfo agentInfo = new UAgentInfo(request.getHeader("User-Agent"), request.getHeader("Accept"));
            return agentInfo.detectTierTablet();
        }
        return (Boolean) request.getSession().getAttribute("tablet");
    }

    public static ModelAndView getLoginModelAndView(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    public static int getRandomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static void logoutUser(User user, HttpServletRequest request) {
        if (user != null) {
            LoggedInUsers.getInstance().userLoggedOut(user);
            LoggedInUsers.getInstance().refreshLobbyPlayers();
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.invalidate();
        }
    }

    public static String getTimeAgo(Date date) {
        StringBuffer gameTime = new StringBuffer("");
        // Get the represented date in milliseconds
        long createTime = date.getTime();
        long currentTime = System.currentTimeMillis();
        double diff = (currentTime - createTime) * 1.0;
        double seconds = (diff / 1000);
        double minutes = (diff / (60 * 1000));
        double hours = (diff / (60 * 60 * 1000));
        double days = (diff / (24 * 60 * 60 * 1000));

        if (days > 1) {
            gameTime.append(KingdomUtil.getPlural((int) days, "day"));
        } else if (hours > 1) {
            gameTime.append(KingdomUtil.getPlural((int) hours, "hour"));
        } else if (minutes > 1) {
            gameTime.append(KingdomUtil.getPlural((int) minutes, "minute"));
        } else {
            gameTime.append(KingdomUtil.getPlural((int) seconds, "second"));
        }

        gameTime.append(" ago");
        return gameTime.toString();
    }

    public static List<Card> uniqueCardList(List<Card> list) {
        Set<Card> set = new HashSet<Card>(list);
        return new ArrayList<Card>(set);
    }

    public static String getLocation(String ipAddress) {
        try {
            Client client = Client.create();
            WebResource webResource = client.resource("http://api.ipinfodb.com/v3/ip-city/");
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            queryParams.add("key", "d2453b713dc82cdc1fddbb28550ef197f8666017107cecfc65ab56311bb69a96");
            queryParams.add("ip", ipAddress);
            String result = webResource.queryParams(queryParams).get(String.class);

            String[] locationValues = result.split(";");

            String location = "";
            location += locationValues[6] + ", " + locationValues[5] + ", " + locationValues[4];

            return location;
        } catch (Exception e) {
            return "Error";
        }
    }
}
