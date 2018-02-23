package com.kingdom.model;

import com.kingdom.model.computer.*;
import com.kingdom.service.GameManager;
import com.kingdom.service.LoggedInUsers;
import com.kingdom.util.CardCostComparator;
import com.kingdom.util.DurationHandler;
import com.kingdom.util.KingdomUtil;
import com.kingdom.util.cardaction.CardActionHandler;
import com.kingdom.util.cardaction.NextActionHandler;
import com.kingdom.util.specialaction.*;

import java.util.*;

public class Game {
    public static final int STATUS_NO_GAMES = 0;
    public static final int STATUS_GAME_BEING_CONFIGURED = 1;
    public static final int STATUS_GAME_WAITING_FOR_PLAYERS = 2;
    public static final int STATUS_GAME_IN_PROGRESS = 3;
    public static final int STATUS_GAME_FINISHED = 4;

    private int status;
    private int numPlayers;
    private int numComputerPlayers;
    private int numEasyComputerPlayers;
    private int numMediumComputerPlayers;
    private int numHardComputerPlayers;
    private int numBMUComputerPlayers;
    private boolean allComputerOpponents;
    private List<Player> players = new ArrayList<Player>(6);
    private Map<Integer, Player> playerMap = new HashMap<Integer, Player>(6);
    private List<Card> kingdomCards = new ArrayList<Card>();
    private Map<String, Card> kingdomCardMap = new HashMap<String, Card>(10);
    private List<Card> supplyCards = new ArrayList<Card>();
    private Map<Integer, Card> cardMap = new HashMap<Integer, Card>();
    private Map<Integer, Card> supplyMap = new HashMap<Integer, Card>();
    private Map<Integer, Integer> supply = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> embargoTokens = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> trollTokens = new HashMap<Integer, Integer>();
    private int currentPlayerIndex = 0;
    private int currentPlayerId = -1;
    private List<Card> trashedCards = new ArrayList<Card>();
    private LinkedList<Card> cardsPlayed = new LinkedList<Card>();
    private List<Card> cardsBought = new ArrayList<Card>();
    private Map<Integer, Refresh> needsRefresh = new HashMap<Integer, Refresh>(6);
    private boolean finishGameOnNextEndTurn;
    private Deque<RepeatedAction> repeatedActions = new ArrayDeque<RepeatedAction>(3);
    private Deque<Card> golemActions = new ArrayDeque<Card>(0);
    private List<Card> trashedTreasureCards = new ArrayList<Card>(6);
    private List<PlayerTurn> turnHistory = new ArrayList<PlayerTurn>();
    private LinkedList<PlayerTurn> recentTurnHistory = new LinkedList<PlayerTurn>();
    private PlayerTurn currentTurn;
    private List<Card> setAsideCards = new ArrayList<Card>();
    private Set<Integer> playersExited = new HashSet<Integer>(6);
    private List<ChatMessage> chats = new ArrayList<ChatMessage>();
    private List<String> colors = new ArrayList<String>(6);
    private Map<Integer, Card> masqueradeCards = new HashMap<Integer, Card>(0);
    private int currentColorIndex = 0;
    private int costDiscount = 0;
    private int numActionsCardsPlayed = 0;
    private int actionCardsInPlay = 0;
    private boolean lighthousePlayed;
    private boolean showGardens;
    private boolean showFarmlands;
    private boolean showVictoryCoins;
    private boolean showVineyard;
    private boolean showSilkRoads;
    private boolean showCathedral;
    private boolean showFairgrounds;
    private boolean showGreatHall;
    private boolean showHarem;
    private boolean showDuke;
    private boolean showNobles;
    private boolean showArchbishops;
    private boolean showDuration;
    private boolean showEmbargoTokens;
    private boolean showTrollTokens;
    private boolean showIslandCards;
    private boolean showMuseumCards;
    private boolean showCityPlannerCards;
    private boolean showNativeVillage;
    private boolean showPirateShipCoins;
    private boolean showFruitTokens;
    private boolean showCattleTokens;
    private boolean showHedgeWizard;
    private boolean showGoldenTouch;
    private boolean showSins;
    private List<Card> durationCardsPlayed = new ArrayList<Card>(0);
    private boolean outpostTurn;
    private boolean outpostCardPlayed;
    private List<Card> smugglersCards = new ArrayList<Card>(0);
    private List<Card> smugglersCardsGained = new ArrayList<Card>(0);
    private boolean trackSmugglersCards;
    private boolean boughtVictoryCard;
    private boolean trackTreasuryCards;
    private boolean trackAlchemistCards;
    private boolean trackHerbalistCards;
    private boolean playedTreasuryCard;
    private boolean playedAlchemistCard;
    private boolean playedHerbalistCard;
    private boolean checkSecretChamber;
    private boolean checkHorseTraders;
    private boolean checkBellTower;
    private boolean checkEnchantedPalace;
    private Card attackCard;
    private Card throneRoomCard;
    private Card kingsCourtCard;
    private List<Card> blackMarketCards = new ArrayList<Card>(0);
    private List<String> decks = new ArrayList<String>(5);
    private boolean usePotions;
    private int potionsPlayed;
    private List<Card> treasureCardsPlayed = new ArrayList<Card>(5);
    private List<Card> blackMarketTreasureCardsPlayed = new ArrayList<Card>(5);
    private Queue<Card> blackMarketTreasureQueue = new LinkedList<Card>();
    private Map<Integer, Boolean> processingClick = new HashMap<Integer, Boolean>(2);
    private boolean playTreasureCards = false;
    private boolean includePlatinumCards;
    private boolean includeColonyCards;
    private boolean copiedPlayedCard;
    private int goonsCardsPlayed;
    private int hoardCardsPlayed;
    private int talismanCardsPlayed;
    private int actionCardDiscount;
    private Set<Card> contrabandCards = new HashSet<Card>(0);
    private boolean trackContrabandCards;
    private boolean trackBankCards;
    private boolean refreshPeddler;
    private boolean royalSealCardPlayed;
    private Map<Integer, Boolean> tradeRouteTokenMap = new HashMap<Integer, Boolean>(0);
    private boolean trackTradeRouteTokens;
    private int tradeRouteTokensOnMat;
    private boolean checkWatchtower;
    private boolean checkTinker;
    private List<Card> blackMarketCardsToBuy = new ArrayList<Card>(0);
    private Map<Integer, ComputerPlayer> computerPlayers = new HashMap<Integer, ComputerPlayer>(0);
    private Map<Integer, List<Card>> costMap = new HashMap<Integer, List<Card>>();
    private Map<Integer, List<Card>> potionCostMap = new HashMap<Integer, List<Card>>();
    private Date creationTime = new Date();
    private RandomizingOptions randomizingOptions;
    private GameManager gameManager;
    private boolean custom;
    private Date lastActivity;
    private boolean determinedWinner = false;
    private boolean savedGameHistory = false;
    private String gameEndReason = "";
    private String winnerString = "";
    private List<Card> emptyPiles = new ArrayList<Card>(3);
    private boolean trackActionCardsPlayed;
    private List<Card> actionCardsPlayed = new ArrayList<Card>(3);
    private int historyEntriesAddedThisTurn = 0;
    private int twoCostKingdomCards = 0;
    private int logId;
    private int gameId;
    private boolean alwaysIncludeColonyAndPlatinum;
    private boolean neverIncludeColonyAndPlatinum;
    private String title = "";
    private boolean privateGame = false;
    private String password = "";
    private Set<Card> edictCards = new HashSet<Card>(0);
    private boolean trackEdictCards;
    private boolean annotatedGame;
    private int creatorId;
    private String creatorName = "";
    private boolean testGame;
    private List<Card> prizeCards = new ArrayList<Card>(0);
    private boolean showPrizeCards;
    private boolean gainTournamentBonus;
    private boolean princessCardPlayed;
    private int baneCardId;
    private Card horseTradersCard;
    private int maxHistoryTurnSize;
    private boolean abandonedGame;
    private boolean endingTurn;
    private boolean showVictoryPoints;
    private boolean identicalStartingHands;
    private List<Player> playersWaitingForBellTowerBonus = new ArrayList<Player>(0);
    private IncompleteCard incompleteCard;
    protected Queue<String> nextActionQueue = new LinkedList<String>();
    private List<Integer> enchantedPalaceRevealed = new ArrayList<Integer>();
    private Set<Integer> playersWithCardActions = new HashSet<Integer>(0);
    private boolean checkWalledVillage;
    private boolean playedWalledVillage;
    private boolean repeated;
    private boolean mobile;
    private int previousPlayerId = 0;
    private List<Card> previousPlayerCardsPlayed = new ArrayList<Card>();
    private List<Card> previousPlayerCardsBought = new ArrayList<Card>();
    private boolean usingLeaders;
    private List<Card> availableLeaders = new ArrayList<Card>(0);
    private boolean checkQuest;
    private boolean checkTrader;
    private int crossroadsPlayed;
    private Map<Integer, Card> cardsWithGainCardActions = new HashMap<Integer, Card>(0);
    private boolean trackHighway;
    private int highwayCardsInPlay;
    private boolean trackGoons;
    private boolean checkDuchess;
    private boolean checkScheme;
    private int schemeCardsPlayed;
    private boolean checkTunnel;
    private boolean checkHaggler;
    private int hagglerCardsInPlay;
    private boolean checkFoolsGold;
    private Card foolsGoldCard;
    private boolean checkNobleBrigand;
    private boolean trackLaborer;
    private int laborerCardsInPlay;
    private boolean recentGame;
    private boolean recommendedSet;
    private boolean randomizerReplacementCardNotFound;
    private boolean trackGoodwill;
    private int goodwillCardsInPlay;
    private int fruitTokensPlayed;
    private boolean checkPlantation;

    public Game(int gameId) {
        setPlayerColors();
        this.gameId = gameId;
    }

    private void setPlayerColors() {
        colors.add("red");
        colors.add("#001090"); //dark blue
        colors.add("green");
        colors.add("#0E80DF"); //light blue
        colors.add("purple");
        colors.add("#EF7C00"); //dark orange
    }

    public void init(){
        sortKingdomCards();
        creationTime = new Date();
        updateLastActivity();
        populateCardMaps();
        setupTokens();
        if (numComputerPlayers > 0) {
            addComputerPlayers();
        }
    }

    private void setupTokens() {
        if (showEmbargoTokens) {
            for (Integer cardId : cardMap.keySet()) {
                embargoTokens.put(cardId, 0);
            }
        }
        if (showTrollTokens) {
            for (Integer cardId : cardMap.keySet()) {
                trollTokens.put(cardId, 0);
            }
        }
        if (trackTradeRouteTokens || !blackMarketCards.isEmpty()) {
            for (Card card : supplyMap.values()) {
                if (card.isVictory()) {
                    tradeRouteTokenMap.put(card.getCardId(), true);
                }
                else {
                    tradeRouteTokenMap.put(card.getCardId(), false);
                }
            }
        }
    }

    private void setupSupply() {
        for (Card card : kingdomCards) {
            int numEachCard = 10;
            if (card.isVictory()) {
                if (numPlayers == 2) {
                    numEachCard = 8;
                }
                else {
                    numEachCard = 12;
                }
            }
            supply.put(card.getCardId(), numEachCard);
        }
        if (numPlayers > 4) {
            supply.put(Card.COPPER_ID, 120);
        }
        else {
            supply.put(Card.COPPER_ID, 60);
        }
        if (numPlayers > 4) {
            supply.put(Card.SILVER_ID, 80);
        }
        else {
            supply.put(Card.SILVER_ID, 40);
        }
        if (numPlayers > 4) {
            supply.put(Card.GOLD_ID, 60);
        }
        else {
            supply.put(Card.GOLD_ID, 30);
        }
        if (includePlatinumCards) {
            supply.put(Card.PLATINUM_ID, 12);
        }
        if (numPlayers == 2) {
            supply.put(Card.ESTATE_ID, 8);
            supply.put(Card.DUCHY_ID, 8);
            supply.put(Card.PROVINCE_ID, 8);
            if (includeColonyCards) {
                supply.put(Card.COLONY_ID, 8);
            }
            supply.put(Card.CURSE_ID, 10);
        }
        else if (numPlayers > 4) {
            supply.put(Card.ESTATE_ID, 12);
            supply.put(Card.DUCHY_ID, 12);
            if (includeColonyCards) {
                supply.put(Card.COLONY_ID, 12);
            }
            if (numPlayers == 5) {
                supply.put(Card.PROVINCE_ID, 15);
                supply.put(Card.CURSE_ID, 40);
            }
            else {
                supply.put(Card.PROVINCE_ID, 18);
                supply.put(Card.CURSE_ID, 50);
            }
        }
        else {
            supply.put(Card.ESTATE_ID, 12);
            supply.put(Card.DUCHY_ID, 12);
            supply.put(Card.PROVINCE_ID, 12);
            if (includeColonyCards) {
                supply.put(Card.COLONY_ID, 12);
            }
            if (numPlayers == 3) {
                supply.put(Card.CURSE_ID, 20);
            }
            else {
                supply.put(Card.CURSE_ID, 30);
            }
        }
        if (usePotions) {
            supply.put(Card.POTION_ID, 16);
        }
    }

    private void populateCardMaps() {
        for (Card card : kingdomCards) {
            checkCardName(card, false);
            supplyMap.put(card.getCardId(), card);
            cardMap.put(card.getCardId(), card);
            if (card.getCost() == 2) {
                twoCostKingdomCards++;
            }
            kingdomCardMap.put(card.getName(), card);
        }
        for (Card card : blackMarketCards) {
            cardMap.put(card.getCardId(), card);
            if (card.isCostIncludesPotion()) {
                usePotions = true;
            }
            if (card.getName().equals("Embargo")) {
                showEmbargoTokens = true;
            }
            else if (card.getName().equals("Bridge Troll")) {
                showTrollTokens = true;
            }
        }
        if (showPrizeCards || !blackMarketCards.isEmpty()) {
            for (Card card : prizeCards) {
                cardMap.put(card.getCardId(), card);
            }
        }
        if (usingLeaders) {
            for (Card card : availableLeaders) {
                cardMap.put(card.getCardId(), card);
            }
        }
        setupSupply();
        supplyCards.add(Card.getCopperCard());
        supplyCards.add(Card.getSilverCard());
        supplyCards.add(Card.getGoldCard());
        if(includePlatinumCards) {
            supplyCards.add(Card.getPlatinumCard());
        }
        if (usePotions) {
            supplyCards.add(Card.getPotionCard());
        }
        supplyCards.add(Card.getEstateCard());
        supplyCards.add(Card.getDuchyCard());
        supplyCards.add(Card.getProvinceCard());
        if (includeColonyCards) {
            supplyCards.add(Card.getColonyCard());
        }
        supplyCards.add(Card.getCurseCard());
        for (Card supplyCard : supplyCards) {
            cardMap.put(supplyCard.getCardId(), supplyCard);
            supplyMap.put(supplyCard.getCardId(), supplyCard);
        }
    }

    private void addComputerPlayers() {
        allComputerOpponents = (numComputerPlayers == numPlayers-1);
        int i = 1;
        for (int j = 1; j <= numEasyComputerPlayers; j++,i++) {
            addComputerPlayer(i, false, 1);
        }
        for (int j = 1; j <= numMediumComputerPlayers; j++,i++) {
            addComputerPlayer(i, false, 2);
        }
        for (int j = 1; j <= numHardComputerPlayers; j++,i++) {
            addComputerPlayer(i, false, 3);
        }
        for (int j = 1; j <= numBMUComputerPlayers; j++,i++) {
            addComputerPlayer(i, true, 3);
        }
        for (Card card : supplyMap.values()) {
            if (card.isCostIncludesPotion()) {
                List<Card> cards = potionCostMap.get(card.getCost());
                if (cards == null) {
                    cards = new ArrayList<Card>();
                }
                cards.add(card);
                potionCostMap.put(card.getCost(), cards);
            }
            else {
                List<Card> cards = costMap.get(card.getCost());
                if (cards == null) {
                    cards = new ArrayList<Card>();
                }
                cards.add(card);
                costMap.put(card.getCost(), cards);
            }
        }
    }

    private void sortKingdomCards() {
        CardCostComparator ccc = new CardCostComparator();
        if (baneCardId != 0) {
            Card baneCard = kingdomCards.get(10);
            List<Card> otherCards = kingdomCards.subList(0, 10);
            Collections.sort(otherCards, ccc);
            otherCards.add(5, baneCard);
            kingdomCards = otherCards;
        }
        else {
            Collections.sort(kingdomCards, ccc);
        }
    }

    public void addComputerPlayer(int i, boolean bigMoneyUltimate, int difficulty) {
        int userId = i*(-1);
        User user = new User();
        user.setGender(User.COMPUTER);
        if (bigMoneyUltimate) {
            user.setUserId(userId - 40);
            user.setUsername("C" + i + " (BMU)");
            addPlayer(user, true, true, 3);
        }
        else if (difficulty == 1) {
            user.setUserId(userId - 10);
            user.setUsername("C"+i+" (easy)");
            addPlayer(user, true, false, 1);
        }
        else if (difficulty == 2) {
            user.setUserId(userId - 20);
            user.setUsername("C"+i+" (medium)");
            addPlayer(user, true, false, 2);
        }
        else if (difficulty == 3) {
            user.setUserId(userId - 30);
            user.setUsername("C"+i+" (hard)");
            addPlayer(user, true, false, 3);
        }
    }

    public void addPlayer(User user) {
        addPlayer(user, false, false, 0);
    }

    public void addPlayer(User user, boolean computer, boolean bigMoneyUltimate, int difficulty) {
        Player player = new Player(user, this);
        player.setComputer(computer);
        player.setChatColor(getNextColor());
        getPlayers().add(player);
        getPlayerMap().put(player.getUserId(), player);
        getNeedsRefresh().put(player.getUserId(), new Refresh());
        if (computer) {
            if (bigMoneyUltimate) {
                computerPlayers.put(player.getUserId(), new BigMoneyComputerPlayer(player, this));
            }
            else if (difficulty == 1) {
                computerPlayers.put(player.getUserId(), new EasyComputerPlayer(player, this));
            }
            else if (difficulty == 2) {
                computerPlayers.put(player.getUserId(), new MediumComputerPlayer(player, this));
            }
            else {
                computerPlayers.put(player.getUserId(), new HardComputerPlayer(player, this));
            }
        }
        if (usingLeaders) {
            setPlayerCardAction(player, getSetupLeadersCardAction());
        }
        if (!repeated && getPlayers().size() == getNumPlayers()) {
            start();
        }
    }

    private CardAction getSetupLeadersCardAction() {
        CardAction cardAction = new CardAction(CardAction.TYPE_SETUP_LEADERS);
        cardAction.setNumCards(3);
        cardAction.setCards(availableLeaders);
        cardAction.setDeck(Card.DECK_LEADERS);
        cardAction.setButtonValue("Done");
        cardAction.setCardName("Setup Leaders");
        String instructions = "Choose 3 Leader cards and then click Done";
        cardAction.setInstructions(instructions);
        return cardAction;
    }

    public void removePlayer(User user) {
        Player player = playerMap.get(user.getUserId());
        players.remove(player);
        playerMap.remove(player.getUserId());
        getNeedsRefresh().remove(player.getUserId());
        if (player.getUserId() == creatorId) {
            if (players.isEmpty()) {
                creatorId = 0;
            }
            else {
                creatorId = players.get(0).getUserId();
            }
        }
        if (players.isEmpty()) {
            reset();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        updateLastActivity();
        this.status = status;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumComputerPlayers() {
        return numComputerPlayers;
    }

    public void setNumComputerPlayers(int numComputerPlayers) {
        this.numComputerPlayers = numComputerPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Integer, Player> getPlayerMap() {
        return playerMap;
    }

    public Map<String, Card> getKingdomCardMap() {
        return kingdomCardMap;
    }

    public List<Card> getKingdomCards() {
        return kingdomCards;
    }

    public void setKingdomCards(List<Card> kingdomCards) {
        this.kingdomCards = kingdomCards;
        if (kingdomCards.size() == 11) {
            baneCardId = kingdomCards.get(10).getCardId();
        }
        else {
            baneCardId = 0;
        }
    }

    public List<Card> getSupplyCards() {
        return supplyCards;
    }

    public Map<Integer, Integer> getSupply() {
        return supply;
    }

    public Map<Integer, Integer> getEmbargoTokens() {
        return embargoTokens;
    }

    public Map<Integer, Integer> getTrollTokens() {
        return trollTokens;
    }

    public int numTrollTokens(int cardId) {
        return trollTokens.get(cardId);
    }

    public boolean canBuyCard(Player player, Card card) {
        int cost = getCardCost(card, player, true);
        Integer numInSupply = getSupply().get(card.getCardId());
        return player.getCoins() >= cost && (!card.isCostIncludesPotion() || player.getPotions() > 0) && (!isTrackContrabandCards() || !getContrabandCards().contains(card)) && numInSupply != null && numInSupply > 0;
    }

    public boolean canBuyCardNotInSupply(Player player, Card card) {
        int cost = getCardCost(card, player, true);
        return player.getCoins() >= cost && (!card.isCostIncludesPotion() || player.getPotions() > 0) && (!isTrackContrabandCards() || !getContrabandCards().contains(card));
    }

    public int getCardCost(Card card) {
        return getCardCost(card, getCurrentPlayer(), isBuyPhase());
    }

    public int getCardCostBuyPhase(Card card) {
        return getCardCost(card, getCurrentPlayer(), true);
    }

    private int getCardCost(Card card, Player player, boolean buyPhase) {
        if (card == null) {
            GameError error = new GameError(GameError.GAME_ERROR, "getCardCost - card was null");
            logError(error, false);
            return 0;
        }
        int cost = card.getCost() - getCostDiscount();
        if (buyPhase) {
            cost -= player.getCardDiscount(card);
        }
        if (card.isAction()) {
            cost -= getActionCardDiscount();
        }
        if (buyPhase && card.getName().equals("Peddler")) {
            cost -= (2 * actionCardsInPlay);
        }
        if (buyPhase && isShowTrollTokens()) {
            cost += getTrollTokens().get(card.getCardId());
        }
        if (checkPlantation && card.getName().equals("Plantation") && fruitTokensPlayed > 0) {
            cost -= fruitTokensPlayed;
        }
        if (cost < 0) {
            cost = 0;
        }
        return cost;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public List<Card> getGroupedCardsPlayed() {
        KingdomUtil.groupCards(cardsPlayed);
        return cardsPlayed;
    }

    public List<Card> getCardsPlayed() {
        return cardsPlayed;
    }

    public void removePlayedCard(Card card) {
        getCardsPlayed().remove(card);
        if (trackActionCardsPlayed && card.isAction()) {
            actionCardsPlayed.remove(card);
        }
        if (trackGoons && card.getName().equals("Goons")) {
            goonsCardsPlayed--;
        }
        else if (checkHaggler && card.getName().equals("Haggler")) {
            hagglerCardsInPlay--;
        }
        else if (trackHighway && card.getName().equals("Highway")) {
            highwayCardsInPlay--;
        }
        else if (trackLaborer && card.getName().equals("Laborer")) {
            laborerCardsInPlay--;
        }
        else if (trackGoodwill && card.getName().equals("Goodwill")) {
            goodwillCardsInPlay--;
        }
        actionCardsInPlay--;
    }

    public List<Card> getGroupedCardsBought() {
        KingdomUtil.groupCards(cardsBought);
        return cardsBought;
    }

    public List<Card> getCardsBought() {
        return cardsBought;
    }

    public Map<Integer, Refresh> getNeedsRefresh() {
        return needsRefresh;
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean repeatingGame){
        if (!repeatingGame) {
            status = Game.STATUS_NO_GAMES;
            for (Player player : players) {
                LoggedInUsers.getInstance().gameReset(player.getUserId());
            }
            LoggedInUsers.getInstance().refreshLobbyPlayers();
            numPlayers = 0;
            numComputerPlayers = 0;
            numEasyComputerPlayers = 0;
            numMediumComputerPlayers = 0;
            numHardComputerPlayers = 0;
            numBMUComputerPlayers = 0;
            allComputerOpponents = false;
            usePotions = false;
            playTreasureCards = false;
            includePlatinumCards = false;
            includeColonyCards = false;
            supplyCards.clear();
            kingdomCards.clear();
            kingdomCardMap.clear();
            cardMap.clear();
            supplyMap.clear();
            blackMarketCards.clear();
            showDuke = false;
            showGardens = false;
            showFarmlands = false;
            showVictoryCoins = false;
            showVineyard = false;
            showSilkRoads = false;
            showCathedral = false;
            showFairgrounds = false;
            showGreatHall = false;
            showHarem = false;
            showNobles = false;
            showArchbishops = false;
            showDuration = false;
            showEmbargoTokens = false;
            showTrollTokens = false;
            showIslandCards = false;
            showMuseumCards = false;
            showCityPlannerCards = false;
            showNativeVillage = false;
            showPirateShipCoins = false;
            showFruitTokens = false;
            showCattleTokens = false;
            showSins = false;
            trackSmugglersCards = false;
            trackTreasuryCards = false;
            trackAlchemistCards = false;
            trackHerbalistCards = false;
            checkSecretChamber = false;
            checkHorseTraders = false;
            checkBellTower = false;
            checkEnchantedPalace = false;
            trackContrabandCards = false;
            trackBankCards = false;
            refreshPeddler = false;
            trackTradeRouteTokens = false;
            trackActionCardsPlayed = false;
            alwaysIncludeColonyAndPlatinum = false;
            neverIncludeColonyAndPlatinum = false;
            trackEdictCards = false;
            annotatedGame = false;
            recommendedSet = false;
            testGame = false;
            showPrizeCards = false;
            horseTradersCard = null;
            checkWalledVillage = false;
            showHedgeWizard = false;
            showGoldenTouch = false;
            identicalStartingHands = false;
            baneCardId = 0;
            creatorId = 0;
            creatorName = "";
            title = "";
            privateGame = false;
            password = "";
            twoCostKingdomCards = 0;
            custom = false;
            costMap.clear();
            potionCostMap.clear();
            checkWatchtower = false;
            checkTinker = false;
            mobile = false;
            usingLeaders = false;
            availableLeaders.clear();
            checkQuest = false;
            checkTrader = false;
            trackHighway = false;
            trackGoons = false;
            trackLaborer = false;
            checkDuchess = false;
            checkScheme = false;
            schemeCardsPlayed = 0;
            checkTunnel = false;
            checkHaggler = false;
            checkFoolsGold = false;
            foolsGoldCard = null;
            checkNobleBrigand = false;
            trackGoodwill = false;
            checkPlantation = false;
        }
        players.clear();
        playerMap.clear();
        computerPlayers.clear();
        supply.clear();
        embargoTokens.clear();
        trollTokens.clear();
        trashedCards.clear();
        cardsPlayed.clear();
        cardsBought.clear();
        previousPlayerId = 0;
        previousPlayerCardsPlayed.clear();
        previousPlayerCardsBought.clear();
        emptyPiles.clear();
        finishGameOnNextEndTurn = false;
        needsRefresh.clear();
        repeatedActions.clear();
        golemActions.clear();
        trashedTreasureCards.clear();
        setAsideCards.clear();
        recentTurnHistory.clear();
        turnHistory.clear();
        currentTurn = null;
        chats.clear();
        masqueradeCards.clear();
        currentPlayerIndex = 0;
        currentPlayerId = -1;
        currentColorIndex = 0;
        playersExited.clear();
        costDiscount = 0;
        numActionsCardsPlayed = 0;
        actionCardsInPlay = 0;
        durationCardsPlayed.clear();
        lighthousePlayed = false;
        outpostTurn = false;
        outpostCardPlayed = false;
        smugglersCards.clear();
        boughtVictoryCard = false;
        playedTreasuryCard = false;
        playedAlchemistCard = false;
        playedHerbalistCard = false;
        attackCard = null;
        potionsPlayed = 0;
        treasureCardsPlayed.clear();
        processingClick.clear();
        copiedPlayedCard = false;
        goonsCardsPlayed = 0;
        hagglerCardsInPlay = 0;
        hoardCardsPlayed = 0;
        talismanCardsPlayed = 0;
        actionCardDiscount = 0;
        contrabandCards.clear();
        royalSealCardPlayed = false;
        tradeRouteTokenMap.clear();
        tradeRouteTokensOnMat = 0;
        determinedWinner = false;
        savedGameHistory = false;
        gameEndReason = "";
        winnerString = "";
        actionCardsPlayed.clear();
        historyEntriesAddedThisTurn = 0;
        logId = 0;
        edictCards.clear();
        prizeCards.clear();
        gainTournamentBonus = false;
        princessCardPlayed = false;
        abandonedGame = false;
        playersWaitingForBellTowerBonus.clear();
        incompleteCard = null;
        enchantedPalaceRevealed.clear();
        playersWithCardActions.clear();
        playedWalledVillage = false;
        repeated = false;
        cardsWithGainCardActions.clear();
        highwayCardsInPlay = 0;
        laborerCardsInPlay = 0;
        recentGame = false;
        randomizerReplacementCardNotFound = false;
        goodwillCardsInPlay = 0;
        fruitTokensPlayed = 0;
        LoggedInUsers.getInstance().refreshLobbyGameRooms();
    }

    private void start(){
        Collections.shuffle(players);
        currentPlayerId = players.get(currentPlayerIndex).getUserId();
        status = Game.STATUS_GAME_IN_PROGRESS;
        refreshAllPlayersPlayers();
        refreshAllPlayersGameStatus();
        refreshAllPlayersSupply();
        refreshAllPlayersPlayingArea();
        refreshAllPlayersHandArea();
        refreshAllPlayersTitle();

        if(mobile) {
            maxHistoryTurnSize = players.size();
        }
        else {
            maxHistoryTurnSize = players.size() * 2;
        }

        startPlayerTurn(getCurrentPlayer());

        if (players.get(currentPlayerIndex).isComputer()) {
            new Thread(
                    new Runnable() {
                        public void run() {
                            if(previousPlayerId != 0) {
                                try {
                                    Thread.sleep(2200);
                                }
                                catch (Exception e) {
                                    GameError error = new GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e));
                                    logError(error);
                                }
                            }
                            computerPlayers.get(currentPlayerId).doNextAction();
                        }
                    }
            ).start();
        }
    }

    private void checkCardName(Card card, boolean boughtFromBlackMarket){
        if (card.getName().equals("Gardens")) {
            showGardens = true;
        }
        else if (card.getName().equals("Farmlands")) {
            showFarmlands = true;
        }
        else if (card.getName().equals("Vineyard")) {
            showVineyard = true;
        }
        else if (card.getName().equals("Silk Road")) {
            showSilkRoads = true;
        }
        else if (card.getName().equals("Great Hall")) {
            showGreatHall = true;
        }
        else if (card.getName().equals("Harem")) {
            showHarem = true;
        }
        else if (card.getName().equals("Duke")) {
            showDuke = true;
        }
        else if (card.getName().equals("Nobles")) {
            showNobles = true;
        }
        else if (card.getName().equals("Archbishop")) {
            showArchbishops = true;
        }
        else if (card.getName().equals("Embargo")) {
            showEmbargoTokens = true;
        }
        else if (card.getName().equals("Island")) {
            showIslandCards = true;
        }
        else if (card.getName().equals("Native Village")) {
            showNativeVillage = true;
        }
        else if (card.getName().equals("Pirate Ship")) {
            showPirateShipCoins = true;
        }
        else if (card.getName().equals("Smugglers")) {
            trackSmugglersCards = true;
        }
        else if (card.getName().equals("Treasury")) {
            trackTreasuryCards = true;
        }
        else if (card.getName().equals("Secret Chamber")) {
            checkSecretChamber = true;
        }
        else if (card.getName().equals("Throne Room")) {
            throneRoomCard = card;
        }
        else if (card.getName().equals("King's Court")) {
            kingsCourtCard = card;
        }
        else if (card.getName().equals("Alchemist")) {
            trackAlchemistCards = true;
        }
        else if (card.getName().equals("Herbalist")) {
            trackHerbalistCards = true;
        }
        else if (card.getName().equals("Contraband")) {
            trackContrabandCards = true;
        }
        else if (card.getName().equals("Bank")) {
            trackBankCards = true;
        }
        else if (card.getName().equals("Peddler")) {
            refreshPeddler = true;
        }
        else if (card.getName().equals("Trade Route")) {
            trackTradeRouteTokens = true;
        }
        else if (card.getName().equals("Watchtower")) {
            checkWatchtower = true;
        }
        else if (card.getName().equals("Edict")) {
            trackEdictCards = true;
        }
        else if (card.getName().equals("Fairgrounds")) {
            showFairgrounds = true;
        }
        else if (card.getName().equals("Tournament")) {
            showPrizeCards = true;
        }
        else if (card.getName().equals("Museum")) {
            showPrizeCards = true;
            showMuseumCards = true;
        }
        else if (card.getName().equals("City Planner")) {
            showCityPlannerCards = true;
        }
        else if (card.getName().equals("Horse Traders")) {
            checkHorseTraders = true;
            horseTradersCard = card;
        }
        else if (card.getName().equals("Bell Tower")) {
            checkBellTower = true;
        }
        else if (card.getName().equals("Cathedral")) {
            showCathedral = true;
        }
        else if (card.getName().equals("Enchanted Palace")) {
            checkEnchantedPalace = true;
        }
        else if (card.getName().equals("Hedge Wizard")) {
            showHedgeWizard = true;
        }
        else if (card.getName().equals("Golden Touch")) {
            showGoldenTouch = true;
        }
        else if (card.getName().equals("Tinker")) {
            checkTinker = true;
        }
        else if (card.getName().equals("Bridge Troll")) {
            showTrollTokens = true;
        }
        else if (card.getName().equals("Walled Village")) {
            checkWalledVillage = true;
            trackActionCardsPlayed = true;
        }
        else if (card.getName().equals("Quest")) {
            checkQuest = true;
        }
        else if (card.getName().equals("Trader")) {
            checkTrader = true;
        }
        else if (card.getName().equals("Highway")) {
            trackHighway = true;
        }
        else if (card.getName().equals("Goons")) {
            trackGoons = true;
        }
        else if (card.getName().equals("Duchess") && !boughtFromBlackMarket) {
            checkDuchess = true;
        }
        else if (card.getName().equals("Scheme")) {
            checkScheme = true;
            trackActionCardsPlayed = true;
        }
        else if (card.getName().equals("Tunnel")) {
            checkTunnel = true;
        }
        else if (card.getName().equals("Haggler")) {
            checkHaggler = true;
        }
        else if (card.getName().equals("Fool's Gold")) {
            checkFoolsGold = true;
            foolsGoldCard = card;
        }
        else if (card.getName().equals("Noble Brigand")) {
            checkNobleBrigand = true;
        }
        else if (card.getName().equals("Laborer")) {
            trackLaborer = true;
        }
        else if (card.getName().equals("Goodwill")) {
            trackGoodwill = true;
        }
        else if (card.getName().equals("Plantation")) {
            checkPlantation = true;
        }
        if (card.isDuration()) {
            showDuration = true;
        }
        if (card.isCostIncludesPotion()) {
            usePotions = true;
        }
        if (card.getAddVictoryCoins() > 0 || card.getName().equals("Goons")) {
            showVictoryCoins = true;
        }
        if (card.isSalvation()) {
            showSins = true;
        }
        if (card.getFruitTokens() > 0 || card.getName().equals("Orchard") || card.getName().equals("Goodwill")) {
            showFruitTokens = true;
        }
        if (card.getCattleTokens() > 0 || card.getName().equals("Rancher")) {
            showCattleTokens = true;
        }
    }

    public void cardClicked(Player player, String clickType, int cardId) {
        Card card = cardMap.get(cardId);
        cardClicked(player, clickType, card);
    }

    public void cardClicked(Player player, String clickType, Card card) {
        cardClicked(player, clickType, card, true);
    }

    public void cardClicked(final Player player, String clickType, Card card, boolean confirm) {
        if (allowClick(player)) {
            int coinsBefore = player.getCoins();
            int potionsBefore = player.getPotions();
            updateLastActivity();
            try {
                boolean actionPlayed = false;
                boolean cardBought = false;
                boolean treasurePlayed = false;
                boolean leaderActivated = false;
                if (clickType.equals("hand")) {
                    if (card.isAction()) {
                        if (player.hasBoughtCard()) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play an action after you have bought a card."));
                        }
                        else if (playTreasureCards && treasureCardsPlayed.size() > 0) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play an action after you have played a treasure card."));
                        }
                        else if (player.getActions() > 0) {
                            actionPlayed = true;
                            playActionCard(player, card);
                        }
                        else {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any actions left."));
                        }
                    }
                    else if (playTreasureCards && card.isTreasure()) {
                        if (player.hasBoughtCard()) {
                            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play a treasure card after you have bought a card."));
                        }
                        else {
                            treasurePlayed = true;
                            playTreasureCard(player, card, true, true, confirm, true, false);
                        }
                    }
                }
                else if (clickType.equals("supply")) {
                    cardBought = buyCard(player, card, confirm);
                }
                else if (clickType.equals("leader")) {
                    leaderActivated = activateLeader(player, card.getCardId());
                }

                if ((cardBought || leaderActivated) && !player.isComputer() && player.getBuys() == 0 && !player.isShowCardAction() && player.getExtraCardActions().isEmpty() && !hasUnfinishedGainCardActions()) {
                    endPlayerTurn(player, false);
                }
                else {
                    if (actionPlayed || cardBought || treasurePlayed || leaderActivated) {
                        refreshAllPlayersPlayingArea();
                        refreshHandArea(player);
                    }
                    if (coinsBefore != player.getCoins() || potionsBefore != player.getPotions()) {
                        refreshSupply(player);
                    }
                }
            }
            finally {
                processingClick.remove(player.getUserId());
            }
        }
    }

    private boolean activateLeader(Player player, int cardId) {
        Card card = player.getLeaderCard(cardId);
        if (card == null) {
            return false;
        }
        int cost = getCardCost(card, player, true);
        if (player.getCoins() >= cost && player.getBuys() > 0 && !card.isActivated() && player.getTurns() > 1) {
            if (card.isVictory()) {
                boughtVictoryCard = true;
            }
            if (!playTreasureCards && !player.hasBoughtCard()) {
                playAllTreasureCards(player, false);
            }
            player.setHasBoughtCard(true);
            addHistory(player.getUsername(), " activated the leader ", KingdomUtil.getCardWithBackgroundColor(card));
            player.addCoins(cost * (-1));
            player.addBuys(-1);
            card.setActivated(true);
            player.leaderActivated(card);
            SpecialActionHandler.handleSpecialAction(this, card);
            refreshAllPlayersPlayers();
            return true;
        }
        else {
            if (player.getBuys() == 0) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any more buys."));
            }
            else if (player.getTurns() < 2) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't activate a leader until your third turn."));
            }
            else if (player.getCoins() < cost) {
                if (player.isComputer()) {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins. Card: " + card.getName() + " Coins: " + player.getCoins()));
                }
                else {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins to activate the leader."));
                }
            }
            else if (card.isActivated()) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("Leader has already been activated."));
            }
        }
        return false;
    }

    private boolean buyCard(Player player, Card card, boolean confirm){
        boolean cardBought = false;
        if (trackContrabandCards && contrabandCards.contains(card)) {
            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("This card was banned by Contraband this turn."));
            return false;
        }
        else if (!getRepeatedActions().isEmpty()) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, player.getUsername() + " could not buy a card because a repeated action was not completed for: " + getRepeatedActions().getFirst().getCard().getName());
            logError(error, false);
            //todo return false instead of clearing
            getRepeatedActions().clear();
        }
        else if (hasIncompleteCard()) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, player.getUsername() + " could not buy a card because there was an incomplete action for: " + getIncompleteCard().getCardName());
            logError(error, false);
            //todo return false instead of removing
            removeIncompleteCard();
        }
        int cost = getCardCostBuyPhase(card);
        if(supply == null) {
            System.out.println("supply null");
        }
        else if (card == null) {
            System.out.println("card null");
        }
        else if (supply.get(card.getCardId()) == null) {
            System.out.println("supply card null");
        }
        int numInSupply = getNumInSupply(card);
        boolean missingPotion = false;
        if (card.isCostIncludesPotion() && player.getPotions() == 0) {
            missingPotion = true;
        }
        if (player.getCoins() >= cost && player.getBuys() > 0 && numInSupply > 0 && !missingPotion) {
            if (confirm && !player.isComputer() && !player.hasBoughtCard() && (!playTreasureCards || treasureCardsPlayed.isEmpty())) {
                if (playTreasureCards && !player.getTreasureCards().isEmpty()) {
                    CardAction confirmBuyCardAction = new CardAction(CardAction.TYPE_YES_NO);
                    confirmBuyCardAction.setCardName("Confirm Buy");
                    confirmBuyCardAction.getCards().add(card);
                    confirmBuyCardAction.setInstructions("You haven't played any treasure cards, are you sure you want to buy this card?");
                    setPlayerCardAction(player, confirmBuyCardAction);
                    return false;
                }
                else if (player.getActions() > 0 && player.getActionCards().size() > 0) {
                    boolean confirmBuy = true;
                    if (player.getActionCards().size() == 1) {
                        Card actionCard = player.getActionCards().get(0);
                        if (actionCard.getName().equals("Throne Room") || actionCard.getName().equals("King's Court") || actionCard.getName().equals("Monk") || actionCard.getName().equals("Chapel")) {
                            confirmBuy = false;
                        }
                    }
                    if (confirmBuy) {
                        CardAction confirmBuyCardAction = new CardAction(CardAction.TYPE_YES_NO);
                        confirmBuyCardAction.setCardName("Confirm Buy");
                        confirmBuyCardAction.getCards().add(card);
                        confirmBuyCardAction.setInstructions("You still have actions remaining, are you sure you want to buy this card?");
                        setPlayerCardAction(player, confirmBuyCardAction);
                        return false;
                    }
                }
            }
            if (card.getName().equals("Grand Market")) {
                for (Card treasureCard : treasureCardsPlayed) {
                    if (treasureCard.getCardId() == Card.COPPER_ID) {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't buy this card when you have a Copper in play."));
                        return false;
                    }
                }
            }
            if (!playTreasureCards && !player.hasBoughtCard()) {
                playAllTreasureCards(player, false);
            }
            cardBought = true;
            if (card.isVictory()) {
                boughtVictoryCard = true;
            }
            player.setHasBoughtCard(true);
            addHistory(player.getUsername(), " bought ", KingdomUtil.getArticleWithCardName(card));
            player.addCoins(cost * (-1));
            player.addBuys(-1);
            if (card.isCostIncludesPotion()) {
                if (!playTreasureCards) {
                    potionsPlayed++;
                }
                player.addPotions(-1);
            }
            if (goonsCardsPlayed > 0) {
                player.addVictoryCoins(goonsCardsPlayed);
                refreshAllPlayersPlayers();
                addHistory(player.getUsername(), " gained ", KingdomUtil.getPlural(goonsCardsPlayed, "Victory Coin"), " from ", KingdomUtil.getWordWithBackgroundColor("Goons", Card.ACTION_COLOR));
            }
            cardsBought.add(card);
            if (card.getName().equals("Mint")) {
                for (Card treasureCard : treasureCardsPlayed) {
                    cardsPlayed.remove(treasureCard);
                    trashedCards.add(treasureCard);
                }
                treasureCardsPlayed.clear();
                hoardCardsPlayed = 0;
                talismanCardsPlayed = 0;
                addHistory("The Mint trashed all the treasure cards played by ", player.getUsername());
            }
            playerGainedCard(player, card, "discard", true, true);
            if (card.isVictory() && hoardCardsPlayed > 0) {
                int goldsToGain = hoardCardsPlayed;
                int goldsInSupply = getNumInSupply(Card.GOLD_ID);
                if (goldsInSupply < goldsToGain) {
                    goldsToGain = goldsInSupply;
                }
                if (goldsToGain > 0) {
                    for (int i = 0; i < goldsToGain; i++) {
                        playerGainedCard(player, getGoldCard());
                    }
                }
            }
            if (!card.isVictory() && talismanCardsPlayed > 0 && cost <= 4) {
                int cardsGained = 0;
                while (isCardInSupply(card) && cardsGained < talismanCardsPlayed) {
                    cardsGained++;
                    playerGainedCard(player, card);
                }
            }
            if (trackGoodwill && goodwillCardsInPlay > 0) {
                player.addFruitTokens(goodwillCardsInPlay);
                addHistory(player.getUsername(), " gained ", KingdomUtil.getPlural(goodwillCardsInPlay, "fruit token"));
            }
            if (showEmbargoTokens) {
                int numEmbargoTokens = embargoTokens.get(card.getCardId());
                if (numEmbargoTokens > 0) {
                    int curseCardsGained = 0;
                    for (int i = 0; i < numEmbargoTokens; i++) {
                        int cursesInSupply = getNumInSupply(Card.CURSE_ID);
                        if (cursesInSupply > 0) {
                            playerGainedCard(player, getCurseCard());
                            curseCardsGained++;
                        }
                    }
                    if (curseCardsGained > 0) {
                        refreshDiscard(player);
                    }
                }
            }
        }
        else {
            if (player.getBuys() == 0) {
                setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have any more buys."));
            }
            else if(player.getCoins() < cost) {
                if (playTreasureCards && player.getCoinsInHand() > 0) {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You need to play your treasure cards."));
                }
                else {
                    if (player.isComputer()) {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins. Card: "+card.getName()+" Coins: "+player.getCoins()));
                    }
                    else {
                        setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You don't have enough coins."));
                    }
                }
            }
            else if (missingPotion) {
                if (player.isComputer()) {
                    GameError error = new GameError(GameError.COMPUTER_ERROR, player.getUsername()+" needs a potion to buy "+card.getName());
                    logError(error, false);
                }
                else {
                    setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You need a potion to buy this card."));
                }
            }
        }
        return cardBought;
    }

    public void boughtBlackMarketCard(Card card){
        checkCardName(card, true);
        int cost = getCardCost(card);
        getCurrentPlayer().addCoins(cost * (-1));
        refreshAllPlayersHandArea();
    }

    private void addCardBonuses(Player player, Card card) {
        if (card.getAddActions() != 0) {
            player.addActions(card.getAddActions());
        }
        if (card.getAddBuys() != 0) {
            player.addBuys(card.getAddBuys());
        }
        if (card.getAddCoins() != 0) {
            player.addCoins(card.getAddCoins());
        }
        if (card.getAddCards() != 0) {
            player.drawCards(card.getAddCards());
        }
        if (card.getAddVictoryCoins() != 0) {
            player.addVictoryCoins(card.getAddVictoryCoins());
            refreshAllPlayersPlayers();
        }
        if (card.getSins() != 0) {
            player.addSins(card.getSins());
            refreshAllPlayersPlayers();
        }
        if (card.isPotion()) {
            player.addPotions(1);
        }
        if (card.getFruitTokens() != 0) {
            player.addFruitTokens(card.getFruitTokens());
        }
        if (card.getCattleTokens() != 0) {
            player.addCattleTokens(card.getCattleTokens());
        }
    }

    private void actionCardPlayed(Player player, Card card) {
        actionCardPlayed(player, card, false);
    }

    private void actionCardPlayed(Player player, Card card, boolean repeatedAction){
        addCardBonuses(player, card);
        if (card.getName().equals("Coppersmith")) {
            player.copperSmithPlayed();
        }
        else if (trackGoons && card.getName().equals("Goons") && !repeatedAction) {
            goonsCardsPlayed++;
        }
        else if (checkHaggler && card.getName().equals("Haggler") && !repeatedAction) {
            hagglerCardsInPlay++;
        }
        else if (checkScheme && card.getName().equals("Scheme")) {
            schemeCardsPlayed++;
        }
        else if (card.getName().equals("Crossroads")) {
            crossroadsPlayed++;
        }
        else if (trackHighway && card.getName().equals("Highway") && !repeatedAction) {
            highwayCardsInPlay++;
            refreshAllPlayersSupply();
        }
        else if (trackLaborer && card.getName().equals("Laborer") && !repeatedAction) {
            laborerCardsInPlay++;
            if (laborerCardsInPlay >= 2) {
                player.drawCards(1);
                refreshHand(player);
                refreshAllPlayersCardsBought();
                addHistory(player.getUsername(), " gained +1 card");
            }
        }
        if (trackEdictCards && edictCards.contains(card)) {
            player.addSins(1);
            refreshAllPlayersPlayers();
            addHistory(player.getUsername(), " gained 1 sin from an ", KingdomUtil.getWordWithBackgroundColor("Edict", Card.ACTION_DURATION_COLOR));
        }
        if (trackTreasuryCards && card.getName().equals("Treasury")) {
            playedTreasuryCard = true;    
        }
        if (trackAlchemistCards && card.getName().equals("Alchemist")) {
            playedAlchemistCard = true;
        }
        if (trackHerbalistCards && card.getName().equals("Herbalist")) {
            playedHerbalistCard = true;
        }
        if (checkWalledVillage && card.getName().equals("Walled Village")) {
            playedWalledVillage = true;
        }
        if (card.isDuration()) {
            if (card.getName().equals("Lighthouse")) {
                lighthousePlayed = true;
            }
            else if (card.getName().equals("Outpost")) {
                outpostCardPlayed = true;
            }
        }
        if ((checkSecretChamber || checkHorseTraders || checkBellTower || checkEnchantedPalace) && card.isAttack()) {
            attackCard = card;
            if (checkHorseTraders) {
                addNextAction("check horse traders");
            }
            if (checkSecretChamber) {
                addNextAction("check secret chamber");
            }
            if (checkBellTower) {
                addNextAction("check bell tower");
            }
            if (checkEnchantedPalace) {
                enchantedPalaceRevealed.clear();
                addNextAction("check enchanted palace");
            }
            addNextAction("finish attack");
            NextActionHandler.handleAction(this, "reaction");

            if (hasIncompleteCard()) {
                incompleteCard.actionFinished(player);
            }
        }
        else if (card.isSpecialCard()) {
            SpecialActionHandler.handleSpecialAction(this, card, repeatedAction);
        }
    }

    private void playActionCard(Player player, Card card){
        Card cardCopy;
        if (checkQuest && !card.isCopied() && card.getName().equals("Quest")) {
            cardCopy = new Card(card);
            copiedPlayedCard = true;
        }
        else {
            cardCopy = card;
        }
        numActionsCardsPlayed++;
        if (trackActionCardsPlayed) {
            actionCardsPlayed.add(card);
        }
        actionCardsInPlay++;
        addHistory(player.getUsername(), " played ", KingdomUtil.getArticleWithCardName(card));
        cardsPlayed.add(cardCopy);
        player.removeCardFromHand(card);
        player.addActions(-1);
        if (card.isDuration()) {
            durationCardsPlayed.add(cardCopy);
        }
        actionCardPlayed(player, cardCopy);
        refreshAllPlayersPlayingArea();
        if (refreshPeddler) {
            refreshAllPlayersSupply();
        }
    }

    public void playTreasureCard(Player player, Card card, boolean removeFromHand, boolean playSpecialAction) {
        playTreasureCard(player, card, removeFromHand, playSpecialAction, true, true, false);
    }

    public void playTreasureCard(Player player, Card card, boolean removeFromHand, boolean playSpecialAction, boolean confirm, boolean showHistory, boolean blackMarketTreasure) {
        if (confirm && !player.isComputer() && player.getActions() > 0 && player.getActionCards().size() > 0 && !isBuyPhase()) {
            if (player.getActionCards().size() == 1) {
                Card actionCard = player.getActionCards().get(0);
                if (actionCard.getName().equals("Throne Room") || actionCard.getName().equals("King's Court") || actionCard.getName().equals("Monk") || actionCard.getName().equals("Chapel")) {
                    confirm = false;
                }
            }
            if (confirm) {
                CardAction confirmPlayTreasureCard = new CardAction(CardAction.TYPE_YES_NO);
                confirmPlayTreasureCard.getCards().add(card);
                confirmPlayTreasureCard.setCardName("Confirm Play Treasure Card");
                confirmPlayTreasureCard.setInstructions("You still have actions remaining, are you sure you want to play this Treasure card?");
                setPlayerCardAction(player, confirmPlayTreasureCard);
                return;
            }
        }
        Card cardCopy;
        if (card.getName().equals("Philosopher's Stone")) {
            cardCopy = new Card(card);
            copiedPlayedCard = true;
            cardCopy.setAddCoins(player.getPhilosophersStoneCoins());
        }
        else if(card.getName().equals("Bank")) {
            cardCopy = new Card(card);
            copiedPlayedCard = true;
            cardCopy.setAddCoins(treasureCardsPlayed.size()+1);
        }
        else if(card.getName().equals("Storybook")) {
            cardCopy = new Card(card);
            copiedPlayedCard = true;
        }
        else {
            cardCopy = card;
        }
        if (card.getName().equals("Hoard")) {
            hoardCardsPlayed++;
        }
        else if (card.getName().equals("Talisman")) {
            talismanCardsPlayed++;
        }
        else if (card.getName().equals("Royal Seal")) {
            royalSealCardPlayed = true;
        }
        else if (card.getName().equals("Goodwill")) {
            goodwillCardsInPlay++;
        }
        if (blackMarketTreasure) {
            blackMarketTreasureCardsPlayed.add(cardCopy);
        }
        else {
            treasureCardsPlayed.add(cardCopy);
        }
        cardsPlayed.add(cardCopy);
        if (cardCopy.isPotion()) {
            potionsPlayed++;
        }
        addCardBonuses(player, cardCopy);
        player.treasureCardPlayed(cardCopy, removeFromHand);
        if(showHistory) {
            addHistory(player.getUsername(), " played ", KingdomUtil.getArticleWithCardName(cardCopy));
        }
        if (playSpecialAction && !cardCopy.getSpecial().equals("")) {
            TreasureCardsSpecialActionHandler.handleSpecialAction(this, cardCopy);
            if (hasIncompleteCard()) {
                getIncompleteCard().actionFinished(player);
            }
        }
        refreshAllPlayersPlayingArea();
    }


    public void playAllTreasureCards(Player player) {
        playAllTreasureCards(player, true);
    }

    public void playAllTreasureCards(Player player, boolean confirm) {
        updateLastActivity();
        if (player.hasBoughtCard()) {
            setPlayerInfoDialog(player, InfoDialog.getErrorDialog("You can't play a treasure card after you have bought a card."));
        }
        else {
            if (!confirm || allowClick(player)) {
                try {
                    if (confirm && !player.isComputer() && player.getActions() > 0 && player.getActionCards().size() > 0 && !isBuyPhase()) {
                        if (player.getActionCards().size() == 1) {
                            Card actionCard = player.getActionCards().get(0);
                            if (actionCard.getName().equals("Throne Room") || actionCard.getName().equals("King's Court") || actionCard.getName().equals("Monk") || actionCard.getName().equals("Chapel")) {
                                confirm = false;
                            }
                        }
                        if (confirm) {
                            CardAction confirmPlayTreasureCards = new CardAction(CardAction.TYPE_YES_NO);
                            confirmPlayTreasureCards.setCardName("Confirm Play Treasure Cards");
                            confirmPlayTreasureCards.setInstructions("You still have actions remaining, are you sure you want to play your Treasure cards?");
                            setPlayerCardAction(player, confirmPlayTreasureCards);
                            return;
                        }
                    }
                    if (player.getUserId() == currentPlayerId && !player.getTreasureCards().isEmpty()) {
                        List<Card> treasureCardsPlayed = new ArrayList<Card>();
                        List<Card> cards = new ArrayList<Card>(player.getTreasureCards());
                        for (Card card : cards) {
                            if (card.isAutoPlayTreasure()) {
                                treasureCardsPlayed.add(card);
                            }
                        }
                        if (!treasureCardsPlayed.isEmpty()) {
                            addHistory(player.getUsername(), " played ", KingdomUtil.groupCards(treasureCardsPlayed, true));
                            for (Card card : treasureCardsPlayed) {
                                playTreasureCard(player, card, true, true, false, false, false);
                            }
                        }
                        refreshHandArea(player);
                        refreshSupply(player);
                    }
                }
                finally {
                    processingClick.remove(player.getUserId());
                }
            }
        }
    }

    public void playRepeatedAction(Player player, boolean firstAction) {
        RepeatedAction repeatedAction = repeatedActions.pop();
        Card card = repeatedAction.getCard();
        numActionsCardsPlayed++;
        if (repeatedAction.isFirstAction()) {
            actionCardsInPlay++;
            cardsPlayed.add(card);
            player.removeCardFromHand(card);
            if (card.isDuration()) {
                durationCardsPlayed.add(card);
            }
        }
        actionCardPlayed(player, card, !firstAction);
        if(!card.hasSpecial() && !repeatedActions.isEmpty()){
            playRepeatedAction(player, false);
        }
        refreshAllPlayersPlayingArea();
        if (refreshPeddler) {
            refreshAllPlayersSupply();
        }
    }

    public void playGolemActionCard(Player player) {
        Card card = golemActions.pop();
        addHistory(player.getUsername(), "'s Golem played ", KingdomUtil.getArticleWithCardName(card));
        cardsPlayed.add(card);
        numActionsCardsPlayed++;
        if (trackActionCardsPlayed) {
            actionCardsPlayed.add(card);
        }
        actionCardsInPlay++;
        if (card.isDuration()) {
            durationCardsPlayed.add(card);
        }
        actionCardPlayed(player, card);
        if (!card.hasSpecial() && !golemActions.isEmpty()) {
            playGolemActionCard(player);
        }
        refreshAllPlayersPlayingArea();
    }

    public boolean takeFromSupply(int cardId){
        if (supply.get(cardId) != null) {
            int numInSupply = getNumInSupply(cardId) - 1;
            if (numInSupply < 0) {
                return false;
            }
            supply.put(cardId, numInSupply);
            if(numInSupply == 0) {
                Card card = supplyMap.get(cardId);
                emptyPiles.add(card);
                if (card.getCost() == 2 && kingdomCards.contains(card)) {
                    twoCostKingdomCards--;
                }
                if ((numPlayers < 5 && emptyPiles.size() == 3) || emptyPiles.size() == 4 || cardId == Card.PROVINCE_ID || cardId == Card.COLONY_ID) {
                    finishGameOnNextEndTurn = true;
                    if (cardId == Card.PROVINCE_ID) {
                        gameEndReason = "Province pile gone";
                    }
                    else if (cardId == Card.COLONY_ID) {
                        gameEndReason = "Colony pile gone";
                    }
                    else {
                        gameEndReason = emptyPiles.size()+" piles empty ("+ KingdomUtil.getCardNames(emptyPiles)+")";
                    }
                }
            }
            refreshAllPlayersSupply();
            return true;
        }
        return false;
    }

    public boolean buyingCardWillEndGame(int cardId) {
        if (supply.get(cardId) != null) {
            int numInSupply = getNumInSupply(cardId) - 1;
            if (numInSupply < 0) {
                return false;
            }
            if(numInSupply == 0) {
                int numEmptyPiles = emptyPiles.size()+1;
                if ((numPlayers < 5 && numEmptyPiles == 3) || numEmptyPiles == 4 || cardId == Card.PROVINCE_ID || cardId == Card.COLONY_ID) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addEmbargoToken(int cardId) {
        int numTokens = embargoTokens.get(cardId) + 1;
        embargoTokens.put(cardId, numTokens);
        refreshAllPlayersSupply();
    }

    public void addTrollToken(int cardId) {
        int numTokens = trollTokens.get(cardId) + 1;
        trollTokens.put(cardId, numTokens);
        refreshAllPlayersSupply();
    }

    public void addToSupply(int cardId) {
        if (supply.get(cardId) == null) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, "Supply does not have an entry for cardId: "+cardId);
            logError(error, false);
        }
        int numInSupply = getNumInSupply(cardId) + 1;
        supply.put(cardId, numInSupply);
        if (numInSupply == 1) {
            Card card = supplyMap.get(cardId);
            emptyPiles.remove(card);
            if (card.getCost() == 2 && kingdomCards.contains(card)) {
                twoCostKingdomCards++;
            }
        }
        refreshAllPlayersSupply();
    }

    private void addSmugglersCard(Card card) {
        if (card.getCost() <= 6) {
            smugglersCardsGained.add(card);
        }
    }
    
    private void checkTradeRouteToken(Card card) {
        Boolean hasTradeRouteToken = tradeRouteTokenMap.get(card.getCardId());
        if (hasTradeRouteToken != null && hasTradeRouteToken) {
            tradeRouteTokensOnMat++;
            addHistory("Trade Route Mat now has ", KingdomUtil.getPlural(tradeRouteTokensOnMat, "Token"));
            tradeRouteTokenMap.put(card.getCardId(), false);
        }
    }

    public boolean isCurrentPlayer(Player player) {
        return player.getUserId() == currentPlayerId;
    }

    public void playerLostCard(Player player, Card card) {
        if (player.getUserId() == currentPlayerId && card.getAddCoins() != 0) {
            refreshAllPlayersCardsBought();
        }
        if (victoryPointsNeedRefresh(card)) {
            refreshAllPlayersPlayers();
        }
    }

    public void playerGainedCard(Player player, Card card) {
        playerGainedCard(player, card, true);
    }

    public void playerGainedCard(Player player, Card card, boolean takeFromSupply) {
        playerGainedCard(player, card, "discard", takeFromSupply, false);
    }

    public void playerGainedCardToHand(Player player, Card card) {
        playerGainedCardToHand(player, card, true);
    }

    public void playerGainedCardToHand(Player player, Card card, boolean takeFromSupply) {
        playerGainedCard(player, card, "hand", takeFromSupply, false);
    }

    public void playerGainedCardToTopOfDeck(Player player, Card card) {
        playerGainedCardToTopOfDeck(player, card, true);
    }

    public void playerGainedCardToTopOfDeck(Player player, Card card, boolean takeFromSupply) {
        playerGainedCard(player, card, "deck", takeFromSupply, false);
    }

    public boolean victoryPointsNeedRefresh(Card card) {
        if (card == null) {
            GameError error = new GameError(GameError.GAME_ERROR, "victoryPointsNeedRefresh - card was null");
            logError(error, false);
            return false;
        }
        return card.isVictory() || card.isCurse() || showGardens || showFarmlands || showFairgrounds || (showVineyard && card.isAction()) || (showCathedral && card.isSalvation());
    }

    public void playerGainedCard(Player player, Card card, String destination, boolean takeFromSupply, boolean gainedFromBuy) {
        if (card.isCopied() && !card.isCardNotGained()) {
            if (!card.getGainCardActions().isEmpty()) {
                waitIfNotCurrentPlayer(player);
                setPlayerGainCardAction(player, card);
            }
            else {
                gainCardFinished(player, cardMap.get(card.getCardId()));
            }
        }
        else {
            Card cardCopy;
            if(card.isCopied()) {
                cardCopy = card;
            }
            else {
                cardCopy = new Card(card);
            }
            if (gainedFromBuy) {
                cardCopy.setGainedFromBuy(true);
                if (checkNobleBrigand && card.getName().equals("Noble Brigand")) {
                    BuySpecialActionHandler.setNobleBrigandCardAction(this, player);
                }
                List<CardAction> buyCardActions = new ArrayList<CardAction>(0);
                CardAction buyCardAction = BuySpecialActionHandler.getCardAction(this, player, cardCopy);
                if (buyCardAction != null) {
                    buyCardActions.add(buyCardAction);
                }
                if (checkHaggler) {
                    int numTimesToGainHagglerBonus = hagglerCardsInPlay;
                    while (numTimesToGainHagglerBonus > 0) {
                        CardAction hagglerCardAction = BuySpecialActionHandler.getHagglerCardAction(this, cardCopy);
                        if (hagglerCardAction != null) {
                            numTimesToGainHagglerBonus--;
                            buyCardActions.add(hagglerCardAction);
                        }
                        else {
                            break;
                        }
                    }
                }
                if (!buyCardActions.isEmpty()) {
                    cardCopy.setCardNotGained(true);
                    buyCardActions.get(buyCardActions.size() - 1).setGainCardAfterBuyAction(true);
                    for (CardAction cardAction : buyCardActions) {
                        setPlayerCardAction(player, cardAction);
                    }
                    return;
                }
            }
            if (checkTrader && !card.isTraderProcessed() && player.hasTrader() && (!card.isSilver() || !destination.equals("discard") || !takeFromSupply)) {
                cardCopy.setTraderProcessed(true);
                cardCopy.setCardNotGained(true);
                CardAction cardAction = GainCardsReactionHandler.getCardAction("Trader", this, player, cardCopy, destination);
                if (cardAction != null) {
                    waitIfNotCurrentPlayer(player);
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            if (trackSmugglersCards && player.getUserId() == currentPlayerId) {
                addSmugglersCard(card);
            }
            if (trackTradeRouteTokens) {
                checkTradeRouteToken(card);
            }
            if (takeFromSupply) {
                takeFromSupply(card.getCardId());
                refreshAllPlayersSupply();
            }
            if (card.getName().equals("Nomad Camp")) {
                destination = "deck";
            }
            cardCopy.setCardNotGained(false);
            addCardToDestination(player, card, destination);
            if (!gainedFromBuy && !card.isGainedFromBuy()) {
                addGainedCardToDestinationHistory(player, card, destination);
            }
            if (player.isComputer()) {
                ComputerPlayer computerPlayer = getComputerPlayers().get(player.getUserId());
                computerPlayer.gainedCard(card);
            }
            if (victoryPointsNeedRefresh(card)) {
                refreshAllPlayersPlayers();
            }
            cardCopy.setDestination(destination);
            setGainedCardActions(player, cardCopy, destination);
            if (!cardCopy.getGainCardActions().isEmpty()) {
                waitIfNotCurrentPlayer(player);
                setPlayerGainCardAction(player, cardCopy);
            }
            else {
                gainCardFinished(player, card);
            }
            if (checkFoolsGold && card.isProvince()) {
                CardAction foolsGoldCardAction = GainCardsSpecialActionHandler.getFoolsGoldCardAction();
                for (Player p : players) {
                    if (!isCurrentPlayer(p) && p.hasFoolsGoldInHand()) {
                        waitIfNotCurrentPlayer(p);
                        for (int i = 0; i < p.getFoolsGoldInHand(); i++) {
                            setPlayerCardAction(p, foolsGoldCardAction);
                        }
                    }
                }
            }
        }
    }

    private void addGainedCardToDestinationHistory(Player player, Card card, String destination) {
        if (destination.equals("hand")) {
            addHistory(player.getUsername(), " gained ", KingdomUtil.getArticleWithCardName(card), " into ", player.getPronoun(), " hand");
        }
        else if (destination.equals("deck")) {
            addHistory(player.getUsername(), " gained ", KingdomUtil.getArticleWithCardName(card), " on top of ", player.getPronoun(), " deck");
        }
        else if (destination.equals("discard")) {
            addHistory(player.getUsername(), " gained ", KingdomUtil.getArticleWithCardName(card));
        }
    }

    private void addCardToDestination(Player player, Card card, String destination) {
        if (destination.equals("hand")) {
            if (player.getUserId() == currentPlayerId && card.getAddCoins() != 0) {
                refreshAllPlayersCardsBought();
            }
            player.addCardToHand(card);
        }
        else if (destination.equals("deck")) {
            player.addCardToTopOfDeck(card);
        }
        else if (destination.equals("discard")) {
            player.addCardToDiscard(card);
        }
    }

    public void gainCardFinished(Player player, Card card) {
        if (!player.isShowCardAction() && hasUnfinishedGainCardActions()) {
            if(!card.getGainCardActions().isEmpty()) {
                setPlayerGainCardAction(player, card);
            }
            else {
                setPlayerGainCardAction(player, getCardWithUnfinishedGainCardActions());
            }
        }
    }

    public void moveGainedCard(Player player, Card card, String destination) {
        removeGainedCard(player, card);
        addCardToDestination(player, cardMap.get(card.getCardId()), destination);
        gainCardFinished(player, card);
    }

    private void removeGainedCard(Player player, Card gainedCard) {
        if (gainedCard.getDestination().equals("discard")) {
            player.getDiscard().removeLastOccurrence(gainedCard);
        }
        else if (gainedCard.getDestination().equals("deck")) {
            player.getDeck().remove(gainedCard);
        }
        else if (gainedCard.getDestination().equals("tinker")) {
            player.getTinkerCards().remove(gainedCard);
        }
        else if (gainedCard.getDestination().equals("hand")) {
            player.getHand().remove(gainedCard);
        }
    }

    private void waitIfNotCurrentPlayer(Player player) {
        if (!isCurrentPlayer(player)) {
            getPlayersWithCardActions().add(player.getUserId());
            if (!hasIncompleteCard() && !getCurrentPlayer().isShowCardAction()) {
                setPlayerCardAction(getCurrentPlayer(), CardAction.getWaitingForPlayersCardAction());
            }
        }
    }

    public void setPlayerGainCardAction(Player player, Card card) {
        CardAction firstReaction = card.getGainCardActions().values().iterator().next();
        if (card.getGainCardActions().size() == 1) {
            card.getGainCardActions().clear();
            cardsWithGainCardActions.remove(card.getCardId());
            setPlayerCardAction(player, firstReaction);
        }
        else {
            cardsWithGainCardActions.put(card.getCardId(), card);
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_REACTION);
            cardAction.setCardName("Choose Reaction");
            cardAction.setAssociatedCard(firstReaction.getAssociatedCard());
            cardAction.getCards().add(firstReaction.getAssociatedCard());
            cardAction.setDestination(firstReaction.getDestination());
            cardAction.setInstructions("Choose which card you want to process first to react to gaining this card.");
            for (String action : card.getGainCardActions().keySet()) {
                cardAction.getChoices().add(new CardActionChoice(action, action));
            }
            setPlayerCardAction(player, cardAction);
        }
    }

    private void setGainedCardActions(Player player, Card cardCopy, String destination) {
        Map<String, CardAction> gainCardActions = getGainCardActions(player, cardCopy, destination);
        cardCopy.setGainCardActions(gainCardActions);
    }

    private Map<String, CardAction> getGainCardActions(Player player, Card cardCopy, String destination) {
        Map<String, CardAction> gainCardActions = new HashMap<String, CardAction>();
        if (royalSealCardPlayed && player.getUserId() == currentPlayerId && !destination.equals("deck")) {
            CardAction cardAction = GainCardsReactionHandler.getCardAction("Royal Seal", this, player, cardCopy, destination);
            if (cardAction != null) {
                gainCardActions.put("Royal Seal", cardAction);
            }
        }
        if (checkWatchtower && player.hasWatchtower()) {
            CardAction cardAction = GainCardsReactionHandler.getCardAction("Watchtower", this, player, cardCopy, destination);
            if (cardAction != null) {
                gainCardActions.put("Watchtower", cardAction);
            }
        }
        if (checkTinker && player.isPlayedTinker()) {
            CardAction cardAction = GainCardsReactionHandler.getCardAction("Tinker", this, player, cardCopy, destination);
            if (cardAction != null) {
                gainCardActions.put("Tinker", cardAction);
            }
        }
        CardAction cardActionForCard = GainCardsSpecialActionHandler.getCardAction(this, player, cardCopy);
        if (cardActionForCard != null) {
            gainCardActions.put(cardCopy.getName(), cardActionForCard);
        }
        return gainCardActions;
    }

    public void endPlayerTurn(Player player) {
        endPlayerTurn(player, true);
    }

    public void endPlayerTurn(final Player player, boolean confirm) {
        if (allowEndTurn()) {
            endTurn(player, confirm);
            endingTurn = false;
        }
    }

    public synchronized boolean allowEndTurn() {
        if (!endingTurn) {
            endingTurn = true;
            return true;
        }
        return false;
    }

    private void endTurn(Player player, boolean confirm) {
        updateLastActivity();
        if (player.getUserId() == currentPlayerId) {
            if (hasIncompleteCard()) {
                GameError error = new GameError(GameError.COMPUTER_ERROR, player.getUsername() + " could not end turn because there was an incomplete action for: " + getIncompleteCard().getCardName());
                logError(error, false);
                removeIncompleteCard();
            }
            int coins = player.getCoins();
            if (playTreasureCards) {
                coins += player.getCoinsInHand();
            }
            if (confirm && !player.isComputer() && player.getBuys() > 0 && (coins > 2 || (coins == 2 && twoCostKingdomCards > 0))) {
                CardAction confirmEndTurn = new CardAction(CardAction.TYPE_YES_NO);
                confirmEndTurn.setCardName("Confirm End Turn");
                confirmEndTurn.setInstructions("You still have buys remaining, are you sure you want to end your turn?");
                setPlayerCardAction(player, confirmEndTurn);
                return;
            }
            if (playedWalledVillage && actionCardsInPlay <= 2) {
                playedWalledVillage = false;
                List<Card> walledVillagesPlayed = new ArrayList<Card>(1);
                for (Card card : actionCardsPlayed) {
                    if (card.getName().equals("Walled Village")) {
                        walledVillagesPlayed.add(card);
                    }
                }
                if (walledVillagesPlayed.size() == 1) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                    cardAction.setDeck(Card.DECK_PROMO);
                    cardAction.setCardName("Walled Village");
                    cardAction.setInstructions("Do you want your Walled Village to go on top of your deck?");
                    cardAction.getCards().addAll(walledVillagesPlayed);
                    setPlayerCardAction(player, cardAction);
                    return;
                }
                else if (walledVillagesPlayed.size() > 1) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                    cardAction.setDeck(Card.DECK_PROMO);
                    cardAction.setCardName("Walled Village");
                    for (Card card : walledVillagesPlayed) {
                        card.setAutoSelect(true);
                        cardAction.getCards().add(card);
                    }
                    cardAction.setNumCards(cardAction.getCards().size());
                    cardAction.setInstructions("Both Walled Villages played have been selected. Click on any that you do not want to go on top of your deck and then click Done.");
                    cardAction.setButtonValue("Done");
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            if (playedTreasuryCard) {
                playedTreasuryCard = false;
                if (!boughtVictoryCard) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                    cardAction.setDeck(Card.DECK_SEASIDE);
                    cardAction.setCardName("Treasury");
                    for (Card card : cardsPlayed) {
                        if (card.getName().equals("Treasury")) {
                            card.setAutoSelect(true);
                            cardAction.getCards().add(card);
                        }
                    }
                    cardAction.setNumCards(cardAction.getCards().size());
                    cardAction.setInstructions("All Treasuries played have been selected. Click on any that you do not want to go on top of your deck and then click Done.");
                    cardAction.setButtonValue("Done");
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            if (playedAlchemistCard) {
                playedAlchemistCard = false;
                boolean hasPotion;
                if (playTreasureCards) {
                    hasPotion = potionsPlayed > 0;
                }
                else {
                    hasPotion = player.getPotions() > 0 || potionsPlayed > 0;
                }
                if (hasPotion) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                    cardAction.setDeck(Card.DECK_ALCHEMY);
                    cardAction.setCardName("Alchemist");
                    for (Card card : cardsPlayed) {
                        if (card.getName().equals("Alchemist")) {
                            card.setAutoSelect(true);
                            cardAction.getCards().add(card);
                        }
                    }
                    cardAction.setNumCards(cardAction.getCards().size());
                    cardAction.setInstructions("All Alchemists played have been selected. Click on any that you do not want to go on top of your deck and then click Done.");
                    cardAction.setButtonValue("Done");
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            if (playedHerbalistCard) {
                playedHerbalistCard = false;
                Set<Card> treasureCards = new HashSet<Card>(treasureCardsPlayed);
                if (treasureCards.size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                    cardAction.setDeck(Card.DECK_ALCHEMY);
                    cardAction.setCardName("Herbalist");
                    int herbalistCardsPlayed = 0;
                    for (Card card : cardsPlayed) {
                        if (card.getName().equals("Herbalist")) {
                            herbalistCardsPlayed++;
                        }
                    }
                    int numCards = herbalistCardsPlayed;
                    if (treasureCards.size() < herbalistCardsPlayed) {
                        numCards = treasureCards.size();
                    }
                    cardAction.setNumCards(numCards);
                    cardAction.getCards().addAll(treasureCards);
                    cardAction.setInstructions("Select up to " + KingdomUtil.getPlural(numCards, "Treasure card") + " to go back on top of your deck and then click Done");
                    cardAction.setButtonValue("Done");
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            if (checkScheme && schemeCardsPlayed > 0) {
                if (!actionCardsPlayed.isEmpty()) {
                    int numCards = schemeCardsPlayed;
                    if (actionCardsPlayed.size() < schemeCardsPlayed) {
                        numCards = actionCardsPlayed.size();
                    }
                    schemeCardsPlayed = 0;
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName("Scheme");
                    cardAction.setNumCards(numCards);
                    cardAction.getCards().addAll(actionCardsPlayed);
                    cardAction.setInstructions("Select up to " + KingdomUtil.getPlural(numCards, "Action card") + " to go back on top of your deck and then click Done");
                    cardAction.setButtonValue("Done");
                    setPlayerCardAction(player, cardAction);
                    return;
                }
            }
            final boolean takeOutpostTurn = outpostCardPlayed && !outpostTurn;
            if (trackSmugglersCards && !takeOutpostTurn) {
                smugglersCards.clear();
                smugglersCards.addAll(smugglersCardsGained);
                smugglersCardsGained.clear();
            }
            if (showDuration) {
                for (Card card : durationCardsPlayed) {
                    cardsPlayed.remove(card);
                }
            }
            if (copiedPlayedCard) {
                for (Card card : cardsPlayed) {
                    if (card.isCopied()) {
                        player.addCardToDiscard(cardMap.get(card.getCardId()));
                        if(card.getName().equals("Storybook")) {
                            player.getDiscard().addAll(card.getAssociatedCards());
                        }
                    }
                    else {
                        player.addCardToDiscard(card);
                    }
                }
            }
            else {
                player.getDiscard().addAll(cardsPlayed);
            }
            player.getDiscard().addAll(player.getDurationCards());
            if (trackEdictCards) {
                edictCards.removeAll(player.getLastTurnEdictCards());
                player.getLastTurnEdictCards().clear();
                player.getLastTurnEdictCards().addAll(player.getEdictCards());
                player.getEdictCards().clear();
            }
            player.getDurationCards().clear();
            player.getDurationCards().addAll(durationCardsPlayed);
            if (takeOutpostTurn) {
                player.endTurn(3);
            }
            else {
                player.endTurn(5);
            }
            if (lighthousePlayed) {
                player.setHasLighthouse(true);
                lighthousePlayed = false;
            }
            refreshHandArea(player);
            refreshSupply(player);
            if (!takeOutpostTurn) {
                previousPlayerId = currentPlayerId;
                refreshEndTurn(currentPlayerId);
                currentPlayerIndex = getNextPlayerIndex();
                currentPlayerId = players.get(currentPlayerIndex).getUserId();
            }
            if (historyEntriesAddedThisTurn == 0) {
                addHistory(player.getUsername(), " ended ", player.getPronoun(), " turn without doing anything");
            }
            if (finishGameOnNextEndTurn) {
                determineWinner();
                status = STATUS_GAME_FINISHED;
                refreshAllPlayersGameStatus();
                refreshAllPlayersTitle();
                return;
            }

            if (hasNextAction()) {
                for (String nextAction : nextActionQueue) {
                    GameError error = new GameError(GameError.COMPUTER_ERROR, player.getUsername() + " game has next action on end turn: " + nextAction);
                    logError(error, false);
                }
                nextActionQueue.clear();
            }

            resetTurnVariables();

            prepareNextPlayerTurn(takeOutpostTurn);

            processingClick.remove(player.getUserId());
        }
    }

    private void resetTurnVariables() {
        gainTournamentBonus = false;
        historyEntriesAddedThisTurn = 0;
        goonsCardsPlayed = 0;
        hagglerCardsInPlay = 0;
        schemeCardsPlayed = 0;
        hoardCardsPlayed = 0;
        talismanCardsPlayed = 0;
        royalSealCardPlayed = false;
        copiedPlayedCard = false;
        boughtVictoryCard = false;
        previousPlayerCardsPlayed.clear();
        previousPlayerCardsBought.clear();
        previousPlayerCardsPlayed.addAll(cardsPlayed);
        previousPlayerCardsBought.addAll(cardsBought);
        cardsPlayed.clear();
        cardsBought.clear();
        repeatedActions.clear();
        golemActions.clear();
        trashedTreasureCards.clear();
        durationCardsPlayed.clear();
        treasureCardsPlayed.clear();
        contrabandCards.clear();
        potionsPlayed = 0;
        princessCardPlayed = false;
        numActionsCardsPlayed = 0;
        playersWithCardActions.clear();
        if (trackActionCardsPlayed) {
            actionCardsPlayed.clear();
        }
        actionCardsInPlay = 0;
        crossroadsPlayed = 0;
        highwayCardsInPlay = 0;
        laborerCardsInPlay = 0;
        goodwillCardsInPlay = 0;
        fruitTokensPlayed = 0;
        cardsWithGainCardActions.clear();
    }

    private void prepareNextPlayerTurn(boolean takeOutpostTurn) {
        if (costDiscount > 0 || actionCardDiscount > 0) {
            refreshAllPlayersSupply();
            refreshAllPlayersHandArea();
        }
        costDiscount = 0;
        actionCardDiscount = 0;

        final Player nextPlayer = getCurrentPlayer();
        startPlayerTurn(nextPlayer);

        if (showDuration) {
            if (checkTinker) {
                nextPlayer.setPlayedTinker(false);
                if (!nextPlayer.getTinkerCards().isEmpty()) {
                    for (Card card : nextPlayer.getTinkerCards()) {
                        nextPlayer.addCardToHand(card);
                    }
                    addHistory(nextPlayer.getUsername(), " added ", KingdomUtil.groupCards(nextPlayer.getTinkerCards(), true), " from ", nextPlayer.getPronoun(), " ", KingdomUtil.getWordWithBackgroundColor("Tinker", Card.ACTION_DURATION_COLOR), " to ", nextPlayer.getPronoun(), " hand");
                    nextPlayer.getTinkerCards().clear();
                }
            }
            DurationHandler.applyDurationCards(this, nextPlayer);
            actionCardsInPlay += nextPlayer.getDurationCards().size();
        }
        if (usingLeaders && nextPlayer.getCardBonusTurns() > 0) {
            nextPlayer.drawCards(1);
            nextPlayer.setCardBonusTurns(nextPlayer.getCardBonusTurns() - 1);
            addHistory(nextPlayer.getUsername(), " gained +1 Card from ", nextPlayer.getPronoun(), " leader");
        }
        if (usingLeaders && nextPlayer.getBuyBonusTurns() > 0) {
            nextPlayer.addBuys(1);
            nextPlayer.setBuyBonusTurns(nextPlayer.getBuyBonusTurns() - 1);
            addHistory(nextPlayer.getUsername(), " gained +1 Buy from ", nextPlayer.getPronoun(), " leader");
        }
        if (checkHorseTraders && !nextPlayer.getSetAsideCards().isEmpty()) {
            int numHorseTraders = nextPlayer.getSetAsideCards().size();
            for (Card card : nextPlayer.getSetAsideCards()) {
                nextPlayer.addCardToHand(card);
            }
            nextPlayer.getSetAsideCards().clear();
            addHistory(nextPlayer.getUsername(), " returned ", String.valueOf(numHorseTraders), " ", KingdomUtil.getCardWithBackgroundColor(horseTradersCard), " to ", nextPlayer.getPronoun(), " hand, and got to draw ", KingdomUtil.getPlural(numHorseTraders, "Card"));
            nextPlayer.drawCards(numHorseTraders);
        }
        refreshHandArea(nextPlayer);
        refreshSupply(nextPlayer);
        playBeep(nextPlayer);
        setPlayerInfoDialog(nextPlayer, InfoDialog.getYourTurnInfoDialog());
        refreshAllPlayersPlayingArea();
        refreshAllPlayersGameStatus();
        refreshAllPlayersTitle();
        if (refreshPeddler) {
            refreshAllPlayersSupply();
        }
        if (takeOutpostTurn) {
            addHistory(nextPlayer.getUsername(), " is taking an extra turn from the ", KingdomUtil.getWordWithBackgroundColor("Outpost", Card.ACTION_DURATION_COLOR), " Card");
            outpostCardPlayed = false;
            outpostTurn = true;
        }
        else {
            outpostCardPlayed = false;
            outpostTurn = false;
        }

        final int nextPlayerUserId = nextPlayer.getUserId();
        if (nextPlayer.isComputer()) {
            if(takeOutpostTurn) {
                if(previousPlayerId != 0) {
                    try {
                        Thread.sleep(2500);
                    }
                    catch (Exception e) {
                        GameError error = new GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e));
                        logError(error);
                    }
                }
                computerPlayers.get(nextPlayerUserId).doNextAction();
            }
            else {
                new Thread(
                        new Runnable() {
                            public void run() {
                                if(previousPlayerId != 0) {
                                    try {
                                        if(getPreviousPlayer().isComputer() || !allComputerOpponents) {
                                            Thread.sleep(2700);
                                        }
                                        else {
                                            Thread.sleep(2200);
                                        }
                                    }
                                    catch (Exception e) {
                                        GameError error = new GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(e));
                                        logError(error);
                                    }
                                }
                                computerPlayers.get(nextPlayerUserId).doNextAction();
                            }
                        }
                ).start();
            }
        }
    }

    public boolean currentlyWinning(int userId) {
        List<Player> playersCopy = new ArrayList<Player>(players);
        Collections.sort(playersCopy);
        Player firstPlayer = playersCopy.get(0);
        if (firstPlayer.getUserId() == userId) {
            return true;
        }
        int highScore = firstPlayer.getVictoryPoints();
        int leastTurns = firstPlayer.getTurns();
        for (Player player : playersCopy) {
            if (player.getVictoryPoints() == highScore && leastTurns == player.getTurns()) {
                if (player.getUserId() == userId) {
                    return true;
                }
            }
            else {
                break;
            }
        }
        return false;
    }

    public int getLosingMargin(int userId) {
        List<Player> playersCopy = new ArrayList<Player>(players);
        Collections.sort(playersCopy);
        if (playersCopy.isEmpty()) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, "Players Copy Empty, players size: "+players.size());
            logError(error, false);
            return 0;
        }
        Player firstPlayer = playersCopy.get(0);
        if (firstPlayer.getUserId() == userId) {
            return 0;
        }
        for (Player player : playersCopy) {
            if (player.getUserId() == userId) {
                return firstPlayer.getVictoryPoints() - player.getVictoryPoints();
            }
        }
        return 0;
    }

    private synchronized void determineWinner() {
        if (!determinedWinner) {
            determinedWinner = true;
            Collections.sort(players);
            Player firstPlayer = players.get(0);
            int highScore = firstPlayer.getVictoryPoints();
            int leastTurns = firstPlayer.getTurns();
            int marginOfVictory = players.get(0).getVictoryPoints() - players.get(1).getVictoryPoints();
            List<String> winners = new ArrayList<String>();
            for (Player player : players) {
                if (player.getVictoryPoints() == highScore && leastTurns == player.getTurns()) {
                    player.setWinner(true);
                    player.setMarginOfVictory(marginOfVictory);
                    winners.add(player.getUsername());
                }
                else {
                    break;
                }
            }

            if (winners.size() == 1) {
                winnerString = winners.get(0) + " wins!";
            }
            else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < winners.size(); i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    if (i == winners.size() - 1) {
                        sb.append("and ");
                    }
                    sb.append(winners.get(i));
                }
                winnerString = sb.toString() + " tie for the win!";
            }

            saveGameHistory();

            for (ComputerPlayer computerPlayer : computerPlayers.values()) {
                computerPlayer.setStopped(true);
                playerExitedGame(computerPlayer.getPlayer());
            }
        }
    }

    public void saveGameHistory() {
        if (!savedGameHistory) {
            savedGameHistory = true;
            GameHistory history = new GameHistory();
            history.setStartDate(creationTime);
            history.setEndDate(new Date());
            history.setNumPlayers(numPlayers);
            history.setNumComputerPlayers(numComputerPlayers);
            history.setCustom(custom);
            history.setAnnotatedGame(annotatedGame);
            history.setRecentGame(recentGame);
            history.setRecommendedSet(recommendedSet);
            history.setTestGame(testGame);
            history.setAbandonedGame(abandonedGame);
            history.setGameEndReason(gameEndReason);
            history.setWinner(winnerString);
            history.setShowVictoryPoints(showVictoryPoints);
            history.setIdenticalStartingHands(identicalStartingHands);
            history.setRepeated(repeated);
            history.setMobile(mobile);
            history.setLeaders(usingLeaders);
            List<String> cardNames = new ArrayList<String>();
            for (Card kingdomCard : kingdomCards) {
                cardNames.add(kingdomCard.getName());
            }
            if (includePlatinumCards) {
                cardNames.add("Platinum");
            }
            if (includeColonyCards) {
                cardNames.add("Colony");
            }
            history.setCards(KingdomUtil.implode(cardNames, ","));
            gameManager.saveGameHistory(history);

            StringBuilder sb = new StringBuilder();
            for (PlayerTurn playerTurn : turnHistory) {
                sb.append(KingdomUtil.implode(playerTurn.getHistory(), ";"));
            }

            GameLog log = new GameLog();
            log.setGameId(history.getGameId());
            log.setLog(sb.toString());
            gameManager.saveGameLog(log);
            logId = log.getLogId();

            for (Player player : players) {
                gameManager.saveGameUserHistory(history.getGameId(), player);
            }
        }
    }

    public void playerExitedGame(Player player){
        updateLastActivity();
        if (!player.isComputer()) {
            addGameChat(player.getUsername()+ " exited the game");
        }
        playersExited.add(player.getUserId());
        if(playersExited.size() == players.size()){
            reset();
        }
    }

    public void playerQuitGame(Player player) {
        updateLastActivity();
        player.setQuit(true);
        gameEndReason = player.getUsername() + " quit the game";
        determineWinner();
        winnerString = "";
        status = STATUS_GAME_FINISHED;
        refreshAllPlayersGameStatus();
        refreshAllPlayersTitle();
        addGameChat(gameEndReason);
    }

    public int getNextPlayerIndex() {
        if (currentPlayerIndex == players.size() - 1) {
            return 0;
        }
        return currentPlayerIndex+1;
    }

    public int calculateNextPlayerIndex(int playerIndex){
        if (playerIndex == players.size() - 1) {
            return 0;
        }
        return playerIndex + 1;
    }

    public Player getPreviousPlayer() {
        return playerMap.get(previousPlayerId);
    }

    public Player getCurrentPlayer() {
        return playerMap.get(currentPlayerId);
    }

    public void refreshPlayingArea(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshPlayingArea(true);
    }

    public void refreshCardsBought(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshCardsBoughtDiv(true);
    }

    public void refreshSupply(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshSupply(true);
    }

    public void refreshHand(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshHand(true);
    }

    public void refreshHandArea(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshHandArea(true);
    }

    public void refreshDiscard(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshDiscard(true);
    }

    public void refreshCardAction(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshCardAction(true);
    }

    public void refreshInfoDialog(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshInfoDialog(true);
    }

    public void refreshGameStatus(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshGameStatus(true);
    }

    public void refreshTitle(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshTitle(true);
    }

    public void refreshChat(int userId) {
        Refresh refresh = needsRefresh.get(userId);
        refresh.setRefreshChat(true);
    }

    public void closeCardActionDialog(Player player) {
        player.setShowCardAction(false);
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshCardAction(false);
        refresh.setCloseCardActionDialog(true);
    }

    public void closeLoadingDialog(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setCloseLoadingDialog(true);
    }

    public void refreshAll(Player player){
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setRefreshGameStatus(true);
        if(player.isShowCardAction() && player.getCardAction() != null){
            refresh.setRefreshCardAction(true);
        }
        refresh.setRefreshHandArea(true);
        refresh.setRefreshPlayers(true);
        refresh.setRefreshPlayingArea(true);
        refresh.setRefreshSupply(true);

        if(status == STATUS_GAME_IN_PROGRESS && !hasIncompleteCard() && !getCurrentPlayer().isShowCardAction()) {
            if(!getRepeatedActions().isEmpty()) {
                playRepeatedAction(getCurrentPlayer(), false);
            }
            else if (!getGolemActions().isEmpty()) {
                playGolemActionCard(getCurrentPlayer());
            }
        }
    }

    public void refreshAllPlayersPlayingArea(){
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshPlayingArea(true);
        }
    }

    public void refreshAllPlayersCardsPlayed() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshCardsPlayedDiv(true);
        }
    }

    public void refreshAllPlayersCardsBought() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshCardsBoughtDiv(true);
        }
    }

    public void refreshAllPlayersHistory() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshHistory(true);
        }
    }

    public void refreshAllPlayersSupply() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshSupply(true);
        }
    }

    public void refreshAllPlayersHand() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshHand(true);
        }
    }

    public void refreshAllPlayersHandArea() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshHandArea(true);
        }
    }

    public void refreshAllPlayersDiscard() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshDiscard(true);
        }
    }

    public void refreshAllPlayersPlayers() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshPlayers(true);
        }
    }

    public void refreshAllPlayersGameStatus() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshGameStatus(true);
        }
    }

    public void refreshEndTurn(int currentPlayerId) {
        for (Integer userId : needsRefresh.keySet()) {
            if (userId != currentPlayerId) {
                Refresh refresh = needsRefresh.get(userId);
                refresh.setRefreshEndTurn(true);
                boolean refreshHandArea = refresh.isRefreshHand() || refresh.isRefreshHandArea() || refresh.isRefreshDiscard();
                if (refreshHandArea) {
                    refresh.setRefreshHandOnEndTurn(true);
                }
                if (refresh.isRefreshSupply()) {
                    refresh.setRefreshSupply(true);
                }
            }
        }
    }

    public void refreshAllPlayersChat() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshChat(true);
        }
    }

    public void refreshAllPlayersTitle() {
        for (Refresh refresh : needsRefresh.values()) {
            refresh.setRefreshTitle(true);
        }
    }

    public void playBeep(Player player) {
        Refresh refresh = needsRefresh.get(player.getUserId());
        refresh.setPlayBeep(true);
    }

    public void addHistory(String... eventStrings) {
        StringBuffer sb = new StringBuffer();
        for (String s : eventStrings) {
            sb.append(s);
        }
        currentTurn.addHistory(sb.toString());
        historyEntriesAddedThisTurn++;
        refreshAllPlayersHistory();
    }

    public List<ChatMessage> getChats() {
        return chats;
    }

    public void addChat(Player player, String message) {
        updateLastActivity();
        chats.add(new ChatMessage(player.getUsername() + ": " + message, player.getChatColor()));
        refreshAllPlayersChat();
    }

    public void addPrivateChat(User sender, User receiver, String message) {
        updateLastActivity();
        chats.add(new ChatMessage("Private chat from " + sender.getUsername() + ": " + message, "black", receiver.getUserId()));
        refreshChat(receiver.getUserId());
    }

    public void addGameChat(String message){
        chats.add(new ChatMessage(message, "black"));
        refreshAllPlayersChat();
    }

    public String getNextColor(){
        String color = colors.get(currentColorIndex);
        if(currentColorIndex == colors.size()-1){
            currentColorIndex = 0;
        } else{
            currentColorIndex++;
        }
        return color;
    }

    public boolean isShowGardens() {
        return showGardens;
    }

    public boolean isShowFarmlands() {
        return showFarmlands;
    }

    public boolean isShowVictoryCoins() {
        return showVictoryCoins;
    }

    public boolean isShowVineyard() {
        return showVineyard;
    }

    public boolean isShowSilkRoads() {
        return showSilkRoads;
    }

    public boolean isShowCathedral() {
        return showCathedral;
    }

    public boolean isShowFairgrounds() {
        return showFairgrounds;
    }

    public boolean isShowGreatHall() {
        return showGreatHall;
    }

    public boolean isShowHarem() {
        return showHarem;
    }

    public boolean isShowDuke() {
        return showDuke;
    }

    public boolean isShowNobles() {
        return showNobles;
    }

    public boolean isShowArchbishops() {
        return showArchbishops;
    }

    public boolean isShowDuration() {
        return showDuration;
    }

    public boolean isShowEmbargoTokens() {
        return showEmbargoTokens;
    }

    public boolean isShowTrollTokens() {
        return showTrollTokens;
    }

    public boolean isShowIslandCards() {
        return showIslandCards;
    }

    public boolean isShowMuseumCards() {
        return showMuseumCards;
    }

    public boolean isShowCityPlannerCards() {
        return showCityPlannerCards;
    }

    public boolean isShowNativeVillage() {
        return showNativeVillage;
    }

    public boolean isShowPirateShipCoins() {
        return showPirateShipCoins;
    }

    public boolean isShowFruitTokens() {
        return showFruitTokens;
    }

    public boolean isShowCattleTokens() {
        return showCattleTokens;
    }

    public boolean isShowSins() {
        return showSins;
    }

    public int getCostDiscount() {
        if (trackHighway && highwayCardsInPlay > 0) {
            return costDiscount + highwayCardsInPlay;
        }
        return costDiscount;
    }

    public int getActionCardDiscount() {
        return actionCardDiscount;
    }

    public void incrementCostDiscount() {
        costDiscount++;
        addHistory("All cards cost 1 less coin this turn");
    }

    public void incrementActionCardDiscount(int discount) {
        actionCardDiscount += discount;
    }

    public Map<Integer, Card> getCardMap() {
        return cardMap;
    }

    public Map<Integer, Card> getSupplyMap() {
        return supplyMap;
    }

    public Deque<RepeatedAction> getRepeatedActions() {
        return repeatedActions;
    }

    public Deque<Card> getGolemActions() {
        return golemActions;
    }

    public List<Card> getTrashedTreasureCards() {
        return trashedTreasureCards;
    }

    public Map<Integer, Card> getMasqueradeCards() {
        return masqueradeCards;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public List<Card> getTrashedCards() {
        return trashedCards;
    }

    public List<Card> getSetAsideCards() {
        return setAsideCards;
    }

    public int getNumActionsCardsPlayed() {
        return numActionsCardsPlayed;
    }

    public int getActionCardsInPlay() {
        return actionCardsInPlay;
    }

    public List<Card> getSmugglersCards() {
        return smugglersCards;
    }

    public List<Card> getDurationCardsPlayed() {
        return durationCardsPlayed;
    }

    public Card getThroneRoomCard() {
        return throneRoomCard;
    }

    public Card getKingsCourtCard() {
        return kingsCourtCard;
    }

    public Card getAttackCard() {
        return attackCard;
    }

    public Card removeNextBlackMarketCard() {
        if (blackMarketCards.isEmpty()) {
            return null;
        }
        return blackMarketCards.remove(0);
    }

    public List<Card> getBlackMarketCards() {
        return blackMarketCards;
    }

    public void setBlackMarketCards(List<Card> blackMarketCards) {
        this.blackMarketCards = blackMarketCards;
    }

    public List<String> getDecks() {
        return decks;
    }

    public void setDecks(List<String> decks) {
        this.decks = decks;
    }

    public boolean isUsePotions() {
        return usePotions;
    }

    public boolean isPlayTreasureCards() {
        return playTreasureCards;
    }

    public void setPlayTreasureCards(boolean playTreasureCards) {
        this.playTreasureCards = playTreasureCards;
    }

    public int getNumEmptyPiles() {
        return emptyPiles.size();
    }

    public boolean isIncludePlatinumCards() {
        return includePlatinumCards;
    }

    public void setIncludePlatinumCards(boolean includePlatinumCards) {
        this.includePlatinumCards = includePlatinumCards;
    }

    public boolean isIncludeColonyCards() {
        return includeColonyCards;
    }

    public void setIncludeColonyCards(boolean includeColonyCards) {
        this.includeColonyCards = includeColonyCards;
    }

    public List<Card> getTreasureCardsPlayed() {
        return treasureCardsPlayed;
    }

    public List<Card> getBlackMarketTreasureCardsPlayed() {
        return blackMarketTreasureCardsPlayed;
    }

    public Set<Card> getContrabandCards() {
        return contrabandCards;
    }

    public boolean isTrackContrabandCards() {
        return trackContrabandCards;
    }

    public boolean isTrackBankCards() {
        return trackBankCards;
    }

    public int getTradeRouteTokensOnMat() {
        return tradeRouteTokensOnMat;
    }

    public Map<Integer, Boolean> getTradeRouteTokenMap() {
        return tradeRouteTokenMap;
    }

    public boolean isTrackTradeRouteTokens() {
        return trackTradeRouteTokens;
    }

    public List<Card> getBlackMarketCardsToBuy() {
        return blackMarketCardsToBuy;
    }

    public void setBlackMarketCardsToBuy(List<Card> blackMarketCardsToBuy) {
        this.blackMarketCardsToBuy = blackMarketCardsToBuy;
    }

    public Map<Integer, List<Card>> getCostMap() {
        return costMap;
    }

    public Map<Integer, List<Card>> getPotionCostMap() {
        return potionCostMap;
    }

    public Map<Integer, ComputerPlayer> getComputerPlayers() {
        return computerPlayers;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getGameTime() {
        return KingdomUtil.getTimeAgo(creationTime);
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public String getLastActivityString() {
        return KingdomUtil.getTimeAgo(lastActivity);
    }

    public RandomizingOptions getRandomizingOptions() {
        return randomizingOptions;
    }

    public void setRandomizingOptions(RandomizingOptions randomizingOptions) {
        this.randomizingOptions = randomizingOptions;
    }

    public String getGameEndReason() {
        return gameEndReason;
    }

    public void setGameEndReason(String gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public String getWinnerString() {
        return winnerString;
    }

    public synchronized boolean allowClick(Player player) {
        if (player.isComputer()) {
            return true;
        }
        if (processingClick.containsKey(player.getUserId())) {
            return false;
        }
        else {
            processingClick.put(player.getUserId(), true);
            return true;
        }
    }

    public void removeProcessingClick(Player player) {
        processingClick.remove(player.getUserId());
    }

    public void setPlayerCardAction(Player player, CardAction cardAction) {
        if (cardAction == null) {
            GameError error = new GameError(GameError.GAME_ERROR, "setPlayerCardAction, cardAction is null for user: "+player.getUsername());
            logError(error, false);
        }
        else {
            if (player.isShowCardAction() && player.getCardAction().isWaitingForPlayers()) {
                closeCardActionDialog(player);
                closeLoadingDialog(player);
            }
            player.setCardAction(cardAction);
            player.setShowCardAction(true);
            if (player.isComputer() && status == STATUS_GAME_IN_PROGRESS) {
                computerPlayers.get(player.getUserId()).handleCardAction(cardAction);
            }
            else {
                refreshCardAction(player);
            }
        }
    }

    public void setPlayerInfoDialog(Player player, InfoDialog infoDialog) {
        if(!player.isComputer()) {
            player.setShowInfoDialog(true);
            player.setInfoDialog(infoDialog);
            refreshInfoDialog(player);
        }
        else if (infoDialog.isError()) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, infoDialog.getMessage());
            logError(error);
            computerPlayers.get(player.getUserId()).setError(true);
        }
    }

    public Card getEstateCard() {
        return cardMap.get(Card.ESTATE_ID);
    }

    public Card getDuchyCard() {
        return cardMap.get(Card.DUCHY_ID);
    }

    public Card getProvinceCard() {
        return cardMap.get(Card.PROVINCE_ID);
    }

    public Card getColonyCard() {
        return cardMap.get(Card.COLONY_ID);
    }

    public Card getCopperCard() {
        return cardMap.get(Card.COPPER_ID);
    }

    public Card getSilverCard() {
        return cardMap.get(Card.SILVER_ID);
    }

    public Card getGoldCard() {
        return cardMap.get(Card.GOLD_ID);
    }

    public Card getPlatinumCard() {
        return cardMap.get(Card.PLATINUM_ID);
    }

    public Card getCurseCard() {
        return cardMap.get(Card.CURSE_ID);
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void cardActionSubmitted(Player player, List<Integer> selectedCardIds, String yesNoAnswer, String choice, int numberChosen){
        if (allowClick(player)) {
            updateLastActivity();
            try {
                int coinsBefore = player.getCoins();
                CardActionHandler.handleSubmittedCardAction(this, player, selectedCardIds, yesNoAnswer, choice, numberChosen);
                if (coinsBefore != player.getCoins() && player.getUserId() == currentPlayerId) {
                    refreshSupply(player);
                }
            }
            finally {
                processingClick.remove(player.getUserId());
            }
        }
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public List<Card> getAvailableTreasureCardsInSupply() {
        List<Card> cards = new ArrayList<Card>();
        for (Card card : supplyMap.values()) {
            int numInSupply = getNumInSupply(card.getCardId());
            if (numInSupply > 0 && card.isTreasure()) {
                cards.add(card);
            }
        }
        return cards;
    }

    public void logError(GameError error) {
        logError(error, true);    
    }

    public void logError(GameError error, boolean showInChat) {
        List<String> cardNames = new ArrayList<String>();
        for (Card kingdomCard : kingdomCards) {
            cardNames.add(kingdomCard.getName());
        }
        if (includePlatinumCards) {
            cardNames.add("Platinum");
        }
        if (includeColonyCards) {
            cardNames.add("Colony");
        }
        String kingdomCardsString = KingdomUtil.implode(cardNames, ",");
        List<String> playerNames = new ArrayList<String>();
        for (Player player : players) {
            playerNames.add(player.getUsername());
        }

        StringBuilder errorHistory = new StringBuilder();
        if (!playerNames.isEmpty()) {
            errorHistory.append("Players: ").append(KingdomUtil.implode(playerNames, ",")).append("; ");
        }
        if (!kingdomCards.isEmpty()) {
            errorHistory.append("Kingdom Cards: ").append(kingdomCardsString).append("; ");
        }
        if (getCurrentPlayer() != null && getCurrentPlayer().getHand() != null) {
            errorHistory.append("Current Player Hand: ").append(KingdomUtil.getCardNames(getCurrentPlayer().getHand(), false)).append("; ");
        }
        if (currentTurn != null) {
            errorHistory.append(KingdomUtil.implode(currentTurn.getHistory(), ";"));
        }

        error.setHistory(errorHistory.toString());
        gameManager.logError(error);

        if (showInChat) {
            if (error.isComputerError()) {
                addGameChat("The computer encountered an error. This error has been reported and will be fixed as soon as possible. If you would like to keep playing you can quit this game and start a new one with different cards.");
            }
            else {
                addGameChat("The game encountered an error. Try refreshing the page.");
            }
        }
    }

    public boolean isBuyPhase() {
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer.hasBoughtCard() || !treasureCardsPlayed.isEmpty();
    }

    public int getLogId() {
        return logId;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isAlwaysIncludeColonyAndPlatinum() {
        return alwaysIncludeColonyAndPlatinum;
    }

    public void setAlwaysIncludeColonyAndPlatinum(boolean alwaysIncludeColonyAndPlatinum) {
        this.alwaysIncludeColonyAndPlatinum = alwaysIncludeColonyAndPlatinum;
    }

    public boolean isNeverIncludeColonyAndPlatinum() {
        return neverIncludeColonyAndPlatinum;
    }

    public void setNeverIncludeColonyAndPlatinum(boolean neverIncludeColonyAndPlatinum) {
        this.neverIncludeColonyAndPlatinum = neverIncludeColonyAndPlatinum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    public Set<Card> getEdictCards() {
        return edictCards;
    }

    public int getGoonsCardsPlayed() {
        return goonsCardsPlayed;
    }

    public void setNumBMUComputerPlayers(int numBMUComputerPlayers) {
        this.numBMUComputerPlayers = numBMUComputerPlayers;
    }

    public int getNumEasyComputerPlayers() {
        return numEasyComputerPlayers;
    }

    public void setNumEasyComputerPlayers(int numEasyComputerPlayers) {
        this.numEasyComputerPlayers = numEasyComputerPlayers;
    }

    public int getNumMediumComputerPlayers() {
        return numMediumComputerPlayers;
    }

    public void setNumMediumComputerPlayers(int numMediumComputerPlayers) {
        this.numMediumComputerPlayers = numMediumComputerPlayers;
    }

    public int getNumHardComputerPlayers() {
        return numHardComputerPlayers;
    }

    public void setNumHardComputerPlayers(int numHardComputerPlayers) {
        this.numHardComputerPlayers = numHardComputerPlayers;
    }

    public boolean isAnnotatedGame() {
        return annotatedGame;
    }

    public void setAnnotatedGame(boolean annotatedGame) {
        this.annotatedGame = annotatedGame;
    }

    public boolean hasIncompleteCard() {
        return incompleteCard != null;
    }

    public IncompleteCard getIncompleteCard() {
        return incompleteCard;
    }

    public void removeIncompleteCard() {
        incompleteCard = null;
    }

    public void setIncompleteCard(IncompleteCard incompleteCard) {
        this.incompleteCard = incompleteCard;
    }

    public Queue<Card> getBlackMarketTreasureQueue() {
        return blackMarketTreasureQueue;
    }

    public String getPlayerList() {
        List<String> playerNames = new ArrayList<String>();
        for (Player player : players) {
            playerNames.add(player.getUsername());
        }
        return KingdomUtil.implode(playerNames, ", ");
    }

    public void updateLastActivity() {
        lastActivity = new Date();
    }

    public String getCardList() {
        List<String> cardNames = new ArrayList<String>();
        for (Card card : kingdomCardMap.values()) {
            cardNames.add(card.getName());
        }
        return KingdomUtil.implode(cardNames, ", ");
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public boolean isTestGame() {
        return testGame;
    }

    public void setTestGame(boolean testGame) {
        this.testGame = testGame;
    }

    public boolean isAbandonedGame() {
        return abandonedGame;
    }

    public void setAbandonedGame(boolean abandonedGame) {
        this.abandonedGame = abandonedGame;
    }

    public List<Card> getPrizeCards() {
        return prizeCards;
    }

    public String getPrizeCardsString() {
        if (prizeCards.isEmpty()) {
            return "None";
        }
        else {
            return KingdomUtil.getCardNames(prizeCards);
        }
    }

    public void setPrizeCards(List<Card> prizeCards) {
        this.prizeCards = prizeCards;
    }

    public boolean isShowPrizeCards() {
        return showPrizeCards;
    }

    public boolean isGainTournamentBonus() {
        return gainTournamentBonus;
    }

    public void setGainTournamentBonus(boolean gainTournamentBonus) {
        this.gainTournamentBonus = gainTournamentBonus;
    }

    public void princessCardPlayed() {
        if (!princessCardPlayed) {
            princessCardPlayed = true;
            costDiscount += 2;
            addHistory("All cards cost 2 less coins this turn");
        }
    }

    public int getBaneCardId() {
        return baneCardId;
    }

    public Card getHorseTradersCard() {
        return horseTradersCard;
    }

    public List<PlayerTurn> getRecentTurnHistory() {
        return recentTurnHistory;
    }

    private void startPlayerTurn(Player player) {
        if (recentTurnHistory.size() == maxHistoryTurnSize) {
            recentTurnHistory.removeFirst();
        }
        if (currentTurn != null) {
            currentTurn.addHistory("");
        }
        currentTurn = new PlayerTurn(player);
        recentTurnHistory.add(currentTurn);
        turnHistory.add(currentTurn);

        refreshAllPlayersHistory();
    }

    public boolean isShowVictoryPoints() {
        return showVictoryPoints;
    }

    public void setShowVictoryPoints(boolean showVictoryPoints) {
        this.showVictoryPoints = showVictoryPoints;
    }

    public boolean isIdenticalStartingHands() {
        return identicalStartingHands;
    }

    public void setIdenticalStartingHands(boolean identicalStartingHands) {
        this.identicalStartingHands = identicalStartingHands;
    }

    public boolean isAllComputerOpponents() {
        return allComputerOpponents;
    }

    public List<Player> getPlayersWaitingForBellTowerBonus() {
        return playersWaitingForBellTowerBonus;
    }

    public boolean hasNextAction() {
        return !nextActionQueue.isEmpty();
    }

    public String getNextAction() {
        return nextActionQueue.peek();
    }

    public void addNextAction(String nextAction) {
        nextActionQueue.add(nextAction);
    }

    public void removeNextAction() {
        nextActionQueue.remove();
    }

    public boolean isCheckEnchantedPalace() {
        return checkEnchantedPalace;
    }

    public void playerRevealedEnchantedPalace(int userId) {
        enchantedPalaceRevealed.add(userId);
    }

    public boolean revealedEnchantedPalace(int userId) {
        return enchantedPalaceRevealed.contains(userId);
    }

    public boolean isShowHedgeWizard() {
        return showHedgeWizard;
    }

    public boolean isShowGoldenTouch() {
        return showGoldenTouch;
    }

    public Set<Integer> getPlayersWithCardActions() {
        return playersWithCardActions;
    }

    public void repeat() {
        List<Player> playersCopy = new ArrayList<Player>(players);
        Map<Integer, ComputerPlayer> computerPlayerMapCopy = new HashMap<Integer, ComputerPlayer>(computerPlayers);
        reset(true);
        repeated = true;
        setupSupply();
        setupTokens();
        creationTime = new Date();
        updateLastActivity();
        for (Player player : playersCopy) {
            User user = new User();
            user.setUserId(player.getUserId());
            user.setGender(player.getGender());
            user.setUsername(player.getUsername());
            if (player.isComputer()) {
                ComputerPlayer computerPlayer = computerPlayerMapCopy.get(player.getUserId());
                addPlayer(user, true, computerPlayer.isBigMoneyUltimate(), computerPlayer.getDifficulty());
            }
            else {
                addPlayer(user);
            }
        }
        playersCopy.clear();
        computerPlayerMapCopy.clear();
        start();
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public int getPreviousPlayerId() {
        return previousPlayerId;
    }

    public List<Card> getPreviousPlayerCardsPlayed() {
        return previousPlayerCardsPlayed;
    }

    public List<Card> getPreviousPlayerCardsBought() {
        return previousPlayerCardsBought;
    }

    public void setEndingTurn(boolean endingTurn) {
        this.endingTurn = endingTurn;
    }

    public boolean isUsingLeaders() {
        return usingLeaders;
    }

    public void setUsingLeaders(boolean usingLeaders) {
        this.usingLeaders = usingLeaders;
    }

    public List<Card> getAvailableLeaders() {
        return availableLeaders;
    }

    public void setAvailableLeaders(List<Card> availableLeaders) {
        this.availableLeaders = availableLeaders;
    }

    public int getNumProvincesLeft() {
        return getNumInSupply(Card.PROVINCE_ID);
    }

    public int getNumColoniesLeft() {
        if (!includeColonyCards) {
            return 0;
        }
        return getNumInSupply(Card.COLONY_ID);
    }

    public boolean isCheckQuest() {
        return checkQuest;
    }

    public void setCopiedPlayedCard(boolean copiedPlayedCard) {
        this.copiedPlayedCard = copiedPlayedCard;
    }
    
    public void finishedGainCardAction(Player player, CardAction cardAction) {
        Card card = cardAction.getAssociatedCard();
        if (card.getGainCardActions().isEmpty()) {
            cardsWithGainCardActions.remove(card.getCardId());
        }
        if (!isCurrentPlayer(player) && !player.isShowCardAction() && player.getExtraCardActions().isEmpty() && !hasUnfinishedGainCardActions()) {
            getPlayersWithCardActions().remove(player.getUserId());
            if (getPlayersWithCardActions().isEmpty() && getCurrentPlayer().isShowCardAction() && getCurrentPlayer().getCardAction().isWaitingForPlayers()) {
                closeCardActionDialog(getCurrentPlayer());
                closeLoadingDialog(getCurrentPlayer());
            }
        }
    }

    public void finishTunnelCardAction(Player player) {
        getPlayersWithCardActions().remove(player.getUserId());
        if (getPlayersWithCardActions().isEmpty() && getCurrentPlayer().isShowCardAction() && getCurrentPlayer().getCardAction().isWaitingForPlayers()) {
            closeCardActionDialog(getCurrentPlayer());
            closeLoadingDialog(getCurrentPlayer());
        }
    }

    public int getCrossroadsPlayed() {
        return crossroadsPlayed;
    }

    public boolean hasUnfinishedGainCardActions() {
        return !cardsWithGainCardActions.isEmpty();
    }

    public Card getCardWithUnfinishedGainCardActions() {
        Iterator<Card> iterator = cardsWithGainCardActions.values().iterator();
        return iterator.next();
    }

    public boolean isCheckDuchess() {
        return checkDuchess;
    }

    public boolean isCardInSupply(Card card) {
        return isCardInSupply(card.getCardId());
    }

    public boolean isCardInSupply(int cardId) {
        return supply.get(cardId) != null && getNumInSupply(cardId) > 0;
    }

    public int getNumInSupply(Card card) {
        return getNumInSupply(card.getCardId());
    }

    public int getNumInSupply(int cardId) {
        return supply.get(cardId);
    }

    public void playerDiscardedCard(Player player, Card card) {
        if (checkTunnel && card.getName().equals("Tunnel") && isCardInSupply(getGoldCard())) {
            waitIfNotCurrentPlayer(player);
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName("Tunnel");
            cardAction.setInstructions("Do you want to reveal your Tunnel to gain a Gold?");
            cardAction.getCards().add(getGoldCard());
            cardAction.setAssociatedCard(card);
            setPlayerCardAction(player, cardAction);
        }
    }

    public Card getFoolsGoldCard() {
        return foolsGoldCard;
    }

    public void showUseFruitTokensCardAction(Player player) {
        if (isCurrentPlayer(player)) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_NUMBER_BETWEEN);
            cardAction.setDeck(Card.DECK_PROLETARIAT);
            cardAction.setCardName("Use Fruit Tokens");
            cardAction.setButtonValue("Done");
            cardAction.setStartNumber(0);
            cardAction.setEndNumber(player.getFruitTokens());
            cardAction.setInstructions("Click the number of Fruit Tokens you want to use.");
            setPlayerCardAction(player, cardAction);
        }
    }

    public void showUseCattleTokensCardAction(Player player) {
        if (isCurrentPlayer(player)) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN);
            cardAction.setDeck(Card.DECK_PROLETARIAT);
            cardAction.setCardName("Use Cattle Tokens");
            cardAction.setButtonValue("Done");
            cardAction.setStartNumber(0);
            cardAction.setEndNumber(player.getCattleTokens());
            cardAction.setInstructions("Click the number of Cattle Tokens you want to use.");
            setPlayerCardAction(player, cardAction);
        }
    }

    public boolean isRecentGame() {
        return recentGame;
    }

    public void setRecentGame(boolean recentGame) {
        this.recentGame = recentGame;
    }

    public boolean isRecommendedSet() {
        return recommendedSet;
    }

    public void setRecommendedSet(boolean recommendedSet) {
        this.recommendedSet = recommendedSet;
    }

    public boolean isRandomizerReplacementCardNotFound() {
        return randomizerReplacementCardNotFound;
    }

    public void setRandomizerReplacementCardNotFound(boolean randomizerReplacementCardNotFound) {
        this.randomizerReplacementCardNotFound = randomizerReplacementCardNotFound;
    }

    public int getFruitTokensPlayed() {
        return fruitTokensPlayed;
    }

    public void addFruitTokensPlayed(int fruitTokensPlayed) {
        this.fruitTokensPlayed += fruitTokensPlayed;
    }
}
