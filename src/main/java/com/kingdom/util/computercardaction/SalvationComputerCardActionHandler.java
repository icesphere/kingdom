package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class SalvationComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();

        if (cardName.equals("Alms")) {
            //todo don't use this card if only have good treasure cards in hand
            Card cardToTrash = computer.getLowestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToTrash.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Archbishop")) {
            //todo better logic to determine best choice
            String choice;
            if (player.getActions() == 0) {
                choice = "actions";
            } else {
                choice = "sins";
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Assassin")) {
            //todo determine best attack card to trash
            Card attackToTrash = computer.getLowestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(attackToTrash.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Baptistry")) {
            //todo determine best card
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(Card.COPPER_ID);
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Bell Tower")) {
            //todo determine best choice
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, "after", -1);
        } else if (cardName.equals("Catacombs")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Edict")) {
            //todo need better logic to determine best choice
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Graverobber")) {
            if (cardAction.getType() == CardAction.TYPE_CHOOSE_CARDS) {
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, new ArrayList<Integer>(), null, null, -1);
            } else {
                String yesNoAnswer;
                if (cardAction.getCards().get(0).getCost() > 0 || computer.wantsCoppers()) {
                    yesNoAnswer = "yes";
                } else {
                    yesNoAnswer = "no";
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
            }
        } else if (cardName.equals("Mendicant")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Scriptorium")) {
            //todo determine best action to discard
            Card cardToDiscard = computer.getLowestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToDiscard.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
    }
}
