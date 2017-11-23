package com.kingdom.web;

import com.kingdom.model.*;
import com.kingdom.service.*;
import com.kingdom.util.KingdomUtil;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class GameController {

    private CardManager cardManager = new CardManager();
    private UserManager userManager = new UserManager();
    private GameManager gameManager = new GameManager();

    @RequestMapping("/createGame.html")
    public ModelAndView createGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }

        Game game = getGame(request);
        if (game == null) {
            game = GameRoomManager.getInstance().getNextAvailableGame();
        }
        if (game == null || GameRoomManager.getInstance().isUpdatingWebsite()) {
            return new ModelAndView("redirect:/showGameRooms.html");
        }

        ModelAndView modelAndView = new ModelAndView("selectCards");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        modelAndView.addObject("createGame", true);
        modelAndView.addObject("title", "Create Game");
        modelAndView.addObject("action", "saveGame.html");

        try {
            request.getSession().setAttribute("gameId", game.getGameId());
            if (user.isGuest()) {
                return new ModelAndView("redirect:/showGameRooms.html");
            }
            game.setCreatorId(user.getUserId());
            game.setCreatorName(user.getUsername());
            LoggedInUsers.getInstance().refreshLobbyGameRooms();
            boolean includeTesting = user.isAdmin();
            addSelectCardsObjects(user, modelAndView, includeTesting);
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/selectCards.html")
    public ModelAndView selectCards(HttpServletRequest request, HttpServletResponse response) {
        User user = new User();

        ModelAndView modelAndView = new ModelAndView("selectCards");
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        modelAndView.addObject("createGame", false);
        modelAndView.addObject("title", "Select Cards");
        modelAndView.addObject("action", "generateCards.html");

        try {
            boolean includeTesting = false;
            addSelectCardsObjects(user, modelAndView, includeTesting);
            return modelAndView;
        } catch (Throwable t) {
            return new ModelAndView("empty");
        }
    }

    private void addSelectCardsObjects(User user, ModelAndView modelAndView, boolean includeTesting) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("kingdomCards", cardManager.getCards(Card.DECK_KINGDOM, includeTesting));
        modelAndView.addObject("intrigueCards", cardManager.getCards(Card.DECK_INTRIGUE, includeTesting));
        modelAndView.addObject("seasideCards", cardManager.getCards(Card.DECK_SEASIDE, includeTesting));
        modelAndView.addObject("alchemyCards", cardManager.getCards(Card.DECK_ALCHEMY, includeTesting));
        modelAndView.addObject("prosperityCards", cardManager.getCards(Card.DECK_PROSPERITY, includeTesting));
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Card.DECK_CORNUCOPIA, includeTesting));
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Card.DECK_HINTERLANDS, includeTesting));
        modelAndView.addObject("promoCards", cardManager.getCards(Card.DECK_PROMO, includeTesting));
        modelAndView.addObject("salvationCards", cardManager.getCards(Card.DECK_SALVATION, includeTesting));
        modelAndView.addObject("fairyTaleCards", cardManager.getCards(Card.DECK_FAIRYTALE, includeTesting));
        modelAndView.addObject("proletariatCards", cardManager.getCards(Card.DECK_PROLETARIAT, includeTesting));
        modelAndView.addObject("fanCards", cardManager.getCards(Card.DECK_FAN, includeTesting));
        modelAndView.addObject("annotatedGames", gameManager.getAnnotatedGames());
        modelAndView.addObject("recentGames", gameManager.getGameHistoryList(user.getUserId(), 10));
        modelAndView.addObject("excludedCards", user.getExcludedCardNames());
        modelAndView.addObject("recommendedSets", gameManager.getRecommendedSets());
    }

    @RequestMapping("/generateCards.html")
    public ModelAndView generateCards(HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
        User user = new User();
        Game game = new Game(-1);

        String generateType = request.getParameter("generateType");
        boolean includeLeaders = KingdomUtil.getRequestBoolean(request, "include_leaders");

        if (includeLeaders) {
            game.setUsingLeaders(true);
            game.setAvailableLeaders(cardManager.getAvailableLeaderCards());
        }

        List<String> decks = new ArrayList<String>();
        List<Card> customSelection = new ArrayList<Card>();
        List<Card> excludedCards = new ArrayList<Card>(0);
        parseCardSelectionRequest(request, user, game, decks, customSelection, excludedCards, generateType);

        setRandomizingOptions(request, game, customSelection, excludedCards, generateType);

        game.setDecks(decks);
        cardManager.setRandomKingdomCards(game);

        user.setExcludedCards(KingdomUtil.getCommaSeparatedCardNames(excludedCards));
        userManager.saveUser(user);

        return showRandomConfirmPage(request, user, game);
    }

    @RequestMapping("/saveGame.html")
    public ModelAndView saveGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {

                String generateType = request.getParameter("generateType");

                int numPlayers = 1;
                int numComputerPlayers = 0;
                int numEasyComputerPlayers = 0;
                int numMediumComputerPlayers = 0;
                int numHardComputerPlayers = 0;
                int numBMUComputerPlayers = 0;

                boolean includeLeaders = KingdomUtil.getRequestBoolean(request, "include_leaders");

                user.setBaseChecked(request.getParameter("deck_kingdom") != null);
                user.setIntrigueChecked(request.getParameter("deck_intrigue") != null);
                user.setSeasideChecked(request.getParameter("deck_seaside") != null);
                user.setAlchemyChecked(request.getParameter("deck_alchemy") != null);
                user.setProsperityChecked(request.getParameter("deck_prosperity") != null);
                user.setCornucopiaChecked(request.getParameter("deck_cornucopia") != null);
                user.setHinterlandsChecked(request.getParameter("deck_hinterlands") != null);
                user.setPromoChecked(request.getParameter("promo_cards") != null);
                user.setSalvationChecked(request.getParameter("deck_salvation") != null);
                user.setFairyTaleChecked(request.getParameter("deck_fairytale") != null);
                user.setProletariatChecked(request.getParameter("deck_proletariat") != null);
                user.setOtherFanCardsChecked(request.getParameter("other_fan_cards") != null);
                user.setLeadersChecked(includeLeaders);
                user.setAlwaysPlayTreasureCards(KingdomUtil.getRequestBoolean(request, "playTreasureCards"));
                user.setShowVictoryPoints(KingdomUtil.getRequestBoolean(request, "showVictoryPoints"));
                user.setIdenticalStartingHands(KingdomUtil.getRequestBoolean(request, "identicalStartingHands"));

                user.setBaseWeight(KingdomUtil.getRequestInt(request, "deck_weight_kingdom", 3));
                user.setIntrigueWeight(KingdomUtil.getRequestInt(request, "deck_weight_intrigue", 3));
                user.setSeasideWeight(KingdomUtil.getRequestInt(request, "deck_weight_seaside", 3));
                user.setAlchemyWeight(KingdomUtil.getRequestInt(request, "deck_weight_alchemy", 3));
                user.setProsperityWeight(KingdomUtil.getRequestInt(request, "deck_weight_prosperity", 3));
                user.setCornucopiaWeight(KingdomUtil.getRequestInt(request, "deck_weight_cornucopia", 3));
                user.setHinterlandsWeight(KingdomUtil.getRequestInt(request, "deck_weight_hinterlands", 3));
                user.setPromoWeight(KingdomUtil.getRequestInt(request, "deck_weight_promo", 3));
                user.setSalvationWeight(KingdomUtil.getRequestInt(request, "deck_weight_salvation", 3));
                user.setFairyTaleWeight(KingdomUtil.getRequestInt(request, "deck_weight_fairytale", 3));
                user.setProletariatWeight(KingdomUtil.getRequestInt(request, "deck_weight_proletariat", 3));
                user.setFanWeight(KingdomUtil.getRequestInt(request, "deck_weight_fan", 3));

                for (int i = 2; i <= 6; i++) {
                    user.setPlayerDefault(i, request.getParameter("player" + i));

                    if (request.getParameter("player" + i).equals("human")) {
                        numPlayers++;
                    } else if (request.getParameter("player" + i).equals("computer_easy")) {
                        numPlayers++;
                        numComputerPlayers++;
                        numEasyComputerPlayers++;
                    } else if (request.getParameter("player" + i).equals("computer_medium")) {
                        numPlayers++;
                        numComputerPlayers++;
                        numMediumComputerPlayers++;
                    } else if (request.getParameter("player" + i).equals("computer_hard")) {
                        numPlayers++;
                        numComputerPlayers++;
                        numHardComputerPlayers++;
                    } else if (request.getParameter("player" + i).equals("computer_bmu")) {
                        numPlayers++;
                        numComputerPlayers++;
                        numBMUComputerPlayers++;
                    }
                }
                game.setNumPlayers(numPlayers);
                game.setNumComputerPlayers(numComputerPlayers);
                game.setNumEasyComputerPlayers(numEasyComputerPlayers);
                game.setNumMediumComputerPlayers(numMediumComputerPlayers);
                game.setNumHardComputerPlayers(numHardComputerPlayers);
                game.setNumBMUComputerPlayers(numBMUComputerPlayers);

                game.setShowVictoryPoints(KingdomUtil.getRequestBoolean(request, "showVictoryPoints"));
                game.setIdenticalStartingHands(KingdomUtil.getRequestBoolean(request, "identicalStartingHands"));

                game.setPlayTreasureCards(KingdomUtil.getRequestBoolean(request, "playTreasureCards"));
                game.setTitle(request.getParameter("title"));
                game.setPrivateGame(KingdomUtil.getRequestBoolean(request, "privateGame"));
                if (game.isPrivateGame()) {
                    game.setPassword(request.getParameter("gamePassword"));
                }
                game.setMobile(KingdomUtil.isMobile(request));

                List<String> decks = new ArrayList<String>();
                List<Card> customSelection = new ArrayList<Card>();
                List<Card> excludedCards = new ArrayList<Card>(0);
                parseCardSelectionRequest(request, user, game, decks, customSelection, excludedCards, generateType);

                if (generateType.equals("annotatedGame") || generateType.equals("recentGame") || generateType.equals("recommendedSet")) {
                    String cards;
                    boolean includePlatinumAndColony = false;
                    if (generateType.equals("annotatedGame")) {
                        AnnotatedGame annotatedGame = gameManager.getAnnotatedGame(Integer.parseInt(request.getParameter("annotatedGameId")));
                        cards = annotatedGame.getCards();
                        includePlatinumAndColony = annotatedGame.isIncludeColonyAndPlatinum();
                        game.setAnnotatedGame(true);
                    } else {
                        if (generateType.equals("recentGame")) {
                            cards = request.getParameter("recentGameCards");
                            game.setRecentGame(true);
                        } else {
                            cards = request.getParameter("recommendedSetCards");
                            game.setRecommendedSet(true);
                        }
                        if (cards.endsWith("Platinum,Colony")) {
                            cards = cards.substring(0, cards.indexOf(",Platinum,Colony"));
                            includePlatinumAndColony = true;
                        }
                    }
                    List<Card> kingdomCards = new ArrayList<Card>();
                    for (String cardString : cards.split(",")) {
                        Card card;
                        if (generateType.equals("annotatedGame")) {
                            card = cardManager.getCard(Integer.parseInt(cardString));
                        } else {
                            card = cardManager.getCard(cardString);
                        }
                        if (card != null) {
                            customSelection.add(card);
                        }
                    }

                    game.setAlwaysIncludeColonyAndPlatinum(includePlatinumAndColony);
                }

                if (includeLeaders) {
                    game.setUsingLeaders(true);
                    game.setAvailableLeaders(cardManager.getAvailableLeaderCards());
                }

                setRandomizingOptions(request, game, customSelection, excludedCards, generateType);

                game.setDecks(decks);
                cardManager.setRandomKingdomCards(game);

                user.setExcludedCards(KingdomUtil.getCommaSeparatedCardNames(excludedCards));
                userManager.saveUser(user);

                return new ModelAndView("redirect:/confirmCards.html");
            }
            return new ModelAndView("redirect:/showGameRooms.html");
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    private void parseCardSelectionRequest(HttpServletRequest request, User user, Game game, List<String> decks, List<Card> customSelection, List<Card> excludedCards, String generateType) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith("deck_") && !name.startsWith("deck_weight_")) {
                String deck = request.getParameter(name);
                int weight = 3;
                if (deck.equals(Card.DECK_KINGDOM)) {
                    weight = user.getBaseWeight();
                } else if (deck.equals(Card.DECK_INTRIGUE)) {
                    weight = user.getIntrigueWeight();
                } else if (deck.equals(Card.DECK_SEASIDE)) {
                    weight = user.getSeasideWeight();
                } else if (deck.equals(Card.DECK_ALCHEMY)) {
                    weight = user.getAlchemyWeight();
                } else if (deck.equals(Card.DECK_PROSPERITY)) {
                    weight = user.getProsperityWeight();
                } else if (deck.equals(Card.DECK_CORNUCOPIA)) {
                    weight = user.getCornucopiaWeight();
                } else if (deck.equals(Card.DECK_HINTERLANDS)) {
                    weight = user.getHinterlandsWeight();
                } else if (deck.equals(Card.DECK_SALVATION)) {
                    weight = user.getSalvationWeight();
                } else if (deck.equals(Card.DECK_FAIRYTALE)) {
                    weight = user.getFairyTaleWeight();
                } else if (deck.equals(Card.DECK_PROLETARIAT)) {
                    weight = user.getProletariatWeight();
                }
                if (weight > 5) {
                    weight = 5;
                }
                for (int i = 0; i < weight; i++) {
                    decks.add(deck);
                }
            } else if (name.startsWith("card_")) {
                int cardId = Integer.parseInt(name.substring(5));
                Card card = cardManager.getCard(cardId);
                customSelection.add(card);
            } else if (name.startsWith("excluded_card_")) {
                int cardId = Integer.parseInt(name.substring(14));
                Card card = cardManager.getCard(cardId);
                excludedCards.add(card);
            }
        }

        String promoCards = request.getParameter("promo_cards");
        if (promoCards != null && promoCards.equals("true")) {
            for (int i = 0; i < user.getPromoWeight(); i++) {
                decks.add(Card.DECK_PROMO);
            }
        }
        String otherFanCards = request.getParameter("other_fan_cards");
        if (otherFanCards != null && otherFanCards.equals("true")) {
            for (int i = 0; i < user.getFanWeight(); i++) {
                decks.add(Card.DECK_FAN);
            }
        }
    }

    private void setRandomizingOptions(HttpServletRequest request, Game game, List<Card> customSelection, List<Card> excludedCards, String generateType) {
        RandomizingOptions options = new RandomizingOptions();

        options.setExcludedCards(excludedCards);

        if (generateType.equals("custom") || generateType.equals("annotatedGame") || generateType.equals("recentGame") || generateType.equals("recommendedSet")) {
            game.setCustom(true);
            options.setThreeToFiveAlchemy(true);
            options.setCustomSelection(customSelection);
            if (KingdomUtil.getRequestBoolean(request, "includeColonyAndPlatinumCards")) {
                game.setAlwaysIncludeColonyAndPlatinum(true);
            }
        } else {
            options.setThreeToFiveAlchemy(KingdomUtil.getRequestBoolean(request, "threeToFiveAlchemy"));
            options.setOneOfEachCost(KingdomUtil.getRequestBoolean(request, "oneOfEachCost"));
            options.setOneWithBuy(KingdomUtil.getRequestBoolean(request, "oneWithBuy"));
            options.setOneWithActions(KingdomUtil.getRequestBoolean(request, "oneWithActions"));
            options.setDefenseForAttack(KingdomUtil.getRequestBoolean(request, "defenseForAttack"));
            game.setAlwaysIncludeColonyAndPlatinum(KingdomUtil.getRequestBoolean(request, "alwaysIncludeColonyAndPlatinum"));
        }
        game.setRandomizingOptions(options);
    }

    @RequestMapping("/confirmCards.html")
    public ModelAndView confirmCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                return showRandomConfirmPage(request, user, game);
            } else {
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    private ModelAndView showRandomConfirmPage(HttpServletRequest request, User user, Game game) throws TemplateModelException {
        boolean includeColonyAndPlatinum = game.isAlwaysIncludeColonyAndPlatinum() || (game.getKingdomCards().get(0).isProsperity() && !game.isNeverIncludeColonyAndPlatinum());
        boolean playTreasureCardsRequired = false;
        for (Card card : game.getKingdomCards()) {
            if (card.isPlayTreasureCards()) {
                playTreasureCardsRequired = true;
            }
        }
        ModelAndView modelAndView = new ModelAndView("randomConfirm");
        modelAndView.addObject("createGame", KingdomUtil.getRequestBoolean(request, "createGame"));
        modelAndView.addObject("player", new Player(user, game));
        modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
        modelAndView.addObject("costDiscount", game.getCostDiscount());
        modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
        modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
        addTrollTokenObjects(game, modelAndView);
        modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
        modelAndView.addObject("cards", game.getKingdomCards());
        modelAndView.addObject("includeColonyAndPlatinum", includeColonyAndPlatinum);
        modelAndView.addObject("playTreasureCardsRequired", playTreasureCardsRequired);
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        modelAndView.addObject("randomizerReplacementCardNotFound", game.isRandomizerReplacementCardNotFound());
        return modelAndView;
    }

    private void addTrollTokenObjects(Game game, ModelAndView modelAndView) throws TemplateModelException {
        modelAndView.addObject("showTrollTokens", game.isShowTrollTokens());
        if (game.isShowTrollTokens()) {
            BeansWrapper bw = new BeansWrapper();
            modelAndView.addObject("trollTokens", bw.wrap(game.getTrollTokens()));
        }
    }

    @RequestMapping("/changeRandomCards.html")
    public ModelAndView changeRandomCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.setRandomKingdomCards(game);
                return confirmCards(request, response);
            } else {
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/swapRandomCard.html")
    public ModelAndView swapRandomCard(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.swapRandomCard(game, Integer.parseInt(request.getParameter("cardId")));
                return confirmCards(request, response);
            } else {
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/swapForTypeOfCard.html")
    public ModelAndView swapForTypeOfCard(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                cardManager.swapForTypeOfCard(game, Integer.parseInt(request.getParameter("cardId")), request.getParameter("cardType"));
                return confirmCards(request, response);
            } else {
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }


    @RequestMapping("/togglePlatinumAndColony.html")
    public ModelAndView togglePlatinumAndColony(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                boolean include = KingdomUtil.getRequestBoolean(request, "include");
                game.setAlwaysIncludeColonyAndPlatinum(include);
                game.setNeverIncludeColonyAndPlatinum(!include);
                return confirmCards(request, response);
            } else {
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/keepRandomCards.html")
    public ModelAndView keepRandomCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_BEING_CONFIGURED) {
                game.setStatus(Game.STATUS_GAME_WAITING_FOR_PLAYERS);
                LoggedInUsers.getInstance().refreshLobbyGameRooms();
                boolean hasBlackMarket = false;
                boolean playTreasureCardsRequired = false;
                boolean includePrizes = false;
                for (Card card : game.getKingdomCards()) {
                    if (card.getName().equals("Black Market")) {
                        hasBlackMarket = true;
                    } else if (card.getName().equals("Tournament") || card.getName().equals("Museum")) {
                        includePrizes = true;
                    }
                    if (card.isPlayTreasureCards()) {
                        playTreasureCardsRequired = true;
                    }
                }
                if (hasBlackMarket) {
                    setBlackMarketCards(game);
                }
                if (playTreasureCardsRequired) {
                    game.setPlayTreasureCards(true);
                }
                if (game.isAlwaysIncludeColonyAndPlatinum() || (game.getKingdomCards().get(0).isProsperity() && !game.isNeverIncludeColonyAndPlatinum())) {
                    game.setIncludeColonyCards(true);
                    game.setIncludePlatinumCards(true);
                }
                if (includePrizes || hasBlackMarket) {
                    game.setPrizeCards(cardManager.getPrizeCards());
                }
                game.setGameManager(gameManager);
                game.init();
                addPlayerToGame(game, user);
            }
            if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                return new ModelAndView("redirect:/showGame.html");
            } else {
                return new ModelAndView("redirect:/showGameRooms.html");
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @SuppressWarnings({"unchecked"})
    private void setBlackMarketCards(Game game) {
        List<Card> allCards = cardManager.getAllCards(false);
        List<Card> blackMarketCards = (List<Card>) CollectionUtils.subtract(allCards, game.getKingdomCards());
        Collections.shuffle(blackMarketCards);
        game.setBlackMarketCards(blackMarketCards);
    }

    @RequestMapping("/cancelGame.html")
    public ModelAndView cancelGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        String gameIdParam = request.getParameter("gameId");
        if (user == null || gameIdParam == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        int gameId = Integer.parseInt(gameIdParam);
        Game game = GameRoomManager.getInstance().getGame(gameId);
        if (game != null && (user.isAdmin() || game.getCreatorId() == user.getUserId())) {
            game.reset();
        }
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    @RequestMapping("/cancelCreateGame.html")
    public ModelAndView cancelCreateGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        game.reset();
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    @RequestMapping("/showGameRooms.html")
    public ModelAndView showGameRooms(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        } else if (user.isExpired()) {
            KingdomUtil.logoutUser(user, request);
            return KingdomUtil.getLoginModelAndView(request);
        }
        Game game = getGame(request);
        try {
            if (game != null && game.getStatus() != Game.STATUS_GAME_WAITING_FOR_PLAYERS && game.getStatus() != Game.STATUS_GAME_FINISHED && game.getPlayerMap().containsKey(user.getUserId())) {
                return new ModelAndView("redirect:/showGame.html");
            }
            user.getRefreshLobby().setRefreshPlayers(false);
            user.getRefreshLobby().setRefreshGameRooms(false);
            user.getRefreshLobby().setRefreshChat(false);
            LoggedInUsers.getInstance().refreshLobby(user);
            ModelAndView modelAndView = new ModelAndView("gameRooms");
            modelAndView.addObject("user", user);
            modelAndView.addObject("players", LoggedInUsers.getInstance().getUsers());
            modelAndView.addObject("gameRooms", GameRoomManager.getInstance().getLobbyGameRooms());
            modelAndView.addObject("chats", LobbyChats.getInstance().getChats());
            modelAndView.addObject("maxGameRoomLimitReached", GameRoomManager.getInstance().maxGameRoomLimitReached());
            modelAndView.addObject("numGamesInProgress", GameRoomManager.getInstance().getGamesInProgress().size());
            modelAndView.addObject("updatingWebsite", GameRoomManager.getInstance().isUpdatingWebsite());
            modelAndView.addObject("updatingMessage", GameRoomManager.getInstance().getUpdatingMessage());
            modelAndView.addObject("showNews", GameRoomManager.getInstance().isShowNews());
            modelAndView.addObject("news", GameRoomManager.getInstance().getNews());

            return modelAndView;
        } catch (Throwable t) {
            if (game != null) {
                GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
                game.logError(error);
            }
            return new ModelAndView("empty");
        }
    }

    private void addPlayerToGame(Game game, User user) {
        user.setGameId(game.getGameId());
        user.setStatus("");
        game.addPlayer(user);
        LoggedInUsers.getInstance().updateUserStatus(user);
        LoggedInUsers.getInstance().refreshLobbyPlayers();
        LoggedInUsers.getInstance().refreshLobbyGameRooms();
    }

    private void removePlayerFromGame(Game game, User user) {
        user.setGameId(0);
        game.removePlayer(user);
        LoggedInUsers.getInstance().updateUser(user);
        LoggedInUsers.getInstance().refreshLobbyPlayers();
        LoggedInUsers.getInstance().refreshLobbyGameRooms();
    }

    @RequestMapping("/leaveGame.html")
    public ModelAndView leaveGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        if (user.getGameId() == 0) {
            return new ModelAndView("redirect:/showGameRooms.html");
        }
        Game game = GameRoomManager.getInstance().getGame(user.getGameId());
        if (game == null || game.getStatus() != Game.STATUS_GAME_WAITING_FOR_PLAYERS) {
            return new ModelAndView("redirect:/showGameRooms.html");
        } else {
            removePlayerFromGame(game, user);
        }
        return new ModelAndView("redirect:/showGameRooms.html");
    }

    @RequestMapping("/joinGame.html")
    public ModelAndView joinGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        if (user.getGameId() != 0) {
            return new ModelAndView("redirect:/showGameRooms.html");
        }
        String gameIdParam = request.getParameter("gameId");
        Game game;
        if (gameIdParam != null) {
            int gameId = Integer.parseInt(gameIdParam);
            game = GameRoomManager.getInstance().getGame(gameId);
        } else {
            game = getGame(request);
        }
        if (game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            if (game.getPlayers().size() == game.getNumPlayers() || game.isPrivateGame()) {
                return showGameRooms(request, response);
            } else {
                if (!game.getPlayerMap().containsKey(user.getUserId())) {
                    if (gameIdParam != null) {
                        request.getSession().setAttribute("gameId", Integer.parseInt(gameIdParam));
                    }
                    addPlayerToGame(game, user);
                }
                if (game.getStatus() == Game.STATUS_GAME_IN_PROGRESS) {
                    return new ModelAndView("redirect:/showGame.html");
                } else {
                    return new ModelAndView("redirect:/showGameRooms.html");
                }
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @SuppressWarnings({"unchecked"})
    @RequestMapping("/joinPrivateGame.html")
    public ModelAndView joinPrivateGame(HttpServletRequest request, HttpServletResponse response) {
        Map model = new HashMap();
        User user = getUser(request);
        if (user == null) {
            model.put("redirectToLogin", true);
            return new ModelAndView("jsonView", model);
        }
        String gameIdParam = request.getParameter("gameId");
        Game game;
        if (gameIdParam != null) {
            int gameId = Integer.parseInt(gameIdParam);
            game = GameRoomManager.getInstance().getGame(gameId);
        } else {
            game = getGame(request);
        }
        if (game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        try {
            String message = "Success";
            String gamePassword = request.getParameter("gamePassword");
            if (gamePassword == null || !gamePassword.equals(game.getPassword())) {
                message = "Invalid Password";
            } else if (game.getPlayers().size() == game.getNumPlayers()) {
                message = "Game Room Full";
            } else if (!game.getPlayerMap().containsKey(user.getUserId())) {
                if (gameIdParam != null) {
                    request.getSession().setAttribute("gameId", Integer.parseInt(gameIdParam));
                }
                addPlayerToGame(game, user);
            }

            model.put("message", message);
            model.put("start", game.getStatus() == Game.STATUS_GAME_IN_PROGRESS);

            return new ModelAndView("jsonView", model);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/showGame.html")
    public ModelAndView showGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null) {
            return KingdomUtil.getLoginModelAndView(request);
        } else if (game == null) {
            return new ModelAndView("redirect:/showGameRooms.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView("game");
            Player player = game.getPlayerMap().get(user.getUserId());
            if (player == null) {
                return showGameRooms(request, response);
            }
            game.refreshAll(player);
            game.closeLoadingDialog(player);
            addGameObjects(game, player, modelAndView, request);
            modelAndView.addObject("user", user);
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @SuppressWarnings({"unchecked"})
    @RequestMapping("/refreshGame.html")
    public ModelAndView refreshGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        Map model = new HashMap();
        if (user == null || game == null) {
            model.put("redirectToLogin", true);
            return new ModelAndView("jsonView", model);
        }
        try {
            Refresh refresh = game.getNeedsRefresh().get(user.getUserId());
            if (refresh == null) {
                model.put("redirectToLobby", true);
                return new ModelAndView("jsonView", model);
            }
            model.put("refreshEndTurn", refresh.isRefreshEndTurn());
            if (refresh.isRefreshEndTurn()) {
                refresh.setRefreshEndTurn(false);
                Player player = game.getPlayerMap().get(user.getUserId());
                if (player.getTurns() > 0) {
                    boolean refreshHandArea = refresh.isRefreshHand() || refresh.isRefreshHandArea() || refresh.isRefreshDiscard();
                    model.put("refreshHandOnEndTurn", refresh.isRefreshHandOnEndTurn());
                    refresh.setRefreshHandOnEndTurn(false);
                    model.put("refreshSupplyOnEndTurn", refresh.isRefreshSupplyOnEndTurn());
                    refresh.setRefreshSupplyOnEndTurn(false);
                }
                model.put("refreshPlayersOnEndTurn", refresh.isRefreshPlayers());
                refresh.setRefreshPlayers(false);
                return new ModelAndView("jsonView", model);
            }
            model.put("refreshGameStatus", refresh.isRefreshGameStatus());
            if (refresh.isRefreshGameStatus()) {
                refresh.setRefreshGameStatus(false);
                model.put("gameStatus", game.getStatus());
                String currentPlayer = String.valueOf(game.getStatus() == Game.STATUS_GAME_IN_PROGRESS && user.getUserId() == game.getCurrentPlayerId());
                model.put("currentPlayer", currentPlayer);
            }
            model.put("closeCardActionDialog", refresh.isCloseCardActionDialog());
            if (refresh.isCloseCardActionDialog()) {
                refresh.setCloseCardActionDialog(false);
            }
            model.put("closeLoadingDialog", refresh.isCloseLoadingDialog());
            if (refresh.isCloseLoadingDialog()) {
                refresh.setCloseLoadingDialog(false);
            }
            int divsToLoad = 0;
            model.put("refreshPlayers", refresh.isRefreshPlayers());
            if (refresh.isRefreshPlayers()) {
                divsToLoad++;
                refresh.setRefreshPlayers(false);
            }
            model.put("refreshSupply", refresh.isRefreshSupply());
            if (refresh.isRefreshSupply()) {
                divsToLoad++;
                refresh.setRefreshSupply(false);
            }
            model.put("refreshPlayingArea", refresh.isRefreshPlayingArea());
            if (refresh.isRefreshPlayingArea()) {
                divsToLoad++;
                refresh.setRefreshPlayingArea(false);
            }
            model.put("refreshCardsPlayed", refresh.isRefreshCardsPlayedDiv());
            if (refresh.isRefreshCardsPlayedDiv()) {
                divsToLoad++;
                refresh.setRefreshCardsPlayedDiv(false);
            }
            model.put("refreshCardsBought", refresh.isRefreshCardsBoughtDiv());
            if (refresh.isRefreshCardsBoughtDiv()) {
                divsToLoad++;
                refresh.setRefreshCardsBoughtDiv(false);
            }
            model.put("refreshHistory", refresh.isRefreshHistory());
            if (refresh.isRefreshHistory()) {
                divsToLoad++;
                refresh.setRefreshHistory(false);
            }
            model.put("refreshHandArea", refresh.isRefreshHandArea());
            if (refresh.isRefreshHandArea()) {
                divsToLoad++;
                refresh.setRefreshHandArea(false);
            }
            model.put("refreshHand", refresh.isRefreshHand());
            if (refresh.isRefreshHand()) {
                divsToLoad++;
                refresh.setRefreshHand(false);
            }
            model.put("refreshDiscard", refresh.isRefreshDiscard());
            if (refresh.isRefreshDiscard()) {
                divsToLoad++;
                refresh.setRefreshDiscard(false);
            }
            model.put("refreshChat", refresh.isRefreshChat());
            if (refresh.isRefreshChat()) {
                divsToLoad++;
                refresh.setRefreshChat(false);
            }
            model.put("refreshCardAction", refresh.isRefreshCardAction());
            if (refresh.isRefreshCardAction()) {
                Player player = game.getPlayerMap().get(user.getUserId());
                if (player.getCardAction() == null) {
                    GameError error = new GameError(GameError.GAME_ERROR, "Card action is null for user: " + player.getUsername() + ", show card action: " + player.isShowCardAction());
                    game.logError(error, false);
                    model.put("refreshCardAction", false);
                } else {
                    model.put("cardActionCardsSize", player.getCardAction().getCards().size());
                    model.put("cardActionNumCards", player.getCardAction().getNumCards());
                    model.put("cardActionType", player.getCardAction().getType());
                    model.put("cardActionWidth", player.getCardAction().getWidth());
                    model.put("cardActionSelectExact", player.getCardAction().isSelectExact());
                    model.put("cardActionSelectUpTo", player.getCardAction().isSelectUpTo());
                    model.put("cardActionSelectAtLeast", player.getCardAction().isSelectAtLeast());
                    divsToLoad++;
                }
                refresh.setRefreshCardAction(false);
            }
            model.put("refreshInfoDialog", refresh.isRefreshInfoDialog());
            if (refresh.isRefreshInfoDialog()) {
                Player player = game.getPlayerMap().get(user.getUserId());
                model.put("infoDialogHideMethod", player.getInfoDialog().getHideMethod());
                model.put("infoDialogWidth", player.getInfoDialog().getWidth());
                model.put("infoDialogHeight", player.getInfoDialog().getHeight());
                model.put("infoDialogTimeout", player.getInfoDialog().getTimeout());
                divsToLoad++;
                refresh.setRefreshInfoDialog(false);
            }
            model.put("playBeep", refresh.isPlayBeep());
            if (refresh.isPlayBeep()) {
                refresh.setPlayBeep(false);
            }
            model.put("refreshTitle", refresh.isRefreshTitle());
            if (refresh.isRefreshTitle()) {
                refresh.setRefreshTitle(false);
                if (game.getStatus() == Game.STATUS_GAME_FINISHED) {
                    model.put("title", "Game Over");
                } else if (game.getCurrentPlayerId() == user.getUserId()) {
                    model.put("title", "Your Turn");
                } else {
                    model.put("title", game.getCurrentPlayer().getUsername() + "'s Turn");
                }
            }
            model.put("divsToLoad", divsToLoad);

            return new ModelAndView("jsonView", model);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/clickCard.html")
    public ModelAndView clickCard(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String clickType = request.getParameter("clickType");
            if (request.getParameter("cardId") != null) {
                int cardId = Integer.parseInt(request.getParameter("cardId"));
                Player player = game.getPlayerMap().get(user.getUserId());
                if (player == null) {
                    return showGameRooms(request, response);
                }
                game.cardClicked(player, clickType, cardId);
                game.closeLoadingDialog(player);
            }
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return refreshGame(request, response);
    }

    @RequestMapping("/playAllTreasureCards.html")
    public ModelAndView playAllTreasureCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        Player player = game.getPlayerMap().get(user.getUserId());
        if (player == null) {
            return showGameRooms(request, response);
        }
        try {
            game.playAllTreasureCards(player);
            game.closeLoadingDialog(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return refreshGame(request, response);
    }

    @RequestMapping("/endTurn.html")
    public ModelAndView endTurn(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            Player player = game.getPlayerMap().get(user.getUserId());
            game.endPlayerTurn(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return refreshGame(request, response);
    }

    @RequestMapping("/submitCardAction.html")
    public ModelAndView submitCardAction(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            //todo error handling if choice is null
            Player player = game.getPlayerMap().get(user.getUserId());
            if (player.getCardAction() != null) {
                if (player.getCardAction().getType() == CardAction.TYPE_INFO) {
                    game.cardActionSubmitted(player, null, null, null, -1);
                } else if (player.getCardAction().getType() == CardAction.TYPE_YES_NO) {
                    if (request.getParameter("answer") == null) {
                        GameError error = new GameError(GameError.GAME_ERROR, "Card Action answer was null");
                        game.logError(error, false);
                        //todo
                    } else {
                        game.cardActionSubmitted(player, null, request.getParameter("answer"), null, -1);
                    }
                } else if (player.getCardAction().getType() == CardAction.TYPE_CHOICES) {
                    if (request.getParameter("choice") == null) {
                        GameError error = new GameError(GameError.GAME_ERROR, "Card Action choice was null");
                        game.logError(error, false);
                        //todo
                    } else {
                        game.cardActionSubmitted(player, null, null, request.getParameter("choice"), -1);
                    }
                } else if (player.getCardAction().getType() == CardAction.TYPE_CHOOSE_NUMBER_BETWEEN || player.getCardAction().getType() == CardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN) {
                    if (request.getParameter("numberChosen") == null) {
                        GameError error = new GameError(GameError.GAME_ERROR, "Card Action number chosen was null");
                        game.logError(error, false);
                        //todo
                    } else {
                        game.cardActionSubmitted(player, null, null, null, Integer.parseInt(request.getParameter("numberChosen")));
                    }
                } else {
                    if (request.getParameter("selectedCards") == null) {
                        GameError error = new GameError(GameError.GAME_ERROR, "Card Action selected cards string was null");
                        game.logError(error, false);
                        //todo
                    } else {
                        String selectedCardsString = request.getParameter("selectedCards");
                        String[] selectedCardsStrings = selectedCardsString.split(",");
                        List<Integer> selectedCardIds = new ArrayList<Integer>();
                        for (String cardId : selectedCardsStrings) {
                            if (!cardId.equals("")) {
                                selectedCardIds.add(Integer.parseInt(cardId));
                            }
                        }
                        game.cardActionSubmitted(player, selectedCardIds, null, null, -1);
                    }
                }
            }
            game.closeLoadingDialog(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return new ModelAndView("empty");
    }

    @RequestMapping("/getPlayersDiv.html")
    public ModelAndView getPlayersDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "playersDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "playersDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            modelAndView.addObject("players", game.getPlayers());
            modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints());
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getSupplyDiv.html")
    public ModelAndView getSupplyDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        return getSupplyDiv(request, user, game, game.getCurrentPlayerId());
    }

    @RequestMapping("/getSupplyDivOnEndTurn.html")
    public ModelAndView getSupplyDivOnEndTurn(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        return getSupplyDiv(request, user, game, 0);
    }

    private ModelAndView getSupplyDiv(HttpServletRequest request, User user, Game game, int currentPlayerId) {
        try {
            String supplyDivTemplate = "supplyDiv";
            if (KingdomUtil.isMobile(request)) {
                supplyDivTemplate = "supplyDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(supplyDivTemplate);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", currentPlayerId);
            modelAndView.addObject("kingdomCards", game.getKingdomCards());
            modelAndView.addObject("supplyCards", game.getSupplyCards());
            try {
                BeansWrapper bw = new BeansWrapper();
                modelAndView.addObject("supply", bw.wrap(game.getSupply()));
                if (game.isShowEmbargoTokens()) {
                    modelAndView.addObject("embargoTokens", bw.wrap(game.getEmbargoTokens()));
                }
                addTrollTokenObjects(game, modelAndView);
                if (game.isTrackTradeRouteTokens()) {
                    modelAndView.addObject("tradeRouteTokenMap", bw.wrap(game.getTradeRouteTokenMap()));
                }
            } catch (TemplateModelException e) {
                //
            }
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens());
            modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens());
            modelAndView.addObject("tradeRouteTokensOnMat", game.getTradeRouteTokensOnMat());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getPreviousPlayerPlayingAreaDiv.html")
    public ModelAndView getPreviousPlayerPlayingAreaDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "playingAreaDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "playingAreaDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getPreviousPlayerId());
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("currentPlayer", game.getPreviousPlayer());
            modelAndView.addObject("user", user);
            modelAndView.addObject("cardsPlayed", game.getPreviousPlayerCardsPlayed());
            modelAndView.addObject("cardsBought", game.getPreviousPlayerCardsBought());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("showPotions", game.isUsePotions());
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getPlayingAreaDiv.html")
    public ModelAndView getPlayingAreaDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "playingAreaDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "playingAreaDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("currentPlayer", game.getCurrentPlayer());
            modelAndView.addObject("user", user);
            modelAndView.addObject("cardsPlayed", game.getCardsPlayed());
            modelAndView.addObject("cardsBought", game.getCardsBought());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("showPotions", game.isUsePotions());
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getCardsPlayedDiv.html")
    public ModelAndView getCardsPlayedDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "cardsPlayedDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "cardsPlayedDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("currentPlayer", game.getCurrentPlayer());
            modelAndView.addObject("user", user);
            modelAndView.addObject("cardsPlayed", game.getCardsPlayed());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getCardsBoughtDiv.html")
    public ModelAndView getCardsBoughtDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "cardsBoughtDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "cardsBoughtDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("currentPlayer", game.getCurrentPlayer());
            modelAndView.addObject("user", user);
            modelAndView.addObject("cardsBought", game.getCardsBought());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("showPotions", game.isUsePotions());
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getHistoryDiv.html")
    public ModelAndView getHistoryDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "historyDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "historyDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            modelAndView.addObject("turnHistory", game.getRecentTurnHistory());
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getHandDiv.html")
    public ModelAndView getHandDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView("handDiv");
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getHandAreaDiv.html")
    public ModelAndView getHandAreaDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        return getHandAreaDiv(request, user, game, game.getCurrentPlayerId());
    }

    @RequestMapping("/getHandAreaDivOnEndTurn.html")
    public ModelAndView getHandAreaDivOnEndTurn(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        return getHandAreaDiv(request, user, game, 0);
    }

    private ModelAndView getHandAreaDiv(HttpServletRequest request, User user, Game game, int currentPlayerId) {
        try {
            String template = "handAreaDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "handAreaDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("showDuration", game.isShowDuration());
            modelAndView.addObject("showIslandCards", game.isShowIslandCards());
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards());
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards());
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage());
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins());
            modelAndView.addObject("showFruitTokens", game.isShowFruitTokens());
            modelAndView.addObject("showCattleTokens", game.isShowCattleTokens());
            modelAndView.addObject("showSins", game.isShowSins());
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins());
            modelAndView.addObject("currentPlayerId", currentPlayerId);
            modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getDurationDiv.html")
    public ModelAndView getDurationDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView("durationDiv");
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getDiscardDiv.html")
    public ModelAndView getDiscardDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "discardDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "discardDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getChatDiv.html")
    public ModelAndView getChatDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            List<ChatMessage> chats = game.getChats();
            String template = "chatDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "chatDivMobile";
                //Collections.reverse(chats);
            }
            ModelAndView modelAndView = new ModelAndView(template);
            modelAndView.addObject("chats", chats);
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getCardActionDiv.html")
    public ModelAndView getCardActionDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            String template = "cardActionDiv";
            if (KingdomUtil.isMobile(request)) {
                template = "cardActionDivMobile";
            }
            ModelAndView modelAndView = new ModelAndView(template);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            if (player.getCardAction().getType() == CardAction.TYPE_SETUP_LEADERS) {
                modelAndView.addObject("kingdomCards", game.getKingdomCards());
                modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards() && game.isIncludePlatinumCards());
            }
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getGameInfoDiv.html")
    public ModelAndView getGameInfoDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView("gameInfoDiv");
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("players", game.getPlayers());
            modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.getTrashedCards(), true));
            modelAndView.addObject("showIslandCards", game.isShowIslandCards());
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards());
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards());
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins());
            modelAndView.addObject("showNativeVillage", game.isShowNativeVillage());
            modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins());
            modelAndView.addObject("showFruitTokens", game.isShowFruitTokens());
            modelAndView.addObject("showCattleTokens", game.isShowCattleTokens());
            modelAndView.addObject("showSins", game.isShowSins());
            modelAndView.addObject("showDuration", game.isShowDuration());
            modelAndView.addObject("showPrizeCards", game.isShowPrizeCards());
            modelAndView.addObject("prizeCards", game.getPrizeCardsString());
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/showGameResults.html")
    public ModelAndView showGameResults(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        ModelAndView modelAndView = new ModelAndView("gameResults");
        try {
            modelAndView.addObject("user", user);
            modelAndView.addObject("gameStatus", game.getStatus());
            modelAndView.addObject("gameEndReason", game.getGameEndReason());
            modelAndView.addObject("winnerString", game.getWinnerString());
            modelAndView.addObject("players", game.getPlayers());
            modelAndView.addObject("turnHistory", game.getRecentTurnHistory());

            modelAndView.addObject("showGarden", game.isShowGardens());
            modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins());
            modelAndView.addObject("showVineyard", game.isShowVineyard());
            modelAndView.addObject("showSilkRoads", game.isShowSilkRoads());
            modelAndView.addObject("showCathedral", game.isShowCathedral());
            modelAndView.addObject("showFairgrounds", game.isShowFairgrounds());
            modelAndView.addObject("showGreatHall", game.isShowGreatHall());
            modelAndView.addObject("showHarem", game.isShowHarem());
            modelAndView.addObject("showDuke", game.isShowDuke());
            modelAndView.addObject("showNobles", game.isShowNobles());
            modelAndView.addObject("showArchbishops", game.isShowArchbishops());
            modelAndView.addObject("showIslandCards", game.isShowIslandCards());
            modelAndView.addObject("showMuseumCards", game.isShowMuseumCards());
            modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards());
            modelAndView.addObject("showColony", game.isIncludeColonyCards());
            modelAndView.addObject("showSins", game.isShowSins());
            modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints());
            modelAndView.addObject("showEnchantedPalace", game.isCheckEnchantedPalace());
            modelAndView.addObject("showHedgeWizard", game.isShowHedgeWizard());
            modelAndView.addObject("showGoldenTouch", game.isShowGoldenTouch());

            modelAndView.addObject("chats", game.getChats());
            modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents());

            modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.getTrashedCards(), true));

            modelAndView.addObject("logId", game.getLogId());

            modelAndView.addObject("showRepeatGameLink", game.isAllComputerOpponents());

            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/getInfoDialogDiv.html")
    public ModelAndView getInfoDialogDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView("infoDialogDiv");
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/exitGame.html")
    public ModelAndView exitGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            user.setGameId(0);
            user.setStats(null);
            LoggedInUsers.getInstance().updateUser(user);
            LoggedInUsers.getInstance().refreshLobbyPlayers();
            Player player = game.getPlayerMap().get(user.getUserId());
            if (player == null) {
                return new ModelAndView("redirect:/showGame.html");
            }
            game.playerExitedGame(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return new ModelAndView("empty");
    }

    @RequestMapping("/quitGame.html")
    public ModelAndView quitGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            if (game.getStatus() == Game.STATUS_GAME_WAITING_FOR_PLAYERS) {
                game.reset();
                return new ModelAndView("redirect:/showGameRooms.html");
            }
            if (game.getStatus() != Game.STATUS_GAME_FINISHED) {
                Player player = game.getPlayerMap().get(user.getUserId());
                game.playerQuitGame(player);
            }
            return refreshGame(request, response);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    private void processChatCommand(User user, String commandString) {
        try {
            String command = commandString.substring(1, commandString.indexOf(" "));
            String remainingString = commandString.substring(command.length() + 2);
            if (command.equalsIgnoreCase("lobby")) {
                sendLobbyChat(user, remainingString);
            } else if (command.equalsIgnoreCase("whisper") || command.equalsIgnoreCase("w")) {
                String username = remainingString.substring(0, remainingString.indexOf(" "));
                String message = remainingString.substring(username.length() + 1);
                User receivingUser = userManager.getUser(username);
                if (receivingUser != null) {
                    sendPrivateChat(user, message, receivingUser.getUserId());
                }
            }
            //todo help command
        } catch (Exception e) {
            //todo display invalid command message    
        }
    }

    private void sendLobbyChat(User user, String message) {
        if (message != null && !message.equals("")) {
            LobbyChats.getInstance().addChat(user, message);
        }
    }

    private void sendPrivateChat(User user, String message, int receivingUserId) {
        if (message != null && !message.equals("") && receivingUserId > 0) {
            User receivingUser = LoggedInUsers.getInstance().getUser(receivingUserId);
            if (receivingUser != null) {
                if (receivingUser.getGameId() > 0) {
                    Game game = GameRoomManager.getInstance().getGame(receivingUser.getGameId());
                    game.addPrivateChat(user, receivingUser, message);
                } else {
                    LobbyChats.getInstance().addPrivateChat(user, receivingUser, message);
                    LoggedInUsers.getInstance().refreshLobbyChat();
                }
            }
        }
    }

    @RequestMapping("/sendChat.html")
    public ModelAndView sendChat(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            Player player = game.getPlayerMap().get(user.getUserId());
            String message = request.getParameter("message");
            if (message != null && !message.equals("")) {
                if (message.startsWith("/")) {
                    processChatCommand(user, message);
                } else {
                    game.addChat(player, message);
                }
            }
            return refreshGame(request, response);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/sendLobbyChat.html")
    public ModelAndView sendLobbyChat(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return new ModelAndView("redirect:/login.html");
        }
        LoggedInUsers.getInstance().updateUser(user);
        String message = request.getParameter("message");
        if (message != null && message.startsWith("/")) {
            processChatCommand(user, message);
        } else {
            sendLobbyChat(user, message);
        }
        LoggedInUsers.getInstance().refreshLobbyChat();
        return refreshLobby(request, response);
    }

    @RequestMapping("/sendPrivateChat.html")
    public ModelAndView sendPrivateChat(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return new ModelAndView("redirect:/login.html");
        }
        String message = request.getParameter("message");
        int receivingUserId = KingdomUtil.getRequestInt(request, "receivingUserId", 0);
        sendPrivateChat(user, message, receivingUserId);
        return refreshLobby(request, response);
    }

    private ModelAndView loadPlayerDialogContainingCards(HttpServletRequest request, HttpServletResponse response, String templateFile) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            ModelAndView modelAndView = new ModelAndView(templateFile);
            Player player = game.getPlayerMap().get(user.getUserId());
            modelAndView.addObject("player", player);
            modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
            modelAndView.addObject("costDiscount", game.getCostDiscount());
            modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
            modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
            addTrollTokenObjects(game, modelAndView);
            modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
            modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
            return modelAndView;
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
            return new ModelAndView("empty");
        }
    }

    @RequestMapping("/loadNativeVillageDialog.html")
    public ModelAndView loadNativeVillageDialog(HttpServletRequest request, HttpServletResponse response) {
        return loadPlayerDialogContainingCards(request, response, "nativeVillageDialog");
    }

    @RequestMapping("/loadIslandCardsDialog.html")
    public ModelAndView loadIslandCardsDialog(HttpServletRequest request, HttpServletResponse response) {
        return loadPlayerDialogContainingCards(request, response, "islandCardsDialog");
    }

    @RequestMapping("/loadMuseumCardsDialog.html")
    public ModelAndView loadMuseumCardsDialog(HttpServletRequest request, HttpServletResponse response) {
        return loadPlayerDialogContainingCards(request, response, "museumCardsDialog");
    }

    @RequestMapping("/loadCityPlannerCardsDialog.html")
    public ModelAndView loadCityPlannerCardsDialog(HttpServletRequest request, HttpServletResponse response) {
        return loadPlayerDialogContainingCards(request, response, "cityPlannerCardsDialog");
    }

    private void addGameObjects(Game game, Player player, ModelAndView modelAndView, HttpServletRequest request) {
        BeansWrapper bw = new BeansWrapper();
        modelAndView.addObject("player", player);
        modelAndView.addObject("kingdomCards", game.getKingdomCards());
        modelAndView.addObject("supplyCards", game.getSupplyCards());
        try {
            modelAndView.addObject("supply", bw.wrap(game.getSupply()));
            if (game.isShowEmbargoTokens()) {
                modelAndView.addObject("embargoTokens", bw.wrap(game.getEmbargoTokens()));
            }
            addTrollTokenObjects(game, modelAndView);
            if (game.isTrackTradeRouteTokens()) {
                modelAndView.addObject("tradeRouteTokenMap", bw.wrap(game.getTradeRouteTokenMap()));
            }
        } catch (TemplateModelException e) {
            //
        }
        modelAndView.addObject("supplySize", game.getSupply().size());
        modelAndView.addObject("players", game.getPlayers());
        modelAndView.addObject("currentPlayer", game.getCurrentPlayer());
        modelAndView.addObject("currentPlayerId", game.getCurrentPlayerId());
        modelAndView.addObject("gameStatus", game.getStatus());
        modelAndView.addObject("cardsPlayed", game.getCardsPlayed());
        modelAndView.addObject("cardsBought", game.getCardsBought());
        modelAndView.addObject("turnHistory", game.getRecentTurnHistory());
        modelAndView.addObject("chats", game.getChats());
        modelAndView.addObject("allComputerOpponents", game.isAllComputerOpponents());
        modelAndView.addObject("costDiscount", game.getCostDiscount());
        modelAndView.addObject("fruitTokensPlayed", game.getFruitTokensPlayed());
        modelAndView.addObject("actionCardDiscount", game.getActionCardDiscount());
        modelAndView.addObject("actionCardsInPlay", game.getActionCardsInPlay());
        modelAndView.addObject("showDuration", game.isShowDuration());
        modelAndView.addObject("showEmbargoTokens", game.isShowEmbargoTokens());
        modelAndView.addObject("showIslandCards", game.isShowIslandCards());
        modelAndView.addObject("showMuseumCards", game.isShowMuseumCards());
        modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards());
        modelAndView.addObject("showNativeVillage", game.isShowNativeVillage());
        modelAndView.addObject("showPirateShipCoins", game.isShowPirateShipCoins());
        modelAndView.addObject("showFruitTokens", game.isShowFruitTokens());
        modelAndView.addObject("showCattleTokens", game.isShowCattleTokens());
        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins());
        modelAndView.addObject("showPotions", game.isUsePotions());
        modelAndView.addObject("playTreasureCards", game.isPlayTreasureCards());
        modelAndView.addObject("showVictoryPoints", game.isShowVictoryPoints());
        modelAndView.addObject("showTradeRouteTokens", game.isTrackTradeRouteTokens());
        modelAndView.addObject("tradeRouteTokensOnMat", game.getTradeRouteTokensOnMat());
        modelAndView.addObject("trashedCards", KingdomUtil.groupCards(game.getTrashedCards(), true));
        modelAndView.addObject("prizeCards", game.getPrizeCardsString());

        modelAndView.addObject("showGarden", game.isShowGardens());
        modelAndView.addObject("showVictoryCoins", game.isShowVictoryCoins());
        modelAndView.addObject("showSins", game.isShowSins());
        modelAndView.addObject("showVineyard", game.isShowVineyard());
        modelAndView.addObject("showSilkRoads", game.isShowSilkRoads());
        modelAndView.addObject("showCathedral", game.isShowCathedral());
        modelAndView.addObject("showFairgrounds", game.isShowFairgrounds());
        modelAndView.addObject("showGreatHall", game.isShowGreatHall());
        modelAndView.addObject("showHarem", game.isShowHarem());
        modelAndView.addObject("showDuke", game.isShowDuke());
        modelAndView.addObject("showNobles", game.isShowNobles());
        modelAndView.addObject("showArchbishops", game.isShowArchbishops());
        modelAndView.addObject("showIslandCards", game.isShowIslandCards());
        modelAndView.addObject("showMuseumCards", game.isShowMuseumCards());
        modelAndView.addObject("showCityPlannerCards", game.isShowCityPlannerCards());
        modelAndView.addObject("showColony", game.isIncludeColonyCards());
        modelAndView.addObject("showEnchantedPalace", game.isCheckEnchantedPalace());
        modelAndView.addObject("showHedgeWizard", game.isShowHedgeWizard());
        modelAndView.addObject("showGoldenTouch", game.isShowGoldenTouch());

        modelAndView.addObject("showPrizeCards", game.isShowPrizeCards());

        modelAndView.addObject("gameEndReason", game.getGameEndReason());
        modelAndView.addObject("winnerString", game.getWinnerString());
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        modelAndView.addObject("showRepeatGameLink", game.isAllComputerOpponents());
        modelAndView.addObject("logId", game.getLogId());
    }

    public void setCardManager(CardManager cardManager) {
        this.cardManager = cardManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private User getUser(HttpServletRequest request) {
        return KingdomUtil.getUser(request);
    }

    private Game getGame(HttpServletRequest request) {
        Object gameId = request.getSession().getAttribute("gameId");
        if (gameId == null) {
            return null;
        }
        return GameRoomManager.getInstance().getGame((Integer) gameId);
    }

    @RequestMapping("/gameHistory.html")
    public ModelAndView gameHistory(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("gameHistory");
        modelAndView.addObject("user", user);
        modelAndView.addObject("games", gameManager.getGameHistoryList());
        return modelAndView;
    }

    @RequestMapping("/gamePlayersHistory.html")
    public ModelAndView gamePlayersHistory(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        int gameId = Integer.parseInt(request.getParameter("gameId"));
        ModelAndView modelAndView = new ModelAndView("gamePlayersHistory");
        modelAndView.addObject("user", user);
        modelAndView.addObject("players", gameManager.getGamePlayersHistory(gameId));
        modelAndView.addObject("gameId", gameId);
        return modelAndView;
    }

    @RequestMapping("/playerGameHistory.html")
    public ModelAndView playerGameHistory(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("gameHistory");
        modelAndView.addObject("user", user);
        int userId = Integer.parseInt(request.getParameter("userId"));
        modelAndView.addObject("games", gameManager.getGameHistoryList(userId));
        return modelAndView;
    }

    @RequestMapping("/gameErrors.html")
    public ModelAndView gameErrors(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("gameErrors");
        modelAndView.addObject("user", user);
        modelAndView.addObject("errors", gameManager.getGameErrors());
        return modelAndView;
    }

    @RequestMapping("/deleteGameError.html")
    public ModelAndView deleteGameError(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        int errorId = Integer.parseInt(request.getParameter("errorId"));
        gameManager.deleteGameError(errorId);
        return gameErrors(request, response);
    }

    @RequestMapping("/showGameLog.html")
    public ModelAndView showGameLog(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("gameLog");
        int logId = KingdomUtil.getRequestInt(request, "logId", -1);
        int gameId = KingdomUtil.getRequestInt(request, "gameId", -1);
        String[] logs = new String[0];
        GameLog log = null;
        if (logId > 0) {
            log = gameManager.getGameLog(logId);
        } else if (gameId > 0) {
            log = gameManager.getGameLogByGameId(gameId);
        }
        boolean logNotFound;
        if (log != null) {
            logNotFound = false;
            logs = log.getLog().split(";");
        } else {
            logNotFound = true;
        }
        modelAndView.addObject("logs", logs);
        modelAndView.addObject("logNotFound", logNotFound);
        return modelAndView;
    }

    @RequestMapping("/changeStatus.html")
    public ModelAndView changeStatus(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return new ModelAndView("redirect:/login.html");
        }
        String status = request.getParameter("status");
        if (status != null) {
            user.setStatus(status);
        }
        LoggedInUsers.getInstance().updateUserStatus(user);
        LoggedInUsers.getInstance().refreshLobbyPlayers();
        return refreshLobby(request, response);
    }

    @RequestMapping("/showLobbyPlayers.html")
    public ModelAndView showLobbyPlayers(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("lobbyPlayers");
        modelAndView.addObject("user", getUser(request));
        modelAndView.addObject("players", LoggedInUsers.getInstance().getUsers());
        return modelAndView;
    }

    @RequestMapping("/getPlayerStatsDiv.html")
    public ModelAndView getPlayerStatsDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null) {
            return new ModelAndView("redirect:/login.html");
        }
        ModelAndView modelAndView = new ModelAndView("playerStatsDiv");
        userManager.calculateGameStats(user);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping("/overallGameStats.html")
    public ModelAndView overallGameStats(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("overallStats");
        OverallStats stats = gameManager.getOverallStats();
        OverallStats todayStats = gameManager.getOverallStatsForToday();
        OverallStats yesterdayStats = gameManager.getOverallStatsForYesterday();
        OverallStats weekStats = gameManager.getOverallStatsForPastWeek();
        OverallStats monthStats = gameManager.getOverallStatsForPastMonth();
        modelAndView.addObject("overallStats", stats);
        modelAndView.addObject("todayStats", todayStats);
        modelAndView.addObject("yesterdayStats", yesterdayStats);
        modelAndView.addObject("weekStats", weekStats);
        modelAndView.addObject("monthStats", monthStats);
        return modelAndView;
    }

    @RequestMapping("/userStats.html")
    public ModelAndView userStats(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("userStats");
        UserStats stats = gameManager.getUserStats();
        modelAndView.addObject("stats", stats);
        return modelAndView;
    }

    @RequestMapping("/annotatedGames.html")
    public ModelAndView annotatedGames(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("annotatedGames");
        List<AnnotatedGame> games = gameManager.getAnnotatedGames();
        modelAndView.addObject("games", games);
        return modelAndView;
    }

    @RequestMapping("/saveAnnotatedGame.html")
    public ModelAndView saveAnnotatedGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }

        List<String> cardsIds = new ArrayList<String>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith("card_")) {
                int cardId = Integer.parseInt(name.substring(5));
                cardsIds.add(String.valueOf(cardId));
            }
        }
        AnnotatedGame game;
        String id = request.getParameter("id");
        if (id.equals("0")) {
            game = new AnnotatedGame();
        } else {
            game = gameManager.getAnnotatedGame(Integer.parseInt(id));
        }
        game.setTitle(request.getParameter("title"));
        game.setCards(KingdomUtil.implode(cardsIds, ","));
        game.setIncludeColonyAndPlatinum(KingdomUtil.getRequestBoolean(request, "includeColonyAndPlatinumCards"));
        gameManager.saveAnnotatedGame(game);
        return annotatedGames(request, response);
    }

    @RequestMapping("/deleteAnnotatedGame.html")
    public ModelAndView deleteAnnotatedGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String id = request.getParameter("id");
        AnnotatedGame game = gameManager.getAnnotatedGame(Integer.parseInt(id));
        gameManager.deleteAnnotatedGame(game);
        return annotatedGames(request, response);
    }

    @SuppressWarnings({"UnusedAssignment"})
    @RequestMapping("/showAnnotatedGame.html")
    public ModelAndView showAnnotatedGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("annotatedGame");
        String id = request.getParameter("id");

        AnnotatedGame game;
        List<String> selectedCards = new ArrayList<String>();
        if (id.equals("0")) {
            game = new AnnotatedGame();
        } else {
            game = gameManager.getAnnotatedGame(Integer.parseInt(id));
            for (String cardId : game.getCards().split(",")) {
                Card card = cardManager.getCard(Integer.parseInt(cardId));
                selectedCards.add(card.getName());
            }
        }

        modelAndView.addObject("user", user);
        modelAndView.addObject("selectedCards", selectedCards);
        modelAndView.addObject("kingdomCards", cardManager.getCards(Card.DECK_KINGDOM, true));
        modelAndView.addObject("intrigueCards", cardManager.getCards(Card.DECK_INTRIGUE, true));
        modelAndView.addObject("seasideCards", cardManager.getCards(Card.DECK_SEASIDE, true));
        modelAndView.addObject("alchemyCards", cardManager.getCards(Card.DECK_ALCHEMY, true));
        modelAndView.addObject("prosperityCards", cardManager.getCards(Card.DECK_PROSPERITY, true));
        modelAndView.addObject("cornucopiaCards", cardManager.getCards(Card.DECK_CORNUCOPIA, true));
        modelAndView.addObject("hinterlandsCards", cardManager.getCards(Card.DECK_HINTERLANDS, true));
        modelAndView.addObject("proletariatCards", cardManager.getCards(Card.DECK_PROLETARIAT, true));
        modelAndView.addObject("promoCards", cardManager.getCards(Card.DECK_PROMO, true));
        modelAndView.addObject("game", game);
        return modelAndView;
    }

    @RequestMapping("/showModifyHand.html")
    public ModelAndView showModifyHand(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        if (!game.isTestGame() && !user.isAdmin()) {
            return new ModelAndView("redirect:/showGame.html");
        }
        Player player = game.getPlayerMap().get(user.getUserId());
        ModelAndView modelAndView = new ModelAndView("modifyHand");
        modelAndView.addObject("user", user);
        modelAndView.addObject("cards", game.getSupplyMap().values());
        modelAndView.addObject("myPlayer", player);
        modelAndView.addObject("players", game.getPlayers());
        return modelAndView;
    }

    @RequestMapping("/modifyHand.html")
    public ModelAndView modifyHand(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        if (!game.isTestGame() && !user.isAdmin()) {
            return new ModelAndView("redirect:/showGame.html");
        }

        for (Player player : game.getPlayers()) {
            String currentHandChoice = request.getParameter("currentHandChoice_" + player.getUserId());
            List<Card> currentCards = new ArrayList<Card>(player.getHand());
            if (currentHandChoice.equals("discard")) {
                for (Card card : currentCards) {
                    player.discardCardFromHand(card);
                }
            } else if (currentHandChoice.equals("trash")) {
                for (Card card : currentCards) {
                    player.removeCardFromHand(card);
                }
            }

            Enumeration parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = (String) parameterNames.nextElement();
                if (name.startsWith("card_") && name.endsWith("_" + player.getUserId())) {
                    String ids = name.substring(5);
                    int cardId = Integer.parseInt(ids.substring(0, ids.indexOf("_")));
                    Card card = game.getSupplyMap().get(cardId);
                    int numCards = KingdomUtil.getRequestInt(request, name, 0);
                    for (int i = 0; i < numCards; i++) {
                        player.addCardToHand(card);
                    }
                }
            }
            game.refreshHand(player);
        }
        return new ModelAndView("redirect:/showGame.html");
    }

    private boolean showGame(Game game, User user) {
        return game != null && game.getStatus() != Game.STATUS_GAME_WAITING_FOR_PLAYERS && game.getStatus() != Game.STATUS_GAME_FINISHED && game.getPlayerMap().containsKey(user.getUserId());
    }

    @SuppressWarnings({"unchecked"})
    @RequestMapping("/refreshLobby.html")
    public ModelAndView refreshLobby(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        RefreshLobby refresh;
        if (user == null) {
            refresh = new RefreshLobby();
            refresh.setRedirectToLogin(true);
        } else {
            refresh = user.getRefreshLobby();
            if (user.isExpired()) {
                KingdomUtil.logoutUser(user, request);
                refresh.setRedirectToLogin(true);
            }
        }
        Game game = getGame(request);
        if (showGame(game, user)) {
            refresh.setStartGame(true);
        }
        Map model = new HashMap();
        model.put("redirectToLogin", refresh.isRedirectToLogin());
        if (refresh.isRedirectToLogin()) {
            refresh.setRedirectToLogin(false);
        }
        model.put("startGame", refresh.isStartGame());
        if (refresh.isStartGame()) {
            refresh.setStartGame(false);
        }
        int divsToLoad = 0;
        model.put("refreshPlayers", refresh.isRefreshPlayers());
        if (refresh.isRefreshPlayers()) {
            divsToLoad++;
            refresh.setRefreshPlayers(false);
        }
        model.put("refreshGameRooms", refresh.isRefreshGameRooms());
        if (refresh.isRefreshGameRooms()) {
            divsToLoad++;
            refresh.setRefreshGameRooms(false);
        }
        model.put("refreshChat", refresh.isRefreshChat());
        if (refresh.isRefreshChat()) {
            divsToLoad++;
            refresh.setRefreshChat(false);
        }
        model.put("divsToLoad", divsToLoad);

        return new ModelAndView("jsonView", model);
    }

    @RequestMapping("/getLobbyPlayersDiv.html")
    public ModelAndView getLobbyPlayersDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        RefreshLobby refresh;
        if (user == null) {
            refresh = new RefreshLobby();
            refresh.setRedirectToLogin(true);
        } else {
            refresh = user.getRefreshLobby();
            if (user.isExpired()) {
                KingdomUtil.logoutUser(user, request);
                refresh.setRedirectToLogin(true);
            }
        }
        Game game = getGame(request);
        if (showGame(game, user)) {
            refresh.setStartGame(true);
        }
        LoggedInUsers.getInstance().refreshLobby(user);
        ModelAndView modelAndView = new ModelAndView("lobbyPlayersDiv");
        modelAndView.addObject("players", LoggedInUsers.getInstance().getUsers());
        return modelAndView;
    }

    @RequestMapping("/getLobbyChatDiv.html")
    public ModelAndView getLobbyChatDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        RefreshLobby refresh;
        if (user == null) {
            refresh = new RefreshLobby();
            refresh.setRedirectToLogin(true);
        } else {
            refresh = user.getRefreshLobby();
            if (user.isExpired()) {
                KingdomUtil.logoutUser(user, request);
                refresh.setRedirectToLogin(true);
            }
        }
        Game game = getGame(request);
        if (showGame(game, user)) {
            refresh.setStartGame(true);
        }
        LoggedInUsers.getInstance().refreshLobby(user);
        String template = "lobbyChatDiv";
        if (KingdomUtil.isMobile(request)) {
            template = "lobbyChatDivMobile";
        }
        ModelAndView modelAndView = new ModelAndView(template);
        modelAndView.addObject("user", user);
        modelAndView.addObject("chats", LobbyChats.getInstance().getChats());
        return modelAndView;
    }

    @RequestMapping("/getLobbyGameRoomsDiv.html")
    public ModelAndView getLobbyGameRoomsDiv(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        RefreshLobby refresh;
        if (user == null) {
            refresh = new RefreshLobby();
            refresh.setRedirectToLogin(true);
        } else {
            refresh = user.getRefreshLobby();
            if (user.isExpired()) {
                KingdomUtil.logoutUser(user, request);
                refresh.setRedirectToLogin(true);
            }
        }
        Game game = getGame(request);
        if (showGame(game, user)) {
            refresh.setStartGame(true);
        }
        LoggedInUsers.getInstance().refreshLobby(user);
        ModelAndView modelAndView = new ModelAndView("lobbyGameRoomsDiv");
        modelAndView.addObject("user", user);
        modelAndView.addObject("gameRooms", GameRoomManager.getInstance().getLobbyGameRooms());
        modelAndView.addObject("maxGameRoomLimitReached", GameRoomManager.getInstance().maxGameRoomLimitReached());
        modelAndView.addObject("numGamesInProgress", GameRoomManager.getInstance().getGamesInProgress().size());
        modelAndView.addObject("updatingWebsite", GameRoomManager.getInstance().isUpdatingWebsite());
        modelAndView.addObject("updatingMessage", GameRoomManager.getInstance().getUpdatingMessage());
        modelAndView.addObject("showNews", GameRoomManager.getInstance().isShowNews());
        modelAndView.addObject("news", GameRoomManager.getInstance().getNews());
        modelAndView.addObject("mobile", KingdomUtil.isMobile(request));
        return modelAndView;
    }

    @RequestMapping("/showGamesInProgress.html")
    public ModelAndView showGamesInProgress(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("gamesInProgress");
        modelAndView.addObject("games", GameRoomManager.getInstance().getGamesInProgress());
        return modelAndView;
    }

    @RequestMapping("/toggleSound.html")
    public ModelAndView toggleSound(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        user.toggleSoundDefault();
        userManager.saveUser(user);
        return new ModelAndView("empty");
    }

    @RequestMapping("/repeatGame.html")
    public ModelAndView repeatGame(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        if (GameRoomManager.getInstance().isUpdatingWebsite()) {
            return new ModelAndView("redirect:/showGameRooms.html");
        }
        game.repeat();
        return new ModelAndView("redirect:/showGame.html");
    }

    @RequestMapping("/showGameCards.html")
    public ModelAndView showGameCards(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        ModelAndView modelAndView = new ModelAndView("gameCards");
        modelAndView.addObject("cards", game.getKingdomCards());
        modelAndView.addObject("prizeCards", game.getPrizeCards());
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards() && game.isIncludePlatinumCards());
        return modelAndView;
    }

    @RequestMapping("/showLeaders.html")
    public ModelAndView showLeaders(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        ModelAndView modelAndView = new ModelAndView("gameCards");
        modelAndView.addObject("cards", game.getAvailableLeaders());
        modelAndView.addObject("prizeCards", game.getPrizeCards());
        modelAndView.addObject("includesColonyAndPlatinum", game.isIncludeColonyCards() && game.isIncludePlatinumCards());
        return modelAndView;
    }

    @RequestMapping("/useFruitTokens.html")
    public ModelAndView useFruitTokens(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            Player player = game.getPlayerMap().get(user.getUserId());
            if (player == null) {
                return showGameRooms(request, response);
            }
            game.showUseFruitTokensCardAction(player);
            game.closeLoadingDialog(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return refreshGame(request, response);
    }

    @RequestMapping("/useCattleTokens.html")
    public ModelAndView useCattleTokens(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        Game game = getGame(request);
        if (user == null || game == null) {
            return new ModelAndView("redirect:/login.html");
        }
        try {
            Player player = game.getPlayerMap().get(user.getUserId());
            if (player == null) {
                return showGameRooms(request, response);
            }
            game.showUseCattleTokensCardAction(player);
            game.closeLoadingDialog(player);
        } catch (Throwable t) {
            GameError error = new GameError(GameError.GAME_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
        return refreshGame(request, response);
    }

    @RequestMapping("/recommendedSets.html")
    public ModelAndView recommendedSets(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("recommendedSets");
        List<RecommendedSet> recommendedSets = gameManager.getRecommendedSets();
        modelAndView.addObject("recommendedSets", recommendedSets);
        return modelAndView;
    }

    @RequestMapping("/saveRecommendedSet.html")
    public ModelAndView saveRecommendedSet(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }

        RecommendedSet set;
        String id = request.getParameter("id");
        if (id.equals("0")) {
            set = new RecommendedSet();
        } else {
            set = gameManager.getRecommendedSet(Integer.parseInt(id));
        }
        set.setName(request.getParameter("name"));
        set.setDeck(request.getParameter("deck"));
        set.setCards(request.getParameter("cards"));
        gameManager.saveRecommendedSet(set);
        return recommendedSets(request, response);
    }

    @RequestMapping("/deleteRecommendedSet.html")
    public ModelAndView deleteRecommendedSet(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        String id = request.getParameter("id");
        RecommendedSet set = gameManager.getRecommendedSet(Integer.parseInt(id));
        gameManager.deleteRecommendedSet(set);
        return recommendedSets(request, response);
    }

    @SuppressWarnings({"UnusedAssignment"})
    @RequestMapping("/showRecommendedSet.html")
    public ModelAndView showRecommendedSet(HttpServletRequest request, HttpServletResponse response) {
        User user = getUser(request);
        if (user == null || !user.isAdmin()) {
            return KingdomUtil.getLoginModelAndView(request);
        }
        ModelAndView modelAndView = new ModelAndView("recommendedSet");
        String id = request.getParameter("id");

        RecommendedSet set;
        List<String> selectedCards = new ArrayList<String>();
        if (id.equals("0")) {
            set = new RecommendedSet();
        } else {
            set = gameManager.getRecommendedSet(Integer.parseInt(id));
        }

        modelAndView.addObject("set", set);
        return modelAndView;
    }
}
