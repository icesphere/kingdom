package com.kingdom.model;

import java.util.ArrayList;
import java.util.List;

public class CardAction {
    public static final int TYPE_WAITING_FOR_PLAYERS = 1;
    public static final int TYPE_DISCARD_FROM_HAND = 2;
    public static final int TYPE_DISCARD_DOWN_TO_FROM_HAND = 3;
    public static final int TYPE_DISCARD_UP_TO_FROM_HAND = 4;
    public static final int TYPE_GAIN_CARDS_FROM_SUPPLY = 5;
    public static final int TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY = 6;
    public static final int TYPE_TRASH_CARDS_FROM_HAND = 7;
    public static final int TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK = 8;
    public static final int TYPE_TRASH_UP_TO_FROM_HAND = 9;
    public static final int TYPE_CHOOSE_CARDS = 10;
    public static final int TYPE_YES_NO = 11;
    public static final int TYPE_GAIN_UP_TO_FROM_SUPPLY = 12;
    public static final int TYPE_INFO = 13;
    public static final int TYPE_CHOICES = 14;
    public static final int TYPE_CHOOSE_IN_ORDER = 15;
    public static final int TYPE_CHOOSE_UP_TO = 16;
    public static final int TYPE_GAIN_CARDS = 17;
    public static final int TYPE_GAIN_CARDS_UP_TO = 18;
    public static final int TYPE_CHOOSE_NUMBER_BETWEEN = 19;
    public static final int TYPE_SETUP_LEADERS = 20;
    public static final int TYPE_DISCARD_AT_LEAST_FROM_HAND = 21;
    public static final int TYPE_DISCARD_UP_TO = 22;
    public static final int TYPE_CHOOSE_EVEN_NUMBER_BETWEEN = 23;

    private int type;
    private List<Card> cards = new ArrayList<Card>();
    private int numCards;
    private String instructions = "";
    private String buttonValue = "";
    private String cardName = "";
    private int cardId;
    private int phase;
    private int playerId;
    private int width = 0;
    private List<CardActionChoice> choices = new ArrayList<CardActionChoice>();
    private boolean hideOnSelect;
    private String destination;
    private int startNumber;
    private int endNumber;
    private String deck;
    private Card associatedCard;
    private String action;
    private boolean gainCardAction;
    private boolean gainCardAfterBuyAction;

    public CardAction(int type) {
        this.type = type;
    }

    public static CardAction getWaitingForPlayersCardAction(){
        CardAction cardAction = new CardAction(TYPE_WAITING_FOR_PLAYERS);
        cardAction.setInstructions("Waiting For Players");
        return cardAction;
    }

    public static CardAction getWaitingForSecretChamberCardAction() {
        CardAction cardAction = new CardAction(TYPE_WAITING_FOR_PLAYERS);
        cardAction.setInstructions("Waiting For Players to use Secret Chambers");
        cardAction.setWidth(300);
        return cardAction;
    }

    public static CardAction getWaitingForHorseTradersCardAction() {
        CardAction cardAction = new CardAction(TYPE_WAITING_FOR_PLAYERS);
        cardAction.setInstructions("Waiting For Players to use Horse Traders");
        cardAction.setWidth(300);
        return cardAction;
    }

    public static CardAction getWaitingForBellTowerCardAction() {
        CardAction cardAction = new CardAction(TYPE_WAITING_FOR_PLAYERS);
        cardAction.setInstructions("Waiting For Players to use Bell Towers");
        cardAction.setWidth(300);
        return cardAction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getNumCards() {
        return numCards;
    }

    public void setNumCards(int numCards) {
        this.numCards = numCards;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getButtonValue() {
        return buttonValue;
    }

    public void setButtonValue(String buttonValue) {
        this.buttonValue = buttonValue;
    }

    public List<CardActionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<CardActionChoice> choices) {
        this.choices = choices;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    
    public int getCardId(){
        return cardId;
    }

    public void setCardId(int cardId){
        this.cardId = cardId;
    }

    public boolean isDiscard(){
        return type == TYPE_DISCARD_DOWN_TO_FROM_HAND || type == TYPE_DISCARD_FROM_HAND || type == TYPE_DISCARD_UP_TO_FROM_HAND || type == TYPE_DISCARD_AT_LEAST_FROM_HAND || type == TYPE_DISCARD_UP_TO;
    }

    public boolean isWaitingForPlayers() {
        return type == TYPE_WAITING_FOR_PLAYERS;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean isSelectExact(){
        return type == TYPE_DISCARD_FROM_HAND || type == TYPE_GAIN_CARDS_FROM_SUPPLY || type == TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY
                || type == TYPE_TRASH_CARDS_FROM_HAND || type == TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK
                || type == TYPE_CHOOSE_CARDS || type == TYPE_CHOOSE_IN_ORDER || type == TYPE_GAIN_CARDS || type == TYPE_SETUP_LEADERS;
    }

    public boolean isSelectUpTo() {
        return type == TYPE_DISCARD_UP_TO_FROM_HAND || type == TYPE_TRASH_UP_TO_FROM_HAND || type == TYPE_GAIN_UP_TO_FROM_SUPPLY || type == TYPE_CHOOSE_UP_TO || type == TYPE_GAIN_CARDS_UP_TO;
    }

    public boolean isSelectAtLeast() {
        return type == TYPE_DISCARD_AT_LEAST_FROM_HAND;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getWidth() {
        if(width > 0){
            return width;
        }
        if (type == TYPE_WAITING_FOR_PLAYERS) {
            return 250;
        }
        else if (type == TYPE_YES_NO) {
            return 500;
        }
        else{
            return 750;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isHideOnSelect() {
        return hideOnSelect;
    }

    public void setHideOnSelect(boolean hideOnSelect) {
        this.hideOnSelect = hideOnSelect;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    public int getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(int endNumber) {
        this.endNumber = endNumber;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public Card getAssociatedCard() {
        return associatedCard;
    }

    public void setAssociatedCard(Card associatedCard) {
        this.associatedCard = associatedCard;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isGainCardAction() {
        return gainCardAction;
    }

    public void setGainCardAction(boolean gainCardAction) {
        this.gainCardAction = gainCardAction;
    }

    public boolean isGainCardAfterBuyAction() {
        return gainCardAfterBuyAction;
    }

    public void setGainCardAfterBuyAction(boolean gainCardAfterBuyAction) {
        this.gainCardAfterBuyAction = gainCardAfterBuyAction;
    }
}
