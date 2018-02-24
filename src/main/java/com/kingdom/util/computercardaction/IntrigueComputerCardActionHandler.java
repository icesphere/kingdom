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

public class IntrigueComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Baron")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        } else if (cardName.equals("Courtyard")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(computer.getCardToPutOnTopOfDeck(cardAction.getCards()).getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Ironworks")) {
            //todo determine which card would be best to get
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Masquerade")) {
            if (type == CardAction.TYPE_TRASH_UP_TO_FROM_HAND) {
                List<Integer> cardIds = new ArrayList<Integer>();
                if (computer.getNumCardsWorthTrashing(cardAction.getCards()) > 0) {
                    cardIds.addAll(computer.getCardsToTrash(cardAction.getCards(), 1));
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else {
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(computer.getCardToPass(cardAction.getCards()));
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Mining Village")) {
            String yesNo = "no";
            if (computer.getDifficulty() >= 3 && player.getTurns() < 6 && (player.getCoins() == 3 || player.getCoins() == 4)) {
                yesNo = "yes";
            }
            if (computer.onlyBuyVictoryCards()) {
                yesNo = "yes";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNo, null, -1);
        } else if (cardName.equals("Minion")) {

            String choice = "coins";

            int numCardsWorthDiscarding = computer.getNumCardsWorthDiscarding(player.getHand());
            if (numCardsWorthDiscarding >= 3 && player.getCoins() < 5) {
                choice = "discard";
            }

            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Nobles")) {
            String choice;
            if (player.getActions() == 0) {
                choice = "actions";
            } else {
                choice = "cards";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Pawn")) {
            //todo determine when other choices would be better
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "cardAndAction", -1);
        } else if (cardName.equals("Saboteur")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            if (cardToGain.getCost() > 2) {
                cardIds.add(cardToGain.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Scout")) {
            //todo determine when to reorder
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Secret Chamber")) {
            if (type == CardAction.TYPE_DISCARD_UP_TO_FROM_HAND) {
                List<Integer> cardsToDiscard = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToDiscard(card)) {
                        cardsToDiscard.add(card.getCardId());
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
            } else {
                //todo determine when putting cards on top of deck is a good idea
                CardActionHandler.handleSubmittedCardAction(game, player, null, "no", null, -1);
            }
        } else if (cardName.equals("Steward")) {
            if (type == CardAction.TYPE_CHOICES) {
                int cardsToTrash = computer.getNumCardsWorthTrashing(cardAction.getCards());
                String choice;
                //todo determine when it would be good to trash just one card
                if (!computer.isGardensStrategy() && cardsToTrash >= 2 && (player.getActions() > 0 || player.getCoins() < 3)) {
                    choice = "trash";
                } else if (player.getActions() > 0 && player.getCoins() < 3) {
                    choice = "cards";
                } else {
                    choice = "coins";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            }
        } else if (cardName.equals("Swindler")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (cardAction.getCards().get(0).getCost() == 0 && game.getSupply().get(Card.CURSE_ID) > 0) {
                cardIds.add(Card.CURSE_ID);
            } else {
                Collections.shuffle(cardAction.getCards());
                cardIds.add(cardAction.getCards().get(0).getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Torturer")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                //todo determine other situations where getting a curse would be best
                if (game.getSupply().get(Card.CURSE_ID) == 0) {
                    choice = "curse";
                } else {
                    choice = "discard";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else {
                int numCardsToDiscard = cardAction.getNumCards();
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
            }
        } else if (cardName.equals("Trading Post")) {
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else if (cardName.equals("Upgrade")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Wishing Well")) {
            //todo determine which card is most likely to show up
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(Card.COPPER_ID);
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else {
            throw new RuntimeException("Intrigue Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
