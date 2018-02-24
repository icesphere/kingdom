package com.kingdom.model;

import com.kingdom.util.cardaction.NextActionHandler;

public class SinglePlayerIncompleteCard extends IncompleteCard {

    private boolean completed;

    public SinglePlayerIncompleteCard(String cardName, Game game) {
        super(cardName, game);
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setPlayerActionCompleted(int playerId) {
        completed = true;
    }

    @Override
    public void allActionsSet() {
        //not used for single player incomplete card
    }

    @Override
    public void setWaitingDialogs() {
        if (!game.getCurrentPlayer().isShowCardAction() && !game.getPlayersWithCardActions().isEmpty()) {
            game.setPlayerCardAction(game.getCurrentPlayer(), CardAction.getWaitingForPlayersCardAction());
            return;
        }
        if (completed && extraCardActions.isEmpty() && !game.getCurrentPlayer().isShowCardAction()) {
            while (game.hasNextAction()) {
                NextActionHandler.handleAction(game, cardName);
            }
            if (game.hasIncompleteCard() && game.getIncompleteCard().isCompleted()) {
                game.removeIncompleteCard();
            }
        }
    }
}
