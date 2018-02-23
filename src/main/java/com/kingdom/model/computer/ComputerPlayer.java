package com.kingdom.model.computer;

import com.kingdom.model.*;
import com.kingdom.util.CardCostComparator;
import com.kingdom.util.KingdomUtil;
import com.kingdom.util.computercardaction.*;

import java.util.*;

public abstract class ComputerPlayer {
    protected boolean stopped;

    protected Player player;
    protected Game game;
    protected boolean playAction = true;
    protected int potionsBought = 0;

    protected boolean trashingStrategy = false;
    protected boolean bigMoneyStrategy = false;
    protected boolean bigActionsStrategy = false;
    protected boolean victoryCoinsStrategy = false;

    protected boolean gardensStrategy = false;
    protected boolean chapelStrategy = false;
    protected boolean dukeStrategy = false;
    protected boolean cityStrategy = false;
    protected boolean pirateShipStrategy = false;
    protected boolean ambassadorStrategy = false;
    protected boolean laboratoryStrategy = false;
    protected boolean haremStrategy = false;
    protected boolean miningVillageStrategy = false;

    protected Map<String, Card> kingdomCardMap;

    protected int duchiesBought = 0;
    protected int dukesBought = 0;
    protected int kingsCourtsBought = 0;
    protected int throneRoomsBought = 0;
    protected Map<Integer, Integer> cardsGained = new HashMap<Integer, Integer>();
    protected boolean playAllTreasureCards = true;
    protected int actionsBought = 0;
    protected int treasureCardsBought = 0;
    protected int silversBought = 0;
    protected int goldsBought = 0;
    protected int platinumsBought = 0;
    protected int terminalActionsBought = 0;
    protected boolean checkPeddler = false;
    protected int difficulty = 3;
    protected boolean bigMoneyUltimate = false;
    protected boolean hasCountingHouse = false;
    protected boolean hasGoons = false;
    protected boolean hasGardens = false;
    protected boolean hasDuke = false;
    protected boolean hasVineyard = false;
    protected Card firstCard;
    protected Card secondCard;
    protected Card trashingCard;
    protected boolean fiveTwoSplit;
    protected int startingHandCoppers;
    protected boolean hasExtraBuys = false;
    protected boolean hasTrashingCard = false;
    protected boolean hasExtraActionsCard = false;
    protected boolean hasVictoryCoinsCard = false;
    protected boolean hasDefenseCard = false;
    protected int numAttackCards = 0;
    protected List<Card> trashingCards = new ArrayList<Card>();
    private int buyCardAttempts = 0;
    private boolean error;

    public ComputerPlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        kingdomCardMap = game.getKingdomCardMap();
        startingHandCoppers = player.getTreasureCards().size();
        fiveTwoSplit = (startingHandCoppers == 2 || startingHandCoppers == 5);

        analyzeKingdomCards(game);

        setupStartingStrategies();
    }

    private void analyzeKingdomCards(Game game) {
        for (Card card : game.getKingdomCards()) {
            if (card.getAddBuys() > 0 || card.getName().equals("Workshop") || card.getName().equals("Ironworks")) {
                hasExtraBuys = true;
            }
            if (card.getAddActions() >= 2) {
                hasExtraActionsCard = true;
            }
            if (card.isTrashingCard() && !card.isCostIncludesPotion() && !card.getName().equals("Remake") && !card.getName().equals("Forge")) {
                hasTrashingCard = true;
                trashingCards.add(card);
            }
            if (card.isVictoryCoinsCard()) {
                hasVictoryCoinsCard = true;
            }
            if (card.isDefense()) {
                hasDefenseCard = true;
            }
            if (card.isAttack()) {
                numAttackCards++;
            }

            if (card.getName().equals("Harem")) {
                haremStrategy = true;
            }
            else if (card.getName().equals("Counting House")) {
                hasCountingHouse = true;
            }
            else if (card.getName().equals("Peddler")) {
                checkPeddler = true;
            }
            else if (card.getName().equals("Goons")) {
                hasGoons = true;
            }
            else if (card.getName().equals("Gardens")) {
                hasGardens = true;
            }
            else if (card.getName().equals("Duke")) {
                hasDuke = true;
            }
            else if (card.getName().equals("Vineyard")) {
                hasVineyard = true;
            }
        }
    }

    protected abstract void setupStartingStrategies();

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public void doNextAction() {
        if (stopped || game.getStatus() != Game.STATUS_GAME_IN_PROGRESS) {
            return;
        }
        try {
            int loopIterations = 0;
            while (player.isShowCardAction() && player.getCardAction().getType() == CardAction.TYPE_WAITING_FOR_PLAYERS) {
                if (stopped || game.getStatus() != Game.STATUS_GAME_IN_PROGRESS) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                    loopIterations++;
                    //if wait is longer than 10 minutes then throw error and continue
                    if (loopIterations > 600) {
                        GameError error = new GameError(GameError.GAME_ERROR, "Computer has been waiting for over 10 minutes for player to finish card action.");
                        game.logError(error, false);
                        break;
                    }
                }
                catch (InterruptedException e) {
                    //
                }
            }
            loopIterations = 0;
            while (!player.isShowCardAction() && game.hasIncompleteCard()) {
                if (stopped || game.getStatus() != Game.STATUS_GAME_IN_PROGRESS) {
                    return;
                }
                try {
                    Thread.sleep(500);
                    loopIterations++;
                    //if wait is longer than 15 minutes then throw error and continue
                    if (loopIterations > 1200) {
                        GameError error = new GameError(GameError.GAME_ERROR, "computer-hasIncompleteCard in never ending loop. Incomplete Card: "+game.getIncompleteCard().getCardName());
                        game.logError(error, false);
                        break;
                    }
                }
                catch (InterruptedException e) {
                    //
                }
            }
            if (error) {
                endTurn();
            }
            if (player.isShowCardAction()) {
                handleCardAction(player.getCardAction());
                doNextAction();
            }
            else if (playAction && player.getActions() > 0 && !player.getActionCards().isEmpty()) {
                playAction();
                doNextAction();
            }
            else if (game.isPlayTreasureCards() && !player.getTreasureCards().isEmpty()) {
                playTreasure();
                doNextAction();
            }
            else if (player.getBuys() > 0 && player.getCoins() >= 0) {
                int coinsBeforeBuy = player.getCoins();
                Card cardBought = buyCard();
                if (cardBought != null) {
                    buyCardAttempts++;
                    game.cardClicked(player, "supply", cardBought);

                    int cardBoughtCost = game.getCardCostBuyPhase(cardBought);

                    int expectedCoins = coinsBeforeBuy - cardBoughtCost;

                    if (player.getCoins() != expectedCoins) {
                        GameError error = new GameError(GameError.COMPUTER_ERROR, "Card bought: " + cardBought.getName() + ". Expected coins: " + expectedCoins + ". Actual coins: " + player.getCoins());
                        game.logError(error, false);
                        endTurn();
                    }
                    else {
                        if(buyCardAttempts > 10) {
                            GameError error = new GameError(GameError.COMPUTER_ERROR, "Computer tried to buy cards more than 10 times. Recent card bought: "+cardBought.getName());
                            game.logError(error, false);
                            endTurn();
                        }
                        else {
                            doNextAction();
                        }
                    }
                }
                else {
                    endTurn();
                }
            }
            else {
                endTurn();
            }
        }
        catch (Throwable t) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, KingdomUtil.getStackTrace(t));
            game.logError(error);
        }
    }

    private void endTurn() {
        playAllTreasureCards = true;
        playAction = true;
        buyCardAttempts = 0;
        error = false;
        game.endPlayerTurn(player);
    }

    public void gainedCard(Card card) {
        Integer numBought = cardsGained.get(card.getCardId());
        if (numBought == null) {
            numBought = 0;
        }
        numBought++;
        if (card.isAction()) {
            actionsBought++;
            if (card.isTerminalAction()) {
                terminalActionsBought++;
                if (card.getName().equals("Throne Room")) {
                    throneRoomsBought++;
                }
                else if (card.getName().equals("King's Court")) {
                    kingsCourtsBought++;
                }
            }
        }

        if (card.isTreasure() && !card.isPotion()) {
            treasureCardsBought++;
            if(card.isSilver()) {
                silversBought++;
            }
            else if(card.isGold()) {
                goldsBought++;
            }
            else if(card.isPlatinum()) {
                platinumsBought++;
            }
        }

        if (dukeStrategy) {
            if (card.getName().equals("Duke")) {
                dukesBought++;
            }
            else if (card.getName().equals("Duchy")) {
                duchiesBought++;
            }
        }
        cardsGained.put(card.getCardId(), numBought);
    }

    public void playAction() {
        Card actionToPlay = null;
        if (difficulty >= 2 && player.getActionCards().size() > 1) {
            for (Card card : player.getActionCards()) {
                if (card.getName().equals("Throne Room") || card.getName().equals("King's Court")) {
                    actionToPlay = card;
                }
            }
            if (actionToPlay != null) {
                int numTimesToPlayAction = 2;
                if (actionToPlay.getName().equals("King's Court")) {
                    numTimesToPlayAction = 3;
                }
                if (getActionToDuplicate(player.getActionCards(), numTimesToPlayAction) == null) {
                    actionToPlay = null;
                }
            }
        }
        if (difficulty >= 2 && actionToPlay == null) {
            for (Card card : player.getActionCards()) {
                if (card.isAction() && card.getAddActions() > 0) {
                    actionToPlay = card;
                    if (!card.getName().equals("Shanty Town") && !card.getName().equals("Apprentice")) {
                        break;
                    }
                }
            }
        }

        if (actionToPlay == null) {
            if (difficulty < 2) {
                actionToPlay = player.getActionCards().get(0);
            }
            else {
                CardCostComparator ccc = new CardCostComparator();
                Collections.sort(player.getActionCards(), Collections.reverseOrder(ccc));
                for (Card card : player.getActionCards()) {
                    if (shouldPlayAction(card)) {
                        actionToPlay = card;
                        break;
                    }
                }
            }
        }

        if (actionToPlay != null) {
            game.cardClicked(player, "hand", actionToPlay);
        }
        else {
            this.playAction = false;
        }
    }

    public boolean shouldPlayAction(Card action) {
        boolean playAction = true;
        if ((action.getName().equals("Apprentice") || action.getName().equals("Chapel") || action.getName().equals("Ambassador") || action.getName().equals("Salvager")) && getNumCardsWorthTrashing(player.getHand()) == 0) {
            playAction = false;
        }
        else if (action.getName().equals("Chapel") && ((goldsBought == 0 && silversBought == 0) || (player.getCoins() >= 5 && player.getTurns() < 6))) {
            playAction = false;
        }
        else if (action.getName().equals("Tactician") && player.getCoins() >= 6) {
            playAction = false;
        }
        else if (action.getName().equals("Trade Route") && getNumCardsWorthTrashing(player.getHand()) == 0 && game.getTradeRouteTokensOnMat() < 3) {
            playAction = false;
        }
        else if (action.getName().equals("Bishop") && getNumCardsWorthTrashing(player.getHand()) == 0 && !onlyBuyVictoryCards()) {
            playAction = false;
        }
        return playAction;
    }

    public Card getActionToDuplicate(List<Card> cards, int numTimesToPlayAction) {
        Card actionToDuplicate = null;
        for (Card card : cards) {
            if (card.getAddActions() > 0 && !card.getName().equals("Apprentice")) {
                actionToDuplicate = card;
                break;
            }
        }
        if (actionToDuplicate == null) {
            for (Card card : cards) {
                if (card.getName().equals("Chapel") || card.getName().equals("Throne Room") || card.getName().equals("King's Court")) {
                    continue;
                }
                if ((card.getName().equals("Ambassador") || card.getName().equals("Apprentice") || card.getName().equals("Trade Route")) && getNumCardsWorthTrashing(cards) < numTimesToPlayAction) {
                    continue;
                }
                actionToDuplicate = card;
                break;
            }
        }
        return actionToDuplicate;
    }

    public void playTreasure() {

        //play Contraband cards first
        if (difficulty >= 2 && game.isTrackContrabandCards()) {
            for (Card card : player.getTreasureCards()) {
                if (card.getName().equals("Contraband")) {
                    game.cardClicked(player, "hand", card);
                    return;
                }
            }
        }

        if (playAllTreasureCards) {
            playAllTreasureCards = false;
            game.playAllTreasureCards(player, false);
        }
        else {
            Card treasureToPlay = null;

            //play Bank cards last
            if (difficulty >= 2 && game.isTrackBankCards()) {
                for (Card card : player.getTreasureCards()) {
                    if (!card.getName().equals("Bank")) {
                        treasureToPlay = card;
                        break;
                    }
                }
            }
            if (treasureToPlay == null) {
                treasureToPlay = player.getTreasureCards().get(0);
            }
            game.cardClicked(player, "hand", treasureToPlay);
        }
    }

    public boolean onlyBuyVictoryCards() {
        boolean onlyBuyVictoryCards = false;
        Integer provincesInSupply = game.getSupply().get(Card.PROVINCE_ID);
        if (provincesInSupply == null) {
            GameError error = new GameError(GameError.COMPUTER_ERROR, "Supply was null for Province");
            game.logError(error);
        }
        if ((game.getNumPlayers() == 2 && game.getSupply().get(Card.PROVINCE_ID) <= 2) || (game.getNumPlayers() > 2 && game.getSupply().get(Card.PROVINCE_ID) <= 3)) {
            onlyBuyVictoryCards = true;
        }
        else if (game.isIncludeColonyCards() && ((game.getNumPlayers() == 2 && game.getSupply().get(Card.COLONY_ID) <= 2) || (game.getNumPlayers() > 2 && game.getSupply().get(Card.COLONY_ID) <= 3))) {
            onlyBuyVictoryCards = true;
        }
        else if(difficulty >= 2) {
            int pilesWithOneCard = 0;
            int pilesWithTwoCards = 0;
            for (Integer numInSupply : game.getSupply().values()) {
                if (numInSupply == 1) {
                    pilesWithOneCard++;
                }
                else if (numInSupply == 2) {
                    pilesWithTwoCards++;
                }
            }
            int numEmptyPilesForGameEnd = 3;
            if (game.getNumPlayers() > 4) {
                numEmptyPilesForGameEnd = 4;
            }
            if (game.getNumEmptyPiles() + pilesWithOneCard + pilesWithTwoCards == numEmptyPilesForGameEnd) {
                onlyBuyVictoryCards = true;
            }
        }
        return onlyBuyVictoryCards;
    }

    public boolean includeVictoryOnlyCards() {
        return player.getTurns() > 8;
    }

    protected Card buyCard() {
        if (player.getCoins() < 2 && !gardensStrategy && !hasGoons) {
            return null;
        }

        if (game.isUsePotions() && player.getCoins() == 4 && potionsBought == 0 && !kingdomCardMap.containsKey("Black Market") && (!game.isShowTrollTokens() || game.numTrollTokens(Card.POTION_ID) == 0)) {
            potionsBought++;
            return game.getCardMap().get(Card.POTION_ID);
        }

        if (chapelStrategy && cardsGained.get(kingdomCardMap.get("Chapel").getCardId()) == null && ((player.getCoins() == 3 && silversBought > 0) || player.getCoins() == 2) && (!game.isTrackContrabandCards() || !game.getContrabandCards().contains(kingdomCardMap.get("Chapel")))) {
            return kingdomCardMap.get("Chapel");
        }
        if (chapelStrategy && player.getCoins() <= 3 && silversBought < 2 && goldsBought == 0 && game.canBuyCard(player, game.getSilverCard())) {
            return game.getSilverCard();
        }
        if (chapelStrategy && laboratoryStrategy && player.getCoins() <= 5 && game.canBuyCard(player, kingdomCardMap.get("Laboratory"))) {
            return kingdomCardMap.get("Laboratory");
        }
        if(player.getCoins() >= game.getCardCostBuyPhase(game.getGoldCard()) && goldsBought == 0 && (!game.isIncludePlatinumCards() || player.getCoins() < game.getCardCostBuyPhase(game.getPlatinumCard())) && game.canBuyCard(player, game.getGoldCard())) {
            return game.getGoldCard();
        }
        if (gardensStrategy) {
            if (player.getTurns() > 4 && game.canBuyCard(player, kingdomCardMap.get("Gardens"))) {
                if (onlyBuyVictoryCards() && player.getCoins() >= game.getCardCostBuyPhase(kingdomCardMap.get("Gardens"))) {
                    return kingdomCardMap.get("Gardens");
                }
                else if (player.getCoins() == game.getCardCostBuyPhase(kingdomCardMap.get("Gardens"))) {
                    return kingdomCardMap.get("Gardens");
                }
            }
        }
        if (dukeStrategy) {
            Integer duchiesInSupply = game.getSupply().get(Card.DUCHY_ID);
            if (duchiesInSupply > 0 && (player.getTurns() > 8 || duchiesInSupply <= 6) && player.getCoins() >= game.getCardCostBuyPhase(game.getDuchyCard())) {
                if (duchiesBought < 4 || duchiesBought <= dukesBought && (!game.isTrackContrabandCards() || !game.getContrabandCards().contains(game.getDuchyCard()))) {
                    return game.getDuchyCard();
                }
                else if (duchiesBought >= 3 && game.canBuyCard(player, kingdomCardMap.get("Duke"))) {
                    return kingdomCardMap.get("Duke");
                }
            }
        }
        if (checkPeddler && !onlyBuyVictoryCards() && game.canBuyCard(player, kingdomCardMap.get("Peddler"))) {
            if (player.getCoins() < 6) {
                return kingdomCardMap.get("Peddler");
            }
        }
        if (cityStrategy && !onlyBuyVictoryCards() && player.getCoins() <= 6 && player.getTurns() > 8 && game.canBuyCard(player, kingdomCardMap.get("City"))) {
            return kingdomCardMap.get("City");
        }
        if (ambassadorStrategy && !onlyBuyVictoryCards() && player.getTurns() < 2) {
            return kingdomCardMap.get("Ambassador");
        }
        if (pirateShipStrategy && !onlyBuyVictoryCards() && player.getCoins() <= 4 && (terminalActionsBought - actionsBought) < 2 && game.canBuyCard(player, kingdomCardMap.get("Pirate Ship"))) {
            return kingdomCardMap.get("Pirate Ship");
        }

        Card cardToBuy = getRandomHighestCostCardFromCostMap(player.getCoins(), false);
        if (player.getPotions() > 0) {
            Card potionCardToBuy = getRandomHighestCostCardFromCostMap(player.getCoins(), true);
            if (potionCardToBuy != null && (cardToBuy == null || potionCardToBuy.getCost() + 3 >= cardToBuy.getCost())) {
                cardToBuy = potionCardToBuy;
            }
        }
        if (cardToBuy != null) {
            return cardToBuy;
        }
        else if (difficulty >= 2 && wantsCoppers()) {
            return game.getCopperCard();
        }
        return null;
    }

    protected Card buyCardHardDifficulty() {
        if (player.getTurns() < 2) {
            if (player.getTurns() == 0 && firstCard != null) {
                return firstCard;
            }
            else if (player.getTurns() == 1 && secondCard != null) {
                return secondCard;
            }
        }

        int losingMargin = game.getLosingMargin(player.getUserId());
        boolean winning = game.currentlyWinning(player.getUserId());

        if (onlyBuyVictoryCards() && (winning || losingMargin < 6)) {
            for (Integer cardId : game.getSupply().keySet()) {
                Card card = game.getSupplyMap().get(cardId);
                if (game.buyingCardWillEndGame(cardId) && game.canBuyCard(player, card)
                        && (winning || card.getVictoryPoints() > losingMargin)) {
                    return card;
                }
            }
        }

        if (game.isIncludeColonyCards() && player.getCoins() >= game.getCardCostBuyPhase(game.getColonyCard()) && game.canBuyCard(player, game.getColonyCard())) {
            if (!game.buyingCardWillEndGame(Card.COLONY_ID) || game.currentlyWinning(player.getUserId()) || losingMargin < 10) {
                return game.getColonyCard();
            }
        }

        if (game.isIncludePlatinumCards() && platinumsBought <= 1 && !onlyBuyVictoryCards() && player.getCoins() >= game.getCardCostBuyPhase(game.getPlatinumCard()) && game.canBuyCard(player, game.getPlatinumCard())) {
            if (!game.buyingCardWillEndGame(Card.PLATINUM_ID) || game.currentlyWinning(player.getUserId())) {
                return game.getPlatinumCard();
            }
        }

        if (gardensStrategy && player.getTurns() > 4 && game.canBuyCard(player, kingdomCardMap.get("Gardens"))) {
            int gardenCost = game.getCardCostBuyPhase(kingdomCardMap.get("Gardens"));
            if (onlyBuyVictoryCards() && player.getCoins() >= gardenCost && (player.getCoins() < game.getCardCostBuyPhase(game.getProvinceCard()) || player.getBuys() > 1)) {
                return kingdomCardMap.get("Gardens");
            }
            else if (player.getCoins() == gardenCost) {
                return kingdomCardMap.get("Gardens");
            }
        }

        if (goldsBought >= 1 && player.getCoins() >= game.getCardCostBuyPhase(game.getProvinceCard()) && game.canBuyCard(player, game.getProvinceCard())) {
            if (!game.buyingCardWillEndGame(Card.PROVINCE_ID) || game.currentlyWinning(player.getUserId()) || losingMargin < 6) {
                return game.getProvinceCard();
            }
        }

        if (haremStrategy && goldsBought >= 2 && player.getCoins() >= game.getCardCostBuyPhase(game.getGoldCard()) && game.canBuyCard(player, kingdomCardMap.get("Harem"))) {
            if (!game.buyingCardWillEndGame(kingdomCardMap.get("Harem").getCardId()) || game.currentlyWinning(player.getUserId()) || losingMargin < 2) {
                return kingdomCardMap.get("Harem");
            }
        }

        if(kingdomCardMap.containsKey("Farmland") && game.getSupply().get(Card.PROVINCE_ID) <= 5 && player.getCoins() >= game.getCardCostBuyPhase(kingdomCardMap.get("Farmland")) && game.canBuyCard(player, kingdomCardMap.get("Farmland"))) {
            if (!game.buyingCardWillEndGame(kingdomCardMap.get("Farmland").getCardId()) || game.currentlyWinning(player.getUserId()) || losingMargin < 2) {
                return kingdomCardMap.get("Farmland");
            }
        }

        if(game.getSupply().get(Card.PROVINCE_ID) <= 5 && player.getCoins() >= game.getCardCostBuyPhase(game.getDuchyCard()) && game.canBuyCard(player, game.getDuchyCard())) {
            if (!game.buyingCardWillEndGame(Card.DUCHY_ID) || game.currentlyWinning(player.getUserId()) || losingMargin < 3) {
                return game.getDuchyCard();
            }
        }

        if(kingdomCardMap.containsKey("Nobles") && game.getSupply().get(Card.PROVINCE_ID) <= 3 && player.getCoins() >= game.getCardCostBuyPhase(kingdomCardMap.get("Nobles")) && game.canBuyCard(player, kingdomCardMap.get("Nobles"))) {
            if (!game.buyingCardWillEndGame(kingdomCardMap.get("Nobles").getCardId()) || game.currentlyWinning(player.getUserId()) || losingMargin < 2) {
                return kingdomCardMap.get("Nobles");
            }
        }

        if(kingdomCardMap.containsKey("Island") && game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= game.getCardCostBuyPhase(kingdomCardMap.get("Island")) && game.canBuyCard(player, kingdomCardMap.get("Island"))) {
            if (!game.buyingCardWillEndGame(kingdomCardMap.get("Island").getCardId()) || game.currentlyWinning(player.getUserId()) || losingMargin < 2) {
                return kingdomCardMap.get("Island");
            }
        }

        if(kingdomCardMap.containsKey("Great Hall") && game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= game.getCardCostBuyPhase(kingdomCardMap.get("Great Hall")) && game.canBuyCard(player, kingdomCardMap.get("Great Hall"))) {
            if (!game.buyingCardWillEndGame(kingdomCardMap.get("Great Hall").getCardId()) || game.currentlyWinning(player.getUserId())) {
                return kingdomCardMap.get("Great Hall");
            }
        }

        if(game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= game.getCardCostBuyPhase(game.getEstateCard()) && game.canBuyCard(player, game.getEstateCard())) {
            if (!game.buyingCardWillEndGame(Card.ESTATE_ID) || game.currentlyWinning(player.getUserId()) || losingMargin < 2) {
                return game.getEstateCard();
            }
        }

        if (bigMoneyStrategy && actionsBought >= 3) {
            return buyCardBigMoneyUltimate();
        }

        if (trashingStrategy && cardsGained.get(trashingCard.getCardId()) == null && player.getCoins() == game.getCardCostBuyPhase(trashingCard) && game.canBuyCard(player, trashingCard)) {
            return trashingCard;
        }

        if (player.getTurns() < 2) {
            if(terminalActionsBought > 0) {
                List<Card> cardsWithActions = new ArrayList<Card>();
                for (Card card : game.getKingdomCards()) {
                    if (card.getAddActions() > 0 && card.getCost() == player.getCoins() && !excludeCardDefault(card) && !card.isCostIncludesPotion()) {
                        cardsWithActions.add(card);
                    }
                }
                if (player.getCoins() > 2) {
                    cardsWithActions.add(game.getSilverCard());
                    cardsWithActions.add(game.getSilverCard());
                }
                if (!cardsWithActions.isEmpty()) {
                    Collections.shuffle(cardsWithActions);
                    return cardsWithActions.get(0);
                }
            }
        }

        return null;
    }

    protected Card buyCardBigMoneyUltimate() {

        if (player.getCoins() >= 11 && game.isIncludeColonyCards() && game.canBuyCard(player, game.getColonyCard())) {
            return game.getColonyCard();
        }

        if (game.isIncludePlatinumCards() && game.isIncludeColonyCards() && player.getTurns() < 10 && platinumsBought == 0 && player.getCoins() >= 9 && game.canBuyCard(player, game.getPlatinumCard())) {
            return game.getPlatinumCard();
        }

        if (player.getCoins() >= 8 && game.canBuyCard(player, game.getProvinceCard())) {
            return game.getProvinceCard();
        }

        if(game.getSupply().get(Card.PROVINCE_ID) <= 5 && player.getCoins() >= 5 && game.canBuyCard(player, game.getDuchyCard())) {
            return game.getDuchyCard();
        }

        if(kingdomCardMap.containsKey("Island") && game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= 4 && game.canBuyCard(player, kingdomCardMap.get("Island"))) {
            return kingdomCardMap.get("Island");
        }

        if(kingdomCardMap.containsKey("Great Hall") && game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= 3 && game.canBuyCard(player, kingdomCardMap.get("Great Hall"))) {
            return kingdomCardMap.get("Great Hall");
        }

        if(game.getSupply().get(Card.PROVINCE_ID) <= 2 && player.getCoins() >= 2 && game.canBuyCard(player, game.getEstateCard())) {
            return game.getEstateCard();
        }

        if (player.getCoins() >= 6 && game.canBuyCard(player, game.getGoldCard())) {
            return game.getGoldCard();
        }

        if (player.getCoins() >= 3 && game.canBuyCard(player, game.getSilverCard())) {
            return game.getSilverCard();
        }

        return null;
    }

    public Card getRandomHighestCostCardFromCostMap(int cost, boolean costIncludesPotion) {
        Card cardToGain = null;
        Map<Integer, List<Card>> costMap;
        if (costIncludesPotion) {
            costMap = game.getPotionCostMap();
        }
        else {
            costMap = game.getCostMap();
        }
        int adjustedCost = cost;
        if (game.getCostDiscount() > 0) {
            adjustedCost += game.getCostDiscount();
        }
        if (game.getActionCardDiscount() > 0) {
            adjustedCost += game.getActionCardDiscount();
        }
        for (int i = adjustedCost; i > 1; i--) {
            List<Card> cards = costMap.get(i);
            if (cards != null) {
                List<Card> availableCards = new ArrayList<Card>();
                for (Card card : cards) {
                    Integer numInSupply = game.getSupply().get(card.getCardId());
                    if (numInSupply == null) {
                        GameError error = new GameError(GameError.COMPUTER_ERROR, "Supply was null for "+card.getName());
                        game.logError(error, false);
                        continue;
                    }
                    if (!game.canBuyCard(player, card)) {
                        continue;
                    }
                    if (onlyBuyVictoryCards()) {
                        if (card.isVictory()) {
                            availableCards.add(card);
                        }
                    }
                    else if (!excludeCardDefault(card)) {
                        availableCards.add(card);
                    }
                }
                if (!availableCards.isEmpty()) {
                    Collections.shuffle(availableCards);
                    cardToGain = availableCards.get(0);
                    break;
                }
            }
        }
        return cardToGain;
    }

    protected boolean excludeCardDefault(Card card) {
        if (card.isCostIncludesPotion() && player.getPotions() == 0) {
            return true;
        }

        if (card.getName().equals("Grand Market")) {
            for (Card treasureCard : game.getTreasureCardsPlayed()) {
                if (treasureCard.getCardId() == Card.COPPER_ID) {
                    return true;
                }
            }
        }

        return excludeCard(card);
    }

    protected abstract boolean excludeCard(Card card);

    protected boolean excludeCardEasy(Card card) {
        if (card.isCurseOnly()) {
            return true;
        }
        else if (card.getName().equals("Chapel")) {
            return true;
        }
        else if (card.getName().equals("Black Market")) {
            return true;
        }
        else if (card.getName().equals("Treasure Map")) {
            return true;
        }
        else if (card.getName().equals("Museum")) {
            return true;
        }
        else if (card.getName().equals("Archivist")) {
            return true;
        }
        return false;
    }

    protected boolean excludeCardMedium(Card card) {
        if (excludeCardEasy(card)) {
            return true;
        }

        Integer embargoTokens = game.getEmbargoTokens().get(card.getCardId());
        if (game.isShowEmbargoTokens() && embargoTokens > 0) {
            if (embargoTokens > 2 || (!card.isProvince() && !card.isColony())) {
                return true;
            }
        }
        if (!card.isVictory() && !card.isTreasure() && card.getCost() < 5 && cardsGained.get(card.getCardId()) != null && cardsGained.get(card.getCardId()) >= 3) {
            return true;
        }
        else if (chapelStrategy && card.getName().equals("Counting House")) {
            return true;
        }
        else if (card.getName().equals("Monk")) {
            return true;
        }
        else if (card.getName().equals("Baptistry") && wantsCoppers()) {
            return true;
        }
        else if (card.isPotion() && potionsBought > 0) {
            return true;
        }
        else if (card.getName().equals("Throne Room") && (player.getTurns() < 3 || throneRoomsBought >= 2)) {
            return true;
        }
        else if (card.getName().equals("King's Court") && kingsCourtsBought >= 2) {
            return true;
        }
        else if (card.isTerminalAction() && ((terminalActionsBought - actionsBought) > 1 || terminalActionsBought == 1 && actionsBought == 1)) {
            return true;
        }
        else if (card.getName().equals("Lookout")) {
            return true;
        }
        else if (card.isVictoryOnly() && !includeVictoryOnlyCards()) {
            return true;
        }
        else if (player.getTurns() <= 5 && card.getName().equals("Great Hall")) {
            return true;
        }
        else if (card.getName().equals("Forge")) {
            return true;
        }
        else if (card.getName().equals("Witch") && player.getTurns() >= 8) {
            return true;
        }
        else if (card.getName().equals("Mint") && player.getTurns() >= 5) {
            return true;
        }
        else if (card.getName().equals("Remake")) {
            return true;
        }
        else if (card.getName().equals("Sorceress")) {
            return true;
        }
        else if (card.getName().equals("Outpost")) {
            return true;
        }
        else if (card.getName().equals("Quest")) {
            return true;
        }
        else if (card.isPotion() && kingdomCardMap.containsKey("Black Market")) {
            return true;
        }
        else if (card.getFruitTokens() > 0 || card.getCattleTokens() > 0 || card.getName().equals("Goodwill")) {
            return true;
        }
        else if (card.getName().equals("Rancher")) {
            return true;
        }
        else if (card.getName().equals("Farmland") && player.getHand().stream().allMatch(Card::isProvince)) {
            return true;
        }

        return false;
    }

    protected boolean excludeCardHard(Card card) {
        if (excludeCardMedium(card)) {
            return true;
        }

        if (game.buyingCardWillEndGame(card.getCardId()) && !game.currentlyWinning(player.getUserId())) {
            return true;
        }
        else if (card.isVictoryOnly()) {
            return true;
        }
        else if ((card.getName().equals("Mine") || card.getName().equals("Thief") || card.getName().equals("Chancellor")
                || card.getName().equals("Wishing Well") || card.getName().equals("Workshop")
                || card.getName().equals("Horn of Plenty") || card.getName().equals("Quarry")
                || card.getName().equals("Trader") || card.getName().equals("Navigator")
                || card.getName().equals("Oracle") || card.getName().equals("Fool's Gold"))) {
            return true;
        }
        else if (card.getName().equals("Workshop") && !gardensStrategy) {
            return true;
        }
        else if (player.getTurns() < 2 && card.getName().equals("Village")) {
            return true;
        }
        else if (bigMoneyStrategy && card.isTerminalAction() && (terminalActionsBought - actionsBought) > 0) {
            return true;
        }
        else if (bigMoneyStrategy && card.isAction() && !card.isVictory() && actionsBought >= 3) {
            return true;
        }
        else if (!bigActionsStrategy && card.isAction() && !card.isVictory() && actionsBought >= 5) {
            return true;
        }
        else if (card.getName().equals("Expand") && cardsGained.get(kingdomCardMap.get("Expand").getCardId()) != null) {
            return true;
        }
        else if (bigMoneyStrategy && card.isExtraActionsCard()) {
            return true;
        }
        else if (!gardensStrategy && card.getName().equals("Talisman")) {
            return true;
        }

        return false;
    }

    public Card getLowestCostCard(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        if (cards.size() == 1) {
            return cards.get(0);
        }
        CardCostComparator ccc = new CardCostComparator();
        Collections.sort(cards, ccc);
        List<Card> lowCards = new ArrayList<Card>();
        int lowestCost = cards.get(0).getCost();
        if (cards.get(0).isCostIncludesPotion()) {
            lowestCost += 2;
        }
        for (Card card : cards) {
            int cost = card.getCost();
            if (card.isCostIncludesPotion()) {
                cost += 2;
            }
            if (cost == lowestCost) {
                if (!excludeCardDefault(card)) {
                    lowCards.add(card);
                }
            }
            else {
                break;
            }
        }
        if (lowCards.isEmpty()) {
            for (Card card : cards) {
                if (!excludeCardDefault(card)) {
                    return card;
                }
            }
            return cards.get(0);
        }
        Collections.shuffle(lowCards);
        return lowCards.get(0);
    }

    public Card getHighestCostCard(List<Card> cards) {
        return getHighestCostCard(cards, true);
    }

    public Card getHighestCostCard(List<Card> cards, boolean includeVictoryOnlyCards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        if (cards.size() == 1) {
            return cards.get(0);
        }
        CardCostComparator ccc = new CardCostComparator();
        Collections.sort(cards, Collections.reverseOrder(ccc));
        List<Card> topCards = new ArrayList<Card>();
        int highestCost = cards.get(0).getCost();
        if (cards.get(0).isCostIncludesPotion()) {
            highestCost += 2;
        }
        if (includeVictoryOnlyCards && onlyBuyVictoryCards()) {
            for (Card card : cards) {
                if (card.isVictory()) {
                    return card;
                }
            }
        }
        for (Card card : cards) {
            int cost = card.getCost();
            if (card.isCostIncludesPotion()) {
                cost += 2;
            }
            if (cost == highestCost) {
                if (!excludeCardDefault(card) && (includeVictoryOnlyCards || !card.isVictoryOnly())) {
                    topCards.add(card);
                }
            }
            else {
                break;
            }
        }
        if (topCards.isEmpty()) {
            for (Card card : cards) {
                if (!excludeCardDefault(card)) {
                    return card;
                }
            }
            return cards.get(0);
        }
        Collections.shuffle(topCards);
        return topCards.get(0);
    }

    //todo create method to get all useless action cards

    public Card getUselessAction(List<Card> cards) {
        Card action = null;
        if (cards != null && !cards.isEmpty()) {
            if (cards.size() == 1) {
                Card card = cards.get(0);
                if (card.getName().equals("Throne Room") || card.getName().equals("King's Court")) {
                    action = card;
                }
                else if (card.getName().equals("Apprentice") && getNumCardsWorthTrashing(cards) == 0) {
                    action = card;
                }
                else if (card.getName().equals("Bishop") && getNumCardsWorthTrashing(cards) == 0 && !onlyBuyVictoryCards()) {
                    action = card;
                }
            }
            else {
                boolean hasCardWithActions = false;
                for (Card card : cards) {
                    if (card.getName().equals("Throne Room") || card.getName().equals("King's Court") || (card.isAction() && card.getAddActions() > 0)) {
                        hasCardWithActions = true;
                        break;
                    }
                }
                if (!hasCardWithActions) {
                    action = cards.get(0);
                }
            }
        }
        return action;
    }

    public Card getCardToPutOnTopOfDeck(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        Card topDeckCard = null;
        for (Card card : cards) {
            if (card.isCurseOnly()) {
                topDeckCard = card;
                break;
            }
        }
        if (topDeckCard == null) {
            for (Card card : cards) {
                if (card.isVictoryOnly()) {
                    topDeckCard = card;
                    break;
                }
            }
        }
        if (topDeckCard == null) {
            for (Card card : cards) {
                if (card.isCopper()) {
                    topDeckCard = card;
                    break;
                }
            }
        }
        if (topDeckCard == null) {
            topDeckCard = getUselessAction(player.getActionCards());
        }
        if (topDeckCard == null) {
            Collections.shuffle(cards);
            topDeckCard = cards.get(0);
        }
        return topDeckCard;
    }

    public Integer getCardToPass(List<Card> cards) {
        return getCardsToTrash(cards, 1).get(0);
    }

    public int getNumCardsWorthDiscarding(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }
        int numCardsWorthDiscarding = 0;
        for (Card card : cards) {
            if (isCardToDiscard(card)) {
                numCardsWorthDiscarding++;
            }
        }

        if (getUselessAction(cards) != null) {
            numCardsWorthDiscarding++;
        }

        return numCardsWorthDiscarding;
    }

    public boolean isCardToDiscard(Card card) {
        boolean shouldDiscard = false;
        if (card.isCurseOnly() || card.isVictoryOnly() || card.isVictoryReaction()) {
            shouldDiscard = true;
        }
        return shouldDiscard;
    }

    public int getNumCardsWorthTrashing(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }
        int numCardsWorthTrashing = 0;
        for (Card card : cards) {
            if (isCardToTrash(card)) {
                numCardsWorthTrashing++;
            }
        }
        return numCardsWorthTrashing;
    }

    public boolean shouldTrashCopper() {
        int totalNumCards = player.getDeck().size() + player.getDiscard().size() + player.getHand().size();
        return !wantsCoppers() && (silversBought > 0 || goldsBought > 0 || treasureCardsBought > 2) && totalNumCards > 5;
    }

    public boolean isCardToTrash(Card card) {
        boolean shouldTrash = false;
        if (card.isCurseOnly() || (card.isCopper() && shouldTrashCopper()) || (card.getCardId() == Card.ESTATE_ID && player.getTurns() <= 10)) {
            shouldTrash = true;
        }
        return shouldTrash;
    }

    public List<Integer> getCardsToDiscard(List<Card> cards, int numCardsToDiscard) {
        return getCardsToDiscard(cards, numCardsToDiscard, true);
    }

    public List<Integer> getCardsToDiscard(List<Card> cards, int numCardsToDiscard, boolean includeVictoryCards) {
        List<Integer> cardsToDiscard = new ArrayList<Integer>();
        //discard Curses first
        for (Card card : cards) {
            if (card.isCurseOnly()) {
                cardsToDiscard.add(Card.CURSE_ID);
                if (cardsToDiscard.size() == numCardsToDiscard) {
                    break;
                }
            }
        }
        //next discard victory cards
        if (includeVictoryCards && cardsToDiscard.size() < numCardsToDiscard) {
            for (Card card : cards) {
                if (card.isVictoryOnly()) {
                    cardsToDiscard.add(card.getCardId());
                }
                if (cardsToDiscard.size() == numCardsToDiscard) {
                    break;
                }
            }
        }
        //next discard coppers
        if (cardsToDiscard.size() < numCardsToDiscard) {
            for (Card card : cards) {
                if (card.isCopper()) {
                    cardsToDiscard.add(Card.COPPER_ID);
                }
                if (cardsToDiscard.size() == numCardsToDiscard) {
                    break;
                }
            }
        }
        //next add useless action
        if (cardsToDiscard.size() < numCardsToDiscard) {
            Card uselessAction = getUselessAction(cards);
            if (uselessAction != null) {
                cardsToDiscard.add(uselessAction.getCardId());
            }
        }
        //next discard lowest cost cards
        if (cardsToDiscard.size() < numCardsToDiscard) {
            List<Card> extraCards = new ArrayList<Card>();
            for (Card card : cards) {
                if (!cardsToDiscard.contains(card.getCardId())) {
                    extraCards.add(card);
                }
            }

            CardCostComparator ccc = new CardCostComparator();
            Collections.sort(extraCards, ccc);
            for (Card card : extraCards) {
                cardsToDiscard.add(card.getCardId());
                if (cardsToDiscard.size() == numCardsToDiscard) {
                    break;
                }
            }
        }
        return cardsToDiscard;
    }

    public List<Integer> getCardsToTrash(List<Card> cards, int numCardsToTrash) {
        List<Integer> cardsToTrash = new ArrayList<Integer>();
        //trash Curses first
        for (Card card : cards) {
            if (card.isCurseOnly()) {
                cardsToTrash.add(Card.CURSE_ID);
                if (cardsToTrash.size() == numCardsToTrash) {
                    break;
                }
            }
        }
        //next trash estates
        if (cardsToTrash.size() < numCardsToTrash) {
            for (Card card : cards) {
                if (card.isEstate() && player.getTurns() < 10) {
                    cardsToTrash.add(card.getCardId());
                }
                if (cardsToTrash.size() == numCardsToTrash) {
                    break;
                }
            }
        }
        //next trash coppers
        if (cardsToTrash.size() < numCardsToTrash) {
            for (Card card : cards) {
                if (card.isCopper()) {
                    cardsToTrash.add(Card.COPPER_ID);
                }
                if (cardsToTrash.size() == numCardsToTrash) {
                    break;
                }
            }
        }
        //next trash lowest cost cards
        if (cardsToTrash.size() < numCardsToTrash) {
            List<Card> extraCards = new ArrayList<Card>();
            for (Card card : cards) {
                if (!cardsToTrash.contains(card.getCardId())) {
                    extraCards.add(card);
                }
            }

            CardCostComparator ccc = new CardCostComparator();
            Collections.sort(extraCards, ccc);
            for (Card card : extraCards) {
                cardsToTrash.add(card.getCardId());
                if (cardsToTrash.size() == numCardsToTrash) {
                    break;
                }
            }
        }
        return cardsToTrash;
    }

    public List<Integer> getCardsNotNeeded(List<Card> cards, int numCardsNotNeeded) {
        List<Integer> cardsNotNeeded = new ArrayList<Integer>();
        List<Card> cardsCopy = new ArrayList<Card>();
        cardsCopy.addAll(cards);
        List<Card> remainingCards = new ArrayList<Card>();
        //add Curses first
        for (Card card : cardsCopy) {
            if (card.isCurseOnly()) {
                cardsNotNeeded.add(Card.CURSE_ID);
                if (cardsNotNeeded.size() == numCardsNotNeeded) {
                    break;
                }
            }
            else {
                remainingCards.add(card);
            }
        }
        //next add victory cards
        if (cardsNotNeeded.size() < numCardsNotNeeded) {
            cardsCopy.clear();
            cardsCopy.addAll(remainingCards);
            remainingCards.clear();
            for (Card card : cardsCopy) {
                if (card.isVictoryOnly()) {
                    cardsNotNeeded.add(card.getCardId());
                    if (cardsNotNeeded.size() == numCardsNotNeeded) {
                        break;
                    }
                }
                else {
                    remainingCards.add(card);
                }
            }
        }
        //next add useless actions
        if (cardsNotNeeded.size() < numCardsNotNeeded) {
            cardsCopy.clear();
            cardsCopy.addAll(remainingCards);
            remainingCards.clear();
            Card uselessAction = getUselessAction(cardsCopy);
            while (uselessAction != null) {
                cardsNotNeeded.add(uselessAction.getCardId());
                if (cardsNotNeeded.size() == numCardsNotNeeded) {
                    break;
                }
                cardsCopy.remove(uselessAction);
                uselessAction = getUselessAction(cardsCopy);
            }
        }
        //next add coppers
        if (cardsNotNeeded.size() < numCardsNotNeeded) {
            for (Card card : cardsCopy) {
                if (card.isCopper()) {
                    cardsNotNeeded.add(Card.COPPER_ID);
                    if (cardsNotNeeded.size() == numCardsNotNeeded) {
                        break;
                    }
                }
                else {
                    remainingCards.add(card);
                }
            }
        }
        //next add lowest cost cards
        if (cardsNotNeeded.size() < numCardsNotNeeded) {
            CardCostComparator ccc = new CardCostComparator();
            Collections.sort(remainingCards, ccc);
            for (Card card : remainingCards) {
                cardsNotNeeded.add(card.getCardId());
                if (cardsNotNeeded.size() == numCardsNotNeeded) {
                    break;
                }
            }
        }
        return cardsNotNeeded;
    }

    public boolean isGardensStrategy() {
        return gardensStrategy;
    }

    public boolean isHasGoons() {
        return hasGoons;
    }

    public boolean isHasCountingHouse() {
        return hasCountingHouse;
    }

    public boolean wantsCoppers() {
        return isHasCountingHouse() || isGardensStrategy() || (isHasGoons() && game.getGoonsCardsPlayed() > 0);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isBigMoneyUltimate() {
        return bigMoneyUltimate;
    }

    public void handleCardAction(CardAction cardAction) {
        if(cardAction.isWaitingForPlayers()) {
            return;
        }
        if (cardAction.getDeck().equals(Card.DECK_KINGDOM)) {
            KingdomComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_INTRIGUE)) {
            IntrigueComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_SEASIDE)) {
            SeasideComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_ALCHEMY)) {
            AlchemyComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_PROSPERITY)) {
            ProsperityComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_CORNUCOPIA)) {
            CornucopiaComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_HINTERLANDS)) {
            HinterlandsComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_PROMO)) {
            PromoComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_SALVATION)) {
            SalvationComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_FAIRYTALE)) {
            FairyTaleComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_LEADERS)) {
            LeaderComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_PROLETARIAT)) {
            ProletariatComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_FAN)) {
            FanComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else if (cardAction.getDeck().equals(Card.DECK_REACTION)) {
            ReactionComputerCardActionHandler.handleCardAction(cardAction, this);
        }
        else {
            throw new RuntimeException("CardAction with card: " + cardAction.getCardName() + " and type: "+cardAction.getType()+" does not have a deck type");
        }
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getGoldsBought() {
        return goldsBought;
    }
}
