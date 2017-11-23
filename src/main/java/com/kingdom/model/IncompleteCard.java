package com.kingdom.model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: 3/31/11
 * Time: 6:37 AM
 */
public abstract class IncompleteCard {

    protected String cardName;
    protected Game game;
    protected Queue<CardAction> extraCardActions = new LinkedList<CardAction>();
    protected boolean endTurn;
    protected boolean allActionsSet;

    protected IncompleteCard(String cardName, Game game) {
        this.cardName = cardName;
        this.game = game;
        game.setIncompleteCard(this);
    }

    public abstract boolean isCompleted();

    public Queue<CardAction> getExtraCardActions() {
        return extraCardActions;
    }

    public abstract void setWaitingDialogs();

    public String getCardName() {
        return cardName;
    }

    public abstract void setPlayerActionCompleted(int playerId);

    public boolean isEndTurn() {
        return endTurn;
    }

    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public Game getGame() {
        return game;
    }

    public synchronized void actionFinished(Player player) {
        if (!player.isShowCardAction()) {
            setPlayerActionCompleted(player.getUserId());
            if (!extraCardActions.isEmpty()) {
                game.setPlayerCardAction(game.getCurrentPlayer(), extraCardActions.remove());
            }
        }
        setWaitingDialogs();
    }

    public abstract void allActionsSet();
}
