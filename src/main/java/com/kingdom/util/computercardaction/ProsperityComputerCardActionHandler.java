package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class ProsperityComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Bishop")) {
            List<Integer> cardIds;
            if (computer.getNumCardsWorthTrashing(cardAction.getCards()) > 0) {
                cardIds = computer.getCardsToTrash(cardAction.getCards(), 1);
            } else {
                cardIds = computer.getCardsToDiscard(cardAction.getCards(), 1, false);
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Bishop 2")) {
            List<Integer> cardIds;
            if (computer.getNumCardsWorthTrashing(cardAction.getCards()) > 0) {
                cardIds = computer.getCardsToTrash(cardAction.getCards(), 1);
            } else {
                cardIds = new ArrayList<Integer>();
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Contraband")) {
            //todo better logic for determining card
            List<Integer> cardIds = new ArrayList<Integer>();
            Card card = computer.getHighestCostCard(cardAction.getCards());
            cardIds.add(card.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Counting House")) {
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, null, cardAction.getEndNumber());
        } else if (cardName.equals("Expand")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                Card cardToTrash = computer.getLowestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                if (cardToTrash != null) {
                    cardIds.add(cardToTrash.getCardId());
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                if (cardToGain != null) {
                    cardIds.add(cardToGain.getCardId());
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Forge")) {
            //todo need way better logic for this card
            if (type == CardAction.TYPE_TRASH_UP_TO_FROM_HAND) {
                int numToTrash = 3;
                if (cardAction.getNumCards() < 3) {
                    numToTrash = cardAction.getNumCards();
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), numToTrash), null, null, -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                if (cardToGain != null) {
                    cardIds.add(cardToGain.getCardId());
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Goons")) {
            int numCardsToDiscard = player.getHand().size() - 3;
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
        } else if (cardName.equals("King's Court")) {
            Card cardToPlay = computer.getActionToDuplicate(cardAction.getCards(), 3);
            if (cardToPlay == null) {
                cardToPlay = cardAction.getCards().get(0);
            }
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToPlay.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Loan")) {
            String choice;
            if (cardAction.getCards().get(0).isCopper()) {
                choice = "trash";
            } else {
                choice = "discard";
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Mint")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            if (cardToGain != null) {
                cardIds.add(cardToGain.getCardId());
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Mountebank")) {
            //todo determine when it would be good to get curse and copper
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, "discard", -1);
        } else if (cardName.equals("Rabble")) {
            //todo determine when to reorder
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Royal Seal")) {
            String yesNoAnswer;
            if (computer.isCardToDiscard(cardAction.getCards().get(0))) {
                yesNoAnswer = "no";
            } else {
                yesNoAnswer = "yes";
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
        } else if (cardName.equals("Trade Route")) {
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else if (cardName.equals("Vault")) {
            if (type == CardAction.TYPE_DISCARD_UP_TO_FROM_HAND) {
                List<Integer> cardsToDiscard = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToDiscard(card)) {
                        cardsToDiscard.add(card.getCardId());
                    }
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
            } else {
                String yesNoAnswer;
                int cardsToDiscard = 0;
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToDiscard(card)) {
                        cardsToDiscard++;
                    }
                }
                if (cardsToDiscard >= cardAction.getNumCards()) {
                    yesNoAnswer = "yes";
                } else {
                    yesNoAnswer = "no";
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
            }
        } else if (cardName.equals("Vault2")) {
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), 2), null, null, -1);
        } else if (cardName.equals("Watchtower")) {
            String choice;
            if (computer.isCardToTrash(cardAction.getCards().get(0))) {
                choice = "trash";
            } else if (computer.isCardToDiscard(cardAction.getCards().get(0))) {
                choice = "no_reveal";
            } else {
                choice = "deck";
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else {
            throw new RuntimeException("Prosperity Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
