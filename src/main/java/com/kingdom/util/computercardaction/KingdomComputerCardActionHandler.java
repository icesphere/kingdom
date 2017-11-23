package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class KingdomComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Bureaucrat")) {
            List<Integer> cardIds = computer.getCardsNotNeeded(cardAction.getCards(), 1);
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else if (cardName.equals("Cellar")) {
            List<Integer> cardsToDiscard = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                if (computer.isCardToDiscard(card)) {
                    cardsToDiscard.add(card.getCardId());
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
        }
        else if (cardName.equals("Chancellor")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        }
        else if (cardName.equals("Chapel")) {
            List<Integer> cardsToTrash = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                if (computer.isCardToTrash(card)) {
                    cardsToTrash.add(card.getCardId());
                }
                if (cardsToTrash.size() == 4) {
                    break;
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1);
        }
        else if (cardName.equals("Feast")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            if (cardToGain == null) {
                cardToGain = cardAction.getCards().get(0);
            }
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else if (cardName.equals("Library")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        }
        else if (cardName.equals("Militia")) {
            int numCardsToDiscard = player.getHand().size() - 3;
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
        }
        else if (cardName.equals("Mine")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                Card cardToTrash = computer.getLowestCostCard(cardAction.getCards());
                if (cardToTrash != null) {
                    List<Integer> cardIds = new ArrayList<Integer>();
                    cardIds.add(cardToTrash.getCardId());
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                }
            }
            else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                if (cardToGain != null) {
                    List<Integer> cardIds = new ArrayList<Integer>();
                    cardIds.add(cardToGain.getCardId());
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                }
            }
        }
        else if (cardName.equals("Remodel")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                Card cardToTrash = computer.getLowestCostCard(cardAction.getCards());
                if (cardToTrash != null) {
                    List<Integer> cardIds = new ArrayList<Integer>();
                    cardIds.add(cardToTrash.getCardId());
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                }
            }
            else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                if (cardToGain != null) {
                    List<Integer> cardIds = new ArrayList<Integer>();
                    cardIds.add(cardToGain.getCardId());
                    CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                }
            }
        }
        else if (cardName.equals("Spy")) {
            String yesNoAnswer = "yes";
            Card topCard = cardAction.getCards().get(0);
            if (topCard.getCardId() == Card.CURSE_ID || topCard.getCardId() == Card.COPPER_ID) {
                yesNoAnswer = "no";
            }
            for (Card card : player.getVictoryCards()) {
                if (!card.isTreasure() && !card.isAction()) {
                    yesNoAnswer = "no";
                }
            }
            if (cardAction.getPlayerId() == player.getUserId()) {
                if (yesNoAnswer.equals("yes")) {
                    yesNoAnswer = "no";
                }
                else {
                    yesNoAnswer = "yes";
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
        }
        else if (cardName.equals("Thief")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (type == CardAction.TYPE_CHOOSE_CARDS) {
                if (cardAction.getNumCards() >= 1) {
                    Card card1 = cardAction.getCards().get(0);
                    if (cardAction.getCards().size() == 2) {
                        Card card2 = cardAction.getCards().get(1);
                        if (card1.isTreasure() && card2.isTreasure()) {
                            if (card1.getCost() > card2.getCost()) {
                                cardIds.add(card1.getCardId());
                            }
                            else {
                                cardIds.add(card2.getCardId());
                            }
                        }
                        else if (card1.isTreasure()) {
                            cardIds.add(card1.getCardId());
                        }
                        else {
                            cardIds.add(card2.getCardId());
                        }
                    }
                    else {
                        cardIds.add(card1.getCardId());
                    }
                }
            }
            else {
                if (cardAction.getNumCards() > 0) {
                    for (Card card : cardAction.getCards()) {
                        if (card.getCost() > 0) {
                            cardIds.add(card.getCardId());
                        }
                    }
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else if (cardName.equals("Throne Room")) {
            Card cardToPlay = computer.getActionToDuplicate(cardAction.getCards(), 2);
            if (cardToPlay == null) {
                cardToPlay = cardAction.getCards().get(0);
            }
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToPlay.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else if (cardName.equals("Workshop")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        }
        else {
            throw new RuntimeException("Kingdom Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
