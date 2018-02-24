package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class PromoComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();
        if (cardName.equals("Black Market")) {
            if (type == CardAction.TYPE_YES_NO) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, "no", null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_CARDS) {
                List<Integer> cardIds = new ArrayList<Integer>();
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Envoy")) {
            //todo determine which card would be best to get
            Card cardToDiscard = computer.getHighestCostCard(cardAction.getCards(), false);
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToDiscard.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Governor")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (computer.getNumCardsWorthTrashing(player.getHand()) > 0) {
                    choice = "trash";
                } else if (computer.getGoldsBought() == 0) {
                    choice = "money";
                } else {
                    choice = "cards";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else if (type == CardAction.TYPE_TRASH_UP_TO_FROM_HAND) {
                List<Integer> cardIds;
                if (computer.getNumCardsWorthTrashing(cardAction.getCards()) > 0) {
                    cardIds = computer.getCardsToTrash(cardAction.getCards(), 1);
                } else {
                    cardIds = new ArrayList<Integer>();
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Walled Village")) {
            if (type == CardAction.TYPE_YES_NO) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
            } else {
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else {
            throw new RuntimeException("Promo Card Action not handled for card: " + cardAction.getCardName() + " and type: " + type);
        }
    }
}
