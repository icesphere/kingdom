package com.kingdom.model;

public class RepeatedAction {
    private Card card;
    private boolean firstAction;

    public RepeatedAction(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public boolean isFirstAction() {
        return firstAction;
    }

    public void setFirstAction(boolean firstAction) {
        this.firstAction = firstAction;
    }
}
