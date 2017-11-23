package com.kingdom.model;
import com.kingdom.util.KingdomUtil;

import java.util.*;

@SuppressWarnings({"UnusedDeclaration"})
public class Player implements Comparable{
    private int userId;
    private List<Card> deck = new LinkedList<Card>();
    private List<Card> hand = new ArrayList<Card>();
    private LinkedList<Card> discard = new LinkedList<Card>();
    private List<Card> durationCards = new ArrayList<Card>(0);
    private List<Card> havenCards = new ArrayList<Card>(0);
    private List<Card> islandCards = new ArrayList<Card>(0);
    private List<Card> nativeVillageCards = new ArrayList<Card>(0);
    private List<Card> treasureCards = new ArrayList<Card>(0);
    private List<Card> actionCards = new ArrayList<Card>(0);
    private List<Card> victoryCards = new ArrayList<Card>(0);
    private List<Card> museumCards = new ArrayList<Card>(0);
    private List<Card> cityPlannerCards = new ArrayList<Card>(0);
    private int pirateShipCoins;
    private int coins;
    private int coinsInHand;
    private int actions;
    private int buys;
    private int moatCardsInHand;
    private boolean hasLighthouse;
    private int secretChamberCardsInHand;
    private int victoryCardsInHand;
    private int copperCardsInHand;
    private int curseCardsInHand;
    private int copperSmithsPlayed;
    private int estates;
    private int duchies;
    private int provinces;
    private int colonies;
    private int curses;
    private int gardens;
    private int vineyards;
    private int silkRoads;
    private int cathedrals;
    private int sinsRemoved;
    private int cursesRemoved;
    private int fairgrounds;
    private int greatHalls;
    private int harems;
    private int dukes;
    private int nobles;
    private int archbishops;
    private int islands;
    private int enchantedPalaces;
    private int hedgeWizards;
    private int goldenTouches;
    private String username = "";
    private boolean showCardAction;
    private CardAction cardAction;
    private boolean showInfoDialog;
    private InfoDialog infoDialog;
    private String chatColor;
    private boolean hasBoughtCard;
    private int turns;
    private boolean winner;
    private int marginOfVictory;
    private int numCards;
    private int numActions;
    private int numVictoryCards;
    private int numTreasureCards;
    private int numDifferentCards;
    private boolean tacticianBonus;
    private int potions;
    private int potionsInHand;
    private boolean playTreasureCards;
    private String gender;
    private int victoryCoins;
    private int watchtowerCardsInHand;
    private boolean computer;
    private boolean quit;
    private int diamondMines;
    private int sins;
    private List<Card> edictCards = new ArrayList<Card>(0);
    private List<Card> lastTurnEdictCards = new ArrayList<Card>(0);
    private int provinceCardsInHand;
    private int baneCardsInHand;
    private int baneCardId;
    private int horseTradersInHand;
    private int bellTowersInHand;
    private int enchantedPalacesInHand;
    private List<Card> setAsideCards = new ArrayList<Card>(0);
    private boolean playedCopper;
    private int autoPlayCoins;
    private boolean finalPointsCalculated = false;
    private int finalVictoryPoints = 0;
    private List<Card> finalCards = null;
    private boolean playedTinker;
    private List<Card> tinkerCards = new ArrayList<Card>(0);
    private Queue<CardAction> extraCardActions = new LinkedList<CardAction>();
    private boolean usingLeaders;
    private List<Card> leaders = new ArrayList<Card>(3);
    private int pointsFromLeaders;
    private int numCopper;
    private int numSilver;
    private int numGold;
    private int traderCardsInHand;
    private boolean foolsGoldPlayed;
    private int foolsGoldInHand;
    private int fruitTokens;
    private int cattleTokens;

    private boolean enableVictoryCardDiscount;
    private boolean enableActionCardDiscount;
    private boolean enableTreasureCardDiscount;

    private boolean varroActivated;
    private int varroPoints;

    private int victoryCardDiscountTurns = 0;
    private int actionCardDiscountTurns = 0;
    private int treasureCardDiscountTurns = 0;
    private int buyBonusTurns = 0;
    private int cardBonusTurns = 0;

    private int leaderDiscount;

    private boolean mobile;

    public Player(User user, Game game) {
        actions = 1;
        buys = 1;
        playTreasureCards = game.isPlayTreasureCards();
        baneCardId = game.getBaneCardId();
        usingLeaders = game.isUsingLeaders();
        userId = user.getUserId();
        gender =  user.getGender();
        username = user.getUsername();
        mobile = user.isMobile();
        if (game.isIdenticalStartingHands() && game.getPlayers().size() > 0) {
            Player firstPlayer = game.getPlayers().get(0);
            deck.addAll(firstPlayer.getDeck());
            for (Card card : firstPlayer.getHand()) {
                addCardToHand(card);
            }
        }
        else {
            for(int i=0; i<7; i++){
                deck.add(game.getCopperCard());
            }
            for(int i=0; i<3; i++){
                deck.add(game.getEstateCard());
            }
            Collections.shuffle(deck);
            for(int i=0; i<5; i++){
                drawCardAndAddToHand();
            }
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }
    public int getTurns() {
        return turns;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getGroupedHand() {
        KingdomUtil.groupCards(hand);
        return hand;
    }

    public LinkedList<Card> getDiscard() {
        return discard;
    }

    public void addCardToDiscard(Card card){
        discard.add(card);
    }

    public String getGender() {
        return gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoins() {
        if (playTreasureCards) {
            return coins;
        }
        else {
            return coins + coinsInHand;
        }
    }

    public void addCoins(int coins){
        this.coins += coins;
    }

    public void subtractCoins(int coins) {
        this.coins -= coins;
    }

    public int getCoinsInHand() {
        return coinsInHand;
    }

    public List<Card> getTreasureCards() {
        return treasureCards;
    }

    public List<Card> getActionCards() {
        return actionCards;
    }

    public int getActions() {
        return actions;
    }
    
    public void addActions(int actions){
        this.actions += actions;
    }

    public int getBuys() {
        return buys;
    }

    public void addBuys(int buys){
        this.buys += buys;
    }

    public boolean hasMoat() {
        return moatCardsInHand > 0;
    }

    public boolean hasProvinceInHand() {
        return provinceCardsInHand > 0;
    }

    public boolean hasBaneCardInHand() {
        return baneCardsInHand > 0;
    }

    public boolean hasHorseTradersInHand() {
        return horseTradersInHand > 0;
    }

    public boolean hasBellTowerInHand() {
        return bellTowersInHand > 0;
    }

    public boolean hasEnchantedPalaceInHand() {
        return enchantedPalacesInHand > 0;
    }

    public int getEnchantedPalacesInHand() {
        return enchantedPalacesInHand;
    }

    public boolean hasLighthouse() {
        return hasLighthouse;
    }

    public void setHasLighthouse(boolean hasLighthouse){
        this.hasLighthouse = hasLighthouse;
    }

    public boolean hasSecretChamber() {
        return secretChamberCardsInHand > 0;
    }

    public List<Card> getVictoryCards() {
        return victoryCards;
    }

    public boolean hasVictoryCard() {
        return victoryCardsInHand > 0;
    }

    public Card removeTopDeckCard() {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null;
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck();
        }
        return deck.remove(0);
    }

    public void addCardToTopOfDeck(Card card) {
        deck.add(0, card);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void shuffleDiscardIntoDeck(){
        deck.addAll(discard);
        Collections.shuffle(deck);
        discard.clear();
    }

    public Card lookAtTopDeckCard() {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null;
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck();
        }
        return deck.get(0);
    }

    public List<Card> lookAtTopDeckCards(int numCards) {
        List<Card> cards = new ArrayList<Card>();
        while (cards.size() < numCards) {
            Card card = removeTopDeckCard();
            if (card == null) {
                break;
            }
            else {
                cards.add(card);
            }
        }
        if (!cards.isEmpty()) {
            deck.addAll(0, cards);
        }
        return cards;
    }

    public Card lookAtBottomDeckCard() {
        if (deck.isEmpty() && discard.isEmpty()) {
            return null;
        }
        if (deck.isEmpty()) {
            shuffleDiscardIntoDeck();
        }
        return deck.get(deck.size()-1);
    }

    public void addCardToHand(Card card){
        hand.add(card);
        if(card.isTreasure()) {
            treasureCards.add(card);
            coinsInHand += card.getAddCoins();
            if (card.getCardId() == Card.COPPER_ID) {
                coinsInHand += copperSmithsPlayed;
                copperCardsInHand++;
            }
            if (card.isAutoPlayTreasure()) {
                autoPlayCoins += card.getAddCoins();
            }
        }
        if (card.isAction()) {
            actionCards.add(card);
        }
        if (card.getCardId() == Card.CURSE_ID) {
            curseCardsInHand++;
        }
        if (card.isVictory()) {
            victoryCardsInHand++;
            victoryCards.add(card);
        }
        if(card.getName().equals("Moat")){
            moatCardsInHand++;
        }
        else if (card.getName().equals("Secret Chamber")) {
            secretChamberCardsInHand++;
        }
        else if (card.getName().equals("Watchtower")) {
            watchtowerCardsInHand++;
        }
        else if (card.getName().equals("Province")) {
            provinceCardsInHand++;
        }
        else if(card.getCardId() == baneCardId) {
            baneCardsInHand++;
        }
        else if (card.getName().equals("Horse Traders")) {
            horseTradersInHand++;
        }
        else if (card.getName().equals("Bell Tower")) {
            bellTowersInHand++;
        }
        else if (card.getName().equals("Enchanted Palace")) {
            enchantedPalacesInHand++;
        }
        else if (card.getName().equals("Fool's Gold")) {
            foolsGoldInHand++;
        }
        else if (card.getName().equals("Trader")) {
            traderCardsInHand++;
        }
        if(card.getCardId() == Card.POTION_ID){
            potionsInHand++;
        }
    }

    private void cardRemoved(Card card){
        if (card.isTreasure()) {
            treasureCards.remove(card);
            if (!card.getName().equals("Philosopher's Stone") && !card.getName().equals("Bank")) {
                coinsInHand -= card.getAddCoins();
                if (card.isAutoPlayTreasure()) {
                    autoPlayCoins -= card.getAddCoins();
                }
            }
            if (card.getCardId() == Card.COPPER_ID) {
                coinsInHand -= copperSmithsPlayed;
                copperCardsInHand--;
            }
        }
        if (card.isAction()) {
            actionCards.remove(card);
        }
        if (card.getCardId() == Card.CURSE_ID) {
            curseCardsInHand--;
        }
        if (card.isVictory()) {
            victoryCardsInHand--;
            victoryCards.remove(card);
        }
        if(card.getName().equals("Moat")){
            moatCardsInHand--;
        }
        else if (card.getName().equals("Secret Chamber")) {
            secretChamberCardsInHand--;
        }
        else if (card.getName().equals("Watchtower")) {
            watchtowerCardsInHand--;
        }
        else if (card.getName().equals("Province")) {
            provinceCardsInHand--;
        }
        else if(card.getCardId() == baneCardId) {
            baneCardsInHand--;
        }
        else if (card.getName().equals("Horse Traders")) {
            horseTradersInHand--;
        }
        else if (card.getName().equals("Bell Tower")) {
            bellTowersInHand--;
        }
        else if (card.getName().equals("Enchanted Palace")) {
            enchantedPalacesInHand--;
        }
        else if (card.getName().equals("Fool's Gold")) {
            foolsGoldInHand--;
        }
        else if (card.getName().equals("Trader")) {
            traderCardsInHand--;
        }
        if (card.isPotion()) {
            potionsInHand--;
        }
    }

    public void removeCardFromHand(Card card) {
        cardRemoved(card);
        hand.remove(card);
    }

    public void treasureCardPlayed(Card card, boolean removeFromHand) {
        if (card.getCardId() == Card.COPPER_ID) {
            coins += copperSmithsPlayed;
            playedCopper = true;
        }
        if(removeFromHand) {
            cardRemoved(card);
            hand.remove(card);
        }
    }

    public Card getCardFromHandById(int cardId){
        for (Card card : hand) {
            if(card.getCardId() == cardId){
                return card;
            }
        }
        return null;
    }

    public void discardCardFromHand(Card card) {
        cardRemoved(card);
        discard.add(card);
        hand.remove(card);
    }

    public void discardCardFromHand(int cardId){
        Card card = getCardFromHandById(cardId);
        if(card != null){
            discardCardFromHand(card);
        }
    }

    public void discardHand(){
        for (Card card : hand) {
            cardRemoved(card);
        }
        discard.addAll(hand);
        hand.clear();
    }

    public void drawCardAndAddToHand(){
        Card card = removeTopDeckCard();
        if(card != null){
            addCardToHand(card);
        }
    }

    public void drawCards(int numCards){
        int cardsDrawn = 0;
        while(cardsDrawn < numCards) {
            drawCardAndAddToHand();
            cardsDrawn++;
        }
    }

    public void endTurn(int cardsToDraw) {
        coins = 0;
        coinsInHand = 0;
        autoPlayCoins = 0;
        potions = 0;
        potionsInHand = 0;
        actions = 1;
        buys = 1;
        treasureCards.clear();
        actionCards.clear();
        victoryCards.clear();
        moatCardsInHand = 0;
        provinceCardsInHand = 0;
        baneCardsInHand = 0;
        horseTradersInHand = 0;
        bellTowersInHand = 0;
        enchantedPalacesInHand = 0;
        watchtowerCardsInHand = 0;
        secretChamberCardsInHand = 0;
        victoryCardsInHand = 0;
        copperCardsInHand = 0;
        curseCardsInHand = 0;
        copperSmithsPlayed = 0;
        traderCardsInHand = 0;
        discard.addAll(hand);
        hand.clear();
        hasBoughtCard = false;
        playedCopper = false;
        foolsGoldPlayed = false;
        foolsGoldInHand = 0;

        if (usingLeaders) {
            adjustLeaderBonuses();
        }

        drawCards(cardsToDraw);

        showCardAction = false;
        cardAction = null;
        showInfoDialog = false;
        infoDialog = null;
        hasLighthouse = false;
        if (cardsToDraw ==  5) {
            turns++;
        }
    }

    private void adjustLeaderBonuses() {
        if (victoryCardDiscountTurns > 0) {
            victoryCardDiscountTurns--;
        }
        if (actionCardDiscountTurns > 0) {
            actionCardDiscountTurns--;
        }
        if (treasureCardDiscountTurns > 0) {
            treasureCardDiscountTurns--;
        }
        if (enableVictoryCardDiscount) {
            victoryCardDiscountTurns = 2;
            enableVictoryCardDiscount = false;
        }
        if (enableActionCardDiscount) {
            actionCardDiscountTurns = 2;
            enableActionCardDiscount = false;
        }
        if (enableTreasureCardDiscount) {
            treasureCardDiscountTurns = 2;
            enableTreasureCardDiscount = false;
        }
    }

    public int getVictoryPoints() {
        return getVictoryPoints(false);
    }

    public int getFinalVictoryPoints() {
        return getVictoryPoints(true);
    }

    public int getVictoryPoints(boolean gameOver) {
        if (finalPointsCalculated) {
            return finalVictoryPoints;
        }
        Card curseCard = null;
        int victoryPoints = 0;
        gardens = 0;
        vineyards = 0;
        silkRoads = 0;
        cathedrals = 0;
        sinsRemoved = 0;
        cursesRemoved = 0;
        fairgrounds = 0;
        dukes = 0;
        greatHalls = 0;
        harems = 0;
        nobles = 0;
        archbishops = 0;
        islands = 0;
        estates = 0;
        duchies = 0;
        provinces = 0;
        colonies = 0;
        curses = 0;
        numActions = 0;
        numVictoryCards = 0;
        numTreasureCards = 0;
        enchantedPalaces = 0;
        hedgeWizards = 0;
        goldenTouches = 0;
        pointsFromLeaders = 0;
        numCopper = 0;
        numSilver = 0;
        numGold = 0;
        List<Card> allCards = getAllCards();
        numCards = allCards.size();
        Set<String> cardNames = new HashSet<String>();
        for (Card card : allCards) {
            cardNames.add(card.getName());
            if (card.isVictory() || card.isCurse()) {
                victoryPoints += card.getVictoryPoints();
                if (card.getName().equals("Gardens")) {
                    gardens++;
                }
                else if (card.getName().equals("Vineyard")) {
                    vineyards++;
                }
                else if (card.getName().equals("Silk Road")) {
                    silkRoads++;
                }
                else if (card.getName().equals("Fairgrounds")) {
                    fairgrounds++;
                }
                else if (card.getName().equals("Duke")) {
                    dukes++;
                }
                else if (card.getName().equals("Great Hall")) {
                    greatHalls++;
                }
                else if (card.getName().equals("Harem")) {
                    harems++;
                }
                else if (card.getName().equals("Nobles")) {
                    nobles++;
                }
                else if (card.getName().equals("Archbishop")) {
                    archbishops++;
                }
                else if (card.getName().equals("Island")) {
                    islands++;
                }
                else if (card.getName().equals("Cathedral")) {
                    cathedrals++;
                }
                else if (card.getName().equals("Enchanted Palace")) {
                    enchantedPalaces++;
                }
                else if (card.getName().equals("Hedge Wizard")) {
                    hedgeWizards++;
                }
                else if (card.getName().equals("Golden Touch")) {
                    goldenTouches++;
                }
                else if (card.getCardId() == Card.ESTATE_ID) {
                    estates++;
                }
                else if (card.getCardId() == Card.DUCHY_ID) {
                    duchies++;
                }
                else if (card.getCardId() == Card.PROVINCE_ID) {
                    provinces++;
                }
                else if (card.getCardId() == Card.COLONY_ID) {
                    colonies++;
                }
                else if (card.getCardId() == Card.CURSE_ID) {
                    curses++;
                    if (curseCard == null) {
                        curseCard = card;
                    }
                }
            }
            if (card.isAction()) {
                numActions++;
            }
            if (card.isVictory()) {
                numVictoryCards++;
            }
            if (card.isTreasure()) {
                numTreasureCards++;
                if (card.isCopper()) {
                    numCopper++;
                }
                else if (card.isSilver()) {
                    numSilver++;
                }
                else if (card.isGold()) {
                    numGold++;
                }
            }
        }
        if (vineyards > 0) {
            victoryPoints += vineyards * (Math.floor(numActions / 3));
        }
        if (silkRoads > 0) {
            victoryPoints += silkRoads * (Math.floor(numVictoryCards / 4));
        }
        if (dukes > 0) {
            victoryPoints += dukes * duchies;
        }
        if (cathedrals > 0) {
            int cathedralsUsed = 0;
            if (sins > 0) {
                if (cathedrals > sins) {
                    cathedralsUsed = sins;
                    sinsRemoved = sins;
                }
                else {
                    cathedralsUsed = cathedrals;
                    sinsRemoved = cathedrals;
                }
                if (gameOver) {
                    sins -= sinsRemoved;
                }
                else {
                    victoryPoints += sinsRemoved;
                }
            }
            while (cathedralsUsed < cathedrals && curses > 0) {
                cathedralsUsed++;
                cursesRemoved++;
                numCards--;
                victoryPoints++;
                curses--;
                if (gameOver) {
                    allCards.remove(curseCard);
                }
            }
        }
        if (sins > 0) {
            victoryPoints -= sins;
        }
        if (gardens > 0) {
            victoryPoints += gardens * (Math.floor(numCards / 10));
        }
        numDifferentCards = cardNames.size();
        if (fairgrounds > 0) {
            victoryPoints += fairgrounds * 2 * (Math.floor(numDifferentCards / 5));
        }
        victoryPoints += victoryCoins;

        if (usingLeaders) {
            for (Card leader : leaders) {
                if (leader.isActivated()) {
                    pointsFromLeaders += getLeaderPoints(leader);
                }
            }
            victoryPoints += pointsFromLeaders;
        }

        if (gameOver) {
            finalPointsCalculated = true;
            finalVictoryPoints = victoryPoints;
        }

        finalCards = allCards;

        return victoryPoints;
    }

    private int getLeaderPoints(Card leader) {
        int points = leader.getVictoryPoints();
        if (leader.getName().equals("Hatshepsut")) {
            if (numGold == 0) {
                points += 6;
            }
        }
        else if (leader.getName().equals("Hypatia")) {
            points += Math.floor(numVictoryCards / 2);
        }
        else if (leader.getName().equals("Justinian")) {
            int least = numActions;
            if (numVictoryCards < least) {
                least = numVictoryCards;
            }
            if (numTreasureCards < least) {
                least = numTreasureCards;
            }
            points += least;
        }
        else if (leader.getName().equals("Midas")) {
            points += Math.floor(numTreasureCards / 4);
        }
        else if (leader.getName().equals("Pericles")) {
            points += Math.floor(numActions / 2);
        }
        else if (leader.getName().equals("Solomon")) {
            int solomonPoints = (int) Math.floor(100 / numCards);
            if (solomonPoints > 10) {
                solomonPoints = 10;
            }
            points += solomonPoints;
        }
        else if (leader.getName().equals("Varro")) {
            points += varroPoints;
        }
        return points;
    }

    public int getEstates() {
        return estates;
    }

    public int getDuchies() {
        return duchies;
    }

    public int getProvinces() {
        return provinces;
    }

    public int getColonies() {
        return colonies;
    }

    public int getCurses() {
        return curses;
    }

    public int getGardens() {
        return gardens;
    }

    public int getVineyards() {
        return vineyards;
    }

    public int getSilkRoads() {
        return silkRoads;
    }

    public int getCathedrals() {
        return cathedrals;
    }

    public int getSinsRemoved() {
        return sinsRemoved;
    }

    public int getCursesRemoved() {
        return cursesRemoved;
    }

    public int getFairgrounds() {
        return fairgrounds;
    }

    public int getGreatHalls() {
        return greatHalls;
    }

    public int getHarems() {
        return harems;
    }

    public int getDukes() {
        return dukes;
    }

    public int getNobles() {
        return nobles;
    }

    public int getArchbishops() {
        return archbishops;
    }

    public int getIslands() {
        return islands;
    }

    public int getPotions() {
        if (playTreasureCards) {
            return potions;
        }
        else {
            return potions + potionsInHand;
        }
    }

    public void addPotions(int potions) {
        this.potions += potions;
    }

    public int getPotionsInHand() {
        return potionsInHand;
    }

    public int getCurseCardsInHand() {
        return curseCardsInHand;
    }

    public int getPirateShipCoins() {
        return pirateShipCoins;
    }

    public void addPirateShipCoin(){
        pirateShipCoins++;
    }

    public int getFruitTokens() {
        return fruitTokens;
    }

    public void addFruitTokens(int tokens) {
        fruitTokens += tokens;
    }

    public int getCattleTokens() {
        return cattleTokens;
    }

    public void addCattleTokens(int tokens) {
        cattleTokens += tokens;
    }

    public boolean isShowCardAction() {
        return showCardAction;
    }

    public void setShowCardAction(boolean showCardAction) {
        this.showCardAction = showCardAction;
    }

    public CardAction getCardAction() {
        return cardAction;
    }

    public void setCardAction(CardAction cardAction) {
        if (showCardAction && cardAction != null && !cardAction.isWaitingForPlayers()) {
            extraCardActions.add(cardAction);
        }
        else {
            this.cardAction = cardAction;
        }
    }

    public void putCardFromHandOnTopOfDeck(Card card) {
        deck.add(0, card);
        cardRemoved(card);
        hand.remove(card);
    }

    public boolean isShowInfoDialog() {
        return showInfoDialog;
    }

    public void setShowInfoDialog(boolean showInfoDialog) {
        this.showInfoDialog = showInfoDialog;
    }

    public InfoDialog getInfoDialog() {
        return infoDialog;
    }

    public void setInfoDialog(InfoDialog infoDialog) {
        this.infoDialog = infoDialog;
    }

    public boolean isInfoDialogSet(){
        return infoDialog != null;
    }

    public String getChatColor() {
        return chatColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    public boolean hasBoughtCard() {
        return hasBoughtCard;
    }

    public void setHasBoughtCard(boolean hasBoughtCard) {
        this.hasBoughtCard = hasBoughtCard;
    }

    public int compareTo(Object o) {
        Player player = (Player)o;
        if (getVictoryPoints() == player.getVictoryPoints()){
            if(this.turns == player.getTurns()){
                return 0;
            } else if (this.turns < player.getTurns()) {
                return -1;
            } else{
                return 1;
            }
        }
        else if (getVictoryPoints() > player.getVictoryPoints()){
            return -1;
        }
        else{
            return 1;
        }
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public int getNumCards(){
        return numCards;
    }

    public int getNumActions() {
        return numActions;
    }

    public int getNumVictoryCards() {
        return numVictoryCards;
    }

    public int getNumTreasureCards() {
        return numTreasureCards;
    }

    public int getNumDifferentCards() {
        return numDifferentCards;
    }

    public void copperSmithPlayed() {
        copperSmithsPlayed++;
        if (!playTreasureCards) {
            addCoins(copperCardsInHand);
        }
    }

    public List<Card> getDurationCards() {
        return durationCards;
    }

    public String getDurationCardsString() {
        return KingdomUtil.groupCards(durationCards, true);
    }

    public boolean hasTacticianBonus() {
        return tacticianBonus;
    }

    public void setTacticianBonus(boolean tacticianBonus) {
        this.tacticianBonus = tacticianBonus;
    }

    public List<Card> getHavenCards() {
        return havenCards;
    }

    public List<Card> getIslandCards() {
        return islandCards;
    }

    public String getIslandCardsString() {
        return KingdomUtil.groupCards(islandCards, true);
    }

    public List<Card> getMuseumCards() {
        return museumCards;
    }

    public String getMuseumCardsString() {
        return KingdomUtil.groupCards(museumCards, true);
    }   

    public List<Card> getCityPlannerCards() {
        return cityPlannerCards;
    }

    public String getCityPlannerCardsString() {
        return KingdomUtil.groupCards(cityPlannerCards, true);
    }

    public List<Card> getNativeVillageCards() {
        return nativeVillageCards;
    }

    public int getPhilosophersStoneCoins() {
        int numCards = deck.size() + discard.size();
        return (int) Math.floor(numCards / 5);
    }

    public String getPronoun() {
        if (gender.equals(User.MALE)) {
            return "his";
        }
        else if (gender.equals(User.FEMALE)) {
            return "her";
        }
        else if (gender.equals(User.COMPUTER)) {
            return "its";
        }
        return "his/her";
    }

    public int getVictoryCoins() {
        return victoryCoins;
    }

    public void addVictoryCoins(int victoryCoins) {
        this.victoryCoins += victoryCoins;
    }

    public void addSins(int sins) {
        this.sins += sins;
        if (this.sins < 0) {
            this.sins = 0;
        }
        else if (this.sins > 20) {
            this.sins = 20;
        }
    }

    public boolean hasWatchtower() {
        return watchtowerCardsInHand > 0;
    }

    public boolean isComputer() {
        return computer;
    }

    public void setComputer(boolean computer) {
        this.computer = computer;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public List<Card> getAllCards() {
        List<Card> allCards = new ArrayList<Card>();
        allCards.addAll(hand);
        allCards.addAll(deck);
        allCards.addAll(discard);
        allCards.addAll(havenCards);
        allCards.addAll(islandCards);
        allCards.addAll(museumCards);
        allCards.addAll(cityPlannerCards);
        allCards.addAll(nativeVillageCards);
        allCards.addAll(durationCards);
        allCards.addAll(tinkerCards);
        return allCards;
    }

    public String getFinalCards() {
        return KingdomUtil.groupCards(finalCards, true);
    }

    public int getSins() {
        return sins;
    }

    public void setSins(int sins) {
        this.sins = sins;
    }

    public List<Card> getEdictCards() {
        return edictCards;
    }

    public List<Card> getLastTurnEdictCards() {
        return lastTurnEdictCards;
    }

    public int getMarginOfVictory() {
        return marginOfVictory;
    }

    public void setMarginOfVictory(int marginOfVictory) {
        this.marginOfVictory = marginOfVictory;
    }

    public String getCurrentHand() {
        return KingdomUtil.groupCards(hand, true);
    }

    public void setAsideCardFromHand(Card card) {
        removeCardFromHand(card);
        setAsideCards.add(card);
    }

    public List<Card> getSetAsideCards() {
        return setAsideCards;
    }

    public boolean isPlayedCopper() {
        return playedCopper;
    }

    public int getAutoPlayCoins() {
        return autoPlayCoins;
    }

    public boolean isPlayedTinker() {
        return playedTinker;
    }

    public void setPlayedTinker(boolean playedTinker) {
        this.playedTinker = playedTinker;
    }

    public List<Card> getTinkerCards() {
        return tinkerCards;
    }

    public int getEnchantedPalaces() {
        return enchantedPalaces;
    }

    public int getHedgeWizards() {
        return hedgeWizards;
    }

    public int getGoldenTouches() {
        return goldenTouches;
    }

    public Queue<CardAction> getExtraCardActions() {
        return extraCardActions;
    }

    public boolean isUsingLeaders() {
        return usingLeaders;
    }

    public List<Card> getLeaders() {
        return leaders;
    }

    public List<Card> getActivatedLeaders() {
        List<Card> activatedLeaders = new ArrayList<Card>();
        for (Card leader : leaders) {
            if (leader.isActivated()) {
                activatedLeaders.add(leader);
            }
        }
        return activatedLeaders;
    }

    public String getActivatedLeaderCardsString() {
        return KingdomUtil.groupCards(getActivatedLeaders(), true, false);
    }

    public int getVictoryCardDiscountTurns() {
        return victoryCardDiscountTurns;
    }

    public void setVictoryCardDiscountTurns(int victoryCardDiscountTurns) {
        this.victoryCardDiscountTurns = victoryCardDiscountTurns;
    }

    public int getActionCardDiscountTurns() {
        return actionCardDiscountTurns;
    }

    public void setActionCardDiscountTurns(int actionCardDiscountTurns) {
        this.actionCardDiscountTurns = actionCardDiscountTurns;
    }

    public int getTreasureCardDiscountTurns() {
        return treasureCardDiscountTurns;
    }

    public void setTreasureCardDiscountTurns(int treasureCardDiscountTurns) {
        this.treasureCardDiscountTurns = treasureCardDiscountTurns;
    }

    public void setEnableVictoryCardDiscount(boolean enableVictoryCardDiscount) {
        this.enableVictoryCardDiscount = enableVictoryCardDiscount;
    }

    public void setEnableActionCardDiscount(boolean enableActionCardDiscount) {
        this.enableActionCardDiscount = enableActionCardDiscount;
    }

    public void setEnableTreasureCardDiscount(boolean enableTreasureCardDiscount) {
        this.enableTreasureCardDiscount = enableTreasureCardDiscount;
    }

    public void setLeaderDiscount(int leaderDiscount) {
        this.leaderDiscount = leaderDiscount;
    }

    public int getBuyBonusTurns() {
        return buyBonusTurns;
    }

    public void setBuyBonusTurns(int buyBonusTurns) {
        this.buyBonusTurns = buyBonusTurns;
    }

    public int getCardBonusTurns() {
        return cardBonusTurns;
    }

    public void setCardBonusTurns(int cardBonusTurns) {
        this.cardBonusTurns = cardBonusTurns;
    }

    public int getCardDiscount(Card card) {
        int discount = 0;
        if (card.isVictory() && victoryCardDiscountTurns > 0) {
            discount += 2;
        }
        if (card.isAction() && actionCardDiscountTurns > 0) {
            discount += 2;
        }
        if (card.isTreasure() && treasureCardDiscountTurns > 0) {
            discount += 2;
        }
        if (card.isLeader() && leaderDiscount > 0) {
            discount += leaderDiscount;
        }
        return discount;
    }

    public int getPointsFromLeaders() {
        return pointsFromLeaders;
    }

    public void leaderActivated(Card leader) {
        if (leader.getName().equals("Varro")) {
            varroActivated = true;
        }
        else if (varroActivated) {
            varroPoints += 2;
        }
    }

    public Card getLeaderCard(int cardId) {
        for (Card leader : leaders) {
            if (leader.getCardId() == cardId) {
                return leader;
            }
        }
        return null;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean hasTrader() {
        return traderCardsInHand > 0;
    }

    public boolean isFoolsGoldPlayed() {
        return foolsGoldPlayed;
    }

    public void setFoolsGoldPlayed(boolean foolsGoldPlayed) {
        this.foolsGoldPlayed = foolsGoldPlayed;
    }

    public boolean hasFoolsGoldInHand() {
        return foolsGoldInHand > 0;
    }

    public int getFoolsGoldInHand() {
        return foolsGoldInHand;
    }
}
