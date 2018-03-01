package com.kingdom.util.computercardaction;

import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

public class ReactionComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {
        Game game = computer.getGame();
        Player player = computer.getPlayer();
        String cardName = cardAction.getCardName();

        if (cardName.equals("Choose Reaction")) {
            //todo figure out if one if better than another to play first
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, cardAction.getChoices().get(0).getValue(), -1);
        } else if (cardName.equals("Duchess for Duchy")) {
            //todo determine when it is good to get a Duchess
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, "no", null, -1);
        }
    }
}
