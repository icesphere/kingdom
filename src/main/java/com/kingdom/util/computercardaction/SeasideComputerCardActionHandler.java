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
 * Date: Aug 19, 2010
 * Time: 7:58:21 PM
 */
public class SeasideComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Ambassador")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (type == CardAction.TYPE_CHOOSE_CARDS) {
                cardIds.add(computer.getCardToPass(cardAction.getCards()));
            } else {
                //todo decide when not to add cards back into supply
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Embargo")) {
            //todo better algorithm for deciding which card to embargo
            List<Integer> cardIds = new ArrayList<Integer>();
            Card card = computer.getRandomHighestCostCardFromCostMap(5, false);
            if (card != null) {
                cardIds.add(card.getCardId());
            } else {
                cardIds.add(cardAction.getCards().get(0).getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Explorer")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "gold", -1);
        } else if (cardName.equals("Ghost Ship")) {
            if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
                //todo determine when to reorder
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else {
                int numCardsNotNeeded = player.getHand().size() - 3;
                if (numCardsNotNeeded > 0) {
                    CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(cardAction.getCards(), numCardsNotNeeded), null, null, -1);
                }
            }
        } else if (cardName.equals("Haven")) {
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsNotNeeded(cardAction.getCards(), 1), null, null, -1);
        } else if (cardName.equals("Island")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            Card islandCard = null;
            for (Card card : cardAction.getCards()) {
                if (card.isVictoryOnly()) {
                    islandCard = card;
                    break;
                }
            }
            if (islandCard == null) {
                islandCard = computer.getLowestCostCard(cardAction.getCards());
            }
            cardIds.add(islandCard.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Lookout")) {
            //todo need better way to determine cards
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else if (cardName.equals("Native Village")) {
            //todo analyze cards to determine best choice
            String choice;
            if (player.getNativeVillageCards().size() > 2) {
                choice = "hand";
            } else {
                choice = "card";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Navigator")) {
            if (type == CardAction.TYPE_CHOICES) {
                //todo better analysis of cards to determine best choice
                String choice;
                if (player.getCoinsInHand() < 3 || player.getVictoryCards().size() > 2) {
                    choice = "discard";
                } else {
                    choice = "deck";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else {
                //todo determine when to reorder
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Pearl Diver")) {
            String yesNoAnswer;
            if (computer.isCardToDiscard(cardAction.getCards().get(0))) {
                yesNoAnswer = "no";
            } else {
                yesNoAnswer = "yes";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
        } else if (cardName.equals("Pirate Ship")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (player.getPirateShipCoins() > 2) {
                    choice = "coins";
                } else {
                    choice = "attack";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else {
                List<Integer> cardIds = new ArrayList<Integer>();
                if (cardAction.getNumCards() == 1) {
                    if (cardAction.getCards().get(0).isTreasure() && cardAction.getCards().get(1).isTreasure()) {
                        Card cardToTrash = computer.getHighestCostCard(cardAction.getCards());
                        if (cardToTrash != null) {
                            cardIds.add(cardToTrash.getCardId());
                        }
                    } else if (cardAction.getCards().get(0).isTreasure()) {
                        cardIds.add(cardAction.getCards().get(0).getCardId());
                    } else {
                        cardIds.add(cardAction.getCards().get(1).getCardId());
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Salvager")) {
            //todo better logic for determining which card to trash
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), 1), null, null, -1);
        } else if (cardName.equals("Smugglers")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (cardAction.getNumCards() > 0) {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                cardIds.add(cardToGain.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Treasury")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Warehouse")) {
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else {
            throw new RuntimeException("Seaside Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }
}
