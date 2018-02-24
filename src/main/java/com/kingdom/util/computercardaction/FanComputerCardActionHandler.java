package com.kingdom.util.computercardaction;

import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class FanComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();

        if (cardName.equals("Archivist")) {
            //todo determine when other choice would be better
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "draw", -1);
        } else if (cardName.equals("Museum")) {
            //todo add logic for when to use museum
            List<Integer> cardIds = new ArrayList<Integer>();
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
    }
}
