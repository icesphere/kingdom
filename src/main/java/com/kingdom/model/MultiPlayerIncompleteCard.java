package com.kingdom.model;

import com.kingdom.util.cardaction.NextActionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultiPlayerIncompleteCard extends IncompleteCard {

    private Map<Integer, Boolean> completedPlayers = Collections.synchronizedMap(new HashMap<Integer, Boolean>());
    private boolean currentPlayerHasAction;

    public MultiPlayerIncompleteCard(String cardName, Game game, boolean currentPlayerHasAction) {
        super(cardName, game);
        this.currentPlayerHasAction = currentPlayerHasAction;
        for (Player player : game.getPlayerMap().values()) {
            boolean markCompleted = false;
            if (!currentPlayerHasAction && player.getUserId() == game.getCurrentPlayerId()) {
                markCompleted = true;
            }
            completedPlayers.put(player.getUserId(), markCompleted);
        }
    }

    public MultiPlayerIncompleteCard(String cardName, Game game, int userId) {
        super(cardName, game);
        this.currentPlayerHasAction = false;
        for (Player player : game.getPlayerMap().values()) {
            boolean markCompleted = true;
            if (player.getUserId() == userId) {
                markCompleted = false;
            }
            completedPlayers.put(player.getUserId(), markCompleted);
        }
    }

    public boolean isCompleted() {
        boolean allCompleted = true;
        synchronized (completedPlayers) {
            for (Boolean completed : completedPlayers.values()) {
                if (!completed) {
                    allCompleted = false;
                    break;
                }
            }
        }
        return allCompleted;
    }

    @Override
    public void setPlayerActionCompleted(int playerId) {
        completedPlayers.put(playerId, true);
    }

    @Override
    public void allActionsSet() {
        allActionsSet = true;
    }

    @Override
    public void setWaitingDialogs() {
        if (!game.getCurrentPlayer().isShowCardAction() && !game.getPlayersWithCardActions().isEmpty()) {
            game.setPlayerCardAction(game.getCurrentPlayer(), CardAction.getWaitingForPlayersCardAction());
            return;
        }
        if (isCompleted()) {
            int loopIterations = 0;
            while (game.hasNextAction() && game.hasIncompleteCard() && game.getIncompleteCard().isCompleted()) {
                closeWaitingDialogs();
                game.removeIncompleteCard();
                NextActionHandler.handleAction(game, cardName);
                loopIterations++;
                if (loopIterations > 5) {
                    GameError error = new GameError(GameError.GAME_ERROR, "setWaitingDialogs-nextAction in never ending loop. Next action: "+game.getNextAction());
                    game.logError(error, false);
                    game.removeNextAction();
                    game.removeIncompleteCard();
                    break;
                }
            }
            if (!game.hasIncompleteCard() || game.getIncompleteCard().isCompleted()) {
                if (game.getPlayersWithCardActions().isEmpty()) {
                    closeWaitingDialogs();
                }
                if (!game.getCurrentPlayer().isShowCardAction()) {
                    game.removeIncompleteCard();
                }
            }
        }
        else if(allActionsSet) {
            if (currentPlayerHasAction) {
                for (Player player : game.getPlayers()) {
                    if (!player.isShowCardAction()) {
                        game.setPlayerCardAction(player, CardAction.getWaitingForPlayersCardAction());
                    }
                }
            }
            else {
                if (!game.getCurrentPlayer().isShowCardAction()) {
                    game.setPlayerCardAction(game.getCurrentPlayer(), CardAction.getWaitingForPlayersCardAction());
                }
            }
        }
    }

    private void closeWaitingDialogs() {
        for (Player player : game.getPlayers()) {
            if (player.isShowCardAction() && player.getCardAction().isWaitingForPlayers()) {
                game.closeCardActionDialog(player);
                game.closeLoadingDialog(player);
            }
        }
    }

    public boolean isAllActionsSet() {
        return allActionsSet;
    }

    public void setAllActionsSet(boolean allActionsSet) {
        this.allActionsSet = allActionsSet;
    }
}
