package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Aug 20, 2010
 * Time: 7:05:59 AM
 */
public class CornucopiaComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Followers")) {
            int numCardsToDiscard = player.getHand().size() - 3;
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
        }
        else if (cardName.equals("Hamlet")) {
            if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), 1), null, null, -1);
            }
            else {
                //todo better logic on when computer needs +1 action
                String yesNoAnswer;
                int numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.getHand());
                if (numCardsWorthDiscarding > 0) {
                    yesNoAnswer = "yes";
                }
                else {
                    yesNoAnswer = "no";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
            }
        }
        else if (cardName.equals("Hamlet2")) {
            if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), 1), null, null, -1);
            }
            else {
                //todo better logic on when computer needs +1 buy
                String yesNoAnswer;
                int numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.getHand());
                if (numCardsWorthDiscarding > 0) {
                    yesNoAnswer = "yes";
                }
                else {
                    yesNoAnswer = "no";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
            }
        }
        else if (cardName.equals("Horse Traders")) {
            if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            }
            else if(type == CardAction.TYPE_YES_NO) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
            }
        }
        else if (cardName.equals("Horn of Plenty")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else if (cardName.equals("Jester")) {
            String choice = "them";
            if (cardAction.getCards().get(0).getCost() >= 5 || cardAction.getCards().get(0).getAddActions() > 0) {
                choice = "me";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        }
        else if (cardName.equals("Remake")) {
            if(type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            }
            else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        }
        else if (cardName.equals("Tournament")) {
            if (type == CardAction.TYPE_YES_NO) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
            }
            else if(type == CardAction.TYPE_CHOICES) {
                String choice = "prize";
                if (game.getPrizeCards().isEmpty()) {
                    choice = "duchy";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            }
            else if(type == CardAction.TYPE_GAIN_CARDS) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        }
        else if (cardName.equals("Trusty Steed")) {
            //todo determine when other choices would be better
            String choice = "cardsAndCoins";
            if (!player.getActionCards().isEmpty() && player.getActions() == 0) {
                choice = "cardsAndActions";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        }
        else if (cardName.equals("Young Witch")) {
            if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            }
            else if(type == CardAction.TYPE_CHOICES) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, "reveal", -1);
            }
        }
        else {
            throw new RuntimeException("Cornucopia Card Action not handled for card: " + cardAction.getCardName() + " and type: " + type);
        }
    }
}
