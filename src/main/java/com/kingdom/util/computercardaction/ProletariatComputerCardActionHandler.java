package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProletariatComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {
        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Cattle Farm")) {
            String choice;
            if (computer.isCardToDiscard(cardAction.getAssociatedCard())) {
                choice = "discard";
            } else {
                choice = "back";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("City Planner")) {
            if (type == CardAction.TYPE_CHOOSE_CARDS) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_YES_NO) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
            } else if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            }
        } else if (cardName.equals("Fruit Merchant")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            int numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(cardAction.getCards());
            if (numCardsWorthDiscarding > 2) {
                numCardsWorthDiscarding = 2;
            }
            if (numCardsWorthDiscarding > 0) {
                cardIds.addAll(computer.getCardsToDiscard(cardAction.getCards(), numCardsWorthDiscarding));
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Hooligans")) {
            if (type == CardAction.TYPE_CHOOSE_CARDS) {
                List<Integer> cardIds = new ArrayList<Integer>();
                Card cardToPutOnTopOfDeck = computer.getCardToPutOnTopOfDeck(cardAction.getCards());
                cardIds.add(cardToPutOnTopOfDeck.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (computer.isCardToDiscard(cardAction.getAssociatedCard())) {
                    choice = "deck";
                } else {
                    choice = "discard";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            }
        } else if (cardName.equals("Orchard")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        } else if (cardName.equals("Rancher")) {
            if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_UP_TO) {
                List<Integer> cardIds = new ArrayList<Integer>();
                Collections.shuffle(cardAction.getCards());
                cardIds.add(cardAction.getCards().get(0).getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOICES) {
                //todo choice should usually be "cattle" once the computer knows how to use cattle tokens
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, "buy", -1);
            }
        } else if (cardName.equals("Squatter")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        } else if (cardName.equals("Shepherd")) {
            String yesNo = "yes";
            if (computer.getGoldsBought() == 0) {
                yesNo = "no";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNo, null, -1);
        } else if (cardName.equals("Trainee")) {
            if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_CARDS) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), 1), null, null, -1);
            }
        } else {
            throw new RuntimeException("Proletariat Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
