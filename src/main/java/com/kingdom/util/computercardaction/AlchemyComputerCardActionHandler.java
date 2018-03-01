package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class AlchemyComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();

        if (cardName.equals("Alchemist")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Apothecary")) {
            //todo determine when to reorder
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Apprentice")) {
            //todo better logic for determining which card to trash
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), 1), null, null, -1);
        } else if (cardName.equals("Golem")) {
            //todo determine which action is better to play first   
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardAction.getCards().get(0).getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Herbalist")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                if (card.getCost() > 0) {
                    cardIds.add(card.getCardId());
                }
                if (cardIds.size() == cardAction.getNumCards()) {
                    break;
                }
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Scrying Pool")) {
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
                } else {
                    yesNoAnswer = "yes";
                }
            }
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
        } else if (cardName.equals("Transmute")) {
            //todo better logic for determining which card to trash
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), 1), null, null, -1);
        } else if (cardName.equals("University")) {
            //todo determine which action would be best to get
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else {
            throw new RuntimeException("Alchemy Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
