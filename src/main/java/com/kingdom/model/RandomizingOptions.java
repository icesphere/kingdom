package com.kingdom.model;

import java.util.ArrayList;
import java.util.List;

public class RandomizingOptions {
    private boolean oneOfEachCost;
    private boolean oneWithBuy;
    private boolean oneWithActions;
    private boolean defenseForAttack;
    private boolean threeToFiveAlchemy;
    private List<Card> customSelection = new ArrayList<Card>(0);
    private List<Card> excludedCards = new ArrayList<Card>(0);
    private boolean swappingCard;
    private int cardToReplaceIndex;
    private Card cardToReplace;
    private String cardTypeToReplaceWith;

    public boolean isOneOfEachCost() {
        return oneOfEachCost;
    }

    public void setOneOfEachCost(boolean oneOfEachCost) {
        this.oneOfEachCost = oneOfEachCost;
    }

    public boolean isOneWithBuy() {
        return oneWithBuy;
    }

    public void setOneWithBuy(boolean oneWithBuy) {
        this.oneWithBuy = oneWithBuy;
    }

    public boolean isOneWithActions() {
        return oneWithActions;
    }

    public void setOneWithActions(boolean oneWithActions) {
        this.oneWithActions = oneWithActions;
    }

    public boolean isDefenseForAttack() {
        return defenseForAttack;
    }

    public void setDefenseForAttack(boolean defenseForAttack) {
        this.defenseForAttack = defenseForAttack;
    }

    public boolean isThreeToFiveAlchemy() {
        return threeToFiveAlchemy;
    }

    public void setThreeToFiveAlchemy(boolean threeToFiveAlchemy) {
        this.threeToFiveAlchemy = threeToFiveAlchemy;
    }

    public List<Card> getCustomSelection() {
        return customSelection;
    }

    public void setCustomSelection(List<Card> customSelection) {
        this.customSelection = customSelection;
    }

    public List<Card> getExcludedCards() {
        return excludedCards;
    }

    public void setExcludedCards(List<Card> excludedCards) {
        this.excludedCards = excludedCards;
    }

    public boolean isSwappingCard() {
        return swappingCard;
    }

    public void setSwappingCard(boolean swappingCard) {
        this.swappingCard = swappingCard;
    }

    public int getCardToReplaceIndex() {
        return cardToReplaceIndex;
    }

    public void setCardToReplaceIndex(int cardToReplaceIndex) {
        this.cardToReplaceIndex = cardToReplaceIndex;
    }

    public Card getCardToReplace() {
        return cardToReplace;
    }

    public void setCardToReplace(Card cardToReplace) {
        this.cardToReplace = cardToReplace;
    }

    public String getCardTypeToReplaceWith() {
        return cardTypeToReplaceWith;
    }

    public void setCardTypeToReplaceWith(String cardTypeToReplaceWith) {
        this.cardTypeToReplaceWith = cardTypeToReplaceWith;
    }
}
