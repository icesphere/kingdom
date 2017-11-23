package com.kingdom.model;

/**
 * Created by IntelliJ IDEA.
 * Date: Apr 24, 2010
 * Time: 9:39:16 AM
 */
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
