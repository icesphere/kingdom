package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

public class HinterlandsComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Border Village")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Cartographer")) {
            if (type == CardAction.TYPE_DISCARD_UP_TO) {
                List<Integer> cardsToDiscard = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToDiscard(card)) {
                        cardsToDiscard.add(card.getCardId());
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
                //todo determine when to reorder
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Develop")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            } else if (type == CardAction.TYPE_CHOICES) {
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, "more", -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Duchess")) {
            String choice;
            if (computer.isCardToDiscard(cardAction.getAssociatedCard())) {
                choice = "discard";
            } else {
                choice = "back";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
        } else if (cardName.equals("Embassy")) {
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else if (cardName.equals("Ill-Gotten Gains")) {
            String yesNoAnswer;
            if (computer.getPlayer().getCoins() == 5 || computer.getPlayer().getCoins() == 7) {
                yesNoAnswer = "yes";
            } else {
                yesNoAnswer = "no";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNoAnswer, null, -1);
        } else if (cardName.equals("Farmland")) {
            if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Fool's Gold")) {
            String yesNo = "yes";
            if (player.getFoolsGoldInHand() > 1) {
                yesNo = "no";
            }
            CardActionHandler.handleSubmittedCardAction(game, player, null, yesNo, null, -1);
        } else if (cardName.equals("Haggler")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Inn")) {
            if (type == CardAction.TYPE_DISCARD_FROM_HAND) {
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_UP_TO) {
                //todo don't add too many terminal actions
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Jack of all Trades")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (computer.isCardToDiscard(cardAction.getAssociatedCard())) {
                    choice = "discard";
                } else {
                    choice = "back";
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
            }
        } else if (cardName.equals("Mandarin")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (type == CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK) {
                cardIds.add(computer.getCardToPutOnTopOfDeck(cardAction.getCards()).getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
                //todo determine when to reorder
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Margrave")) {
            int numCardsToDiscard = player.getHand().size() - 3;
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
        } else if (cardName.equals("Noble Brigand")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "gold", -1);
        } else if (cardName.equals("Oasis")) {
            CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), cardAction.getNumCards()), null, null, -1);
        } else if (cardName.equals("Oracle")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (cardAction.getCards().size() == 1) {
                    if (computer.isCardToDiscard(cardAction.getCards().get(0))) {
                        choice = "back";
                    } else {
                        choice = "discard";
                    }
                } else {
                    Card firstCard = cardAction.getCards().get(0);
                    Card secondCard = cardAction.getCards().get(1);
                    if (computer.isCardToDiscard(firstCard) && computer.isCardToDiscard(secondCard)) {
                        choice = "back";
                    } else if (!computer.isCardToDiscard(firstCard) && !computer.isCardToDiscard(secondCard)) {
                        choice = "discard";
                    } else {
                        if ((firstCard.isTreasure() || firstCard.isAction()) && firstCard.getCost() >= 5) {
                            choice = "discard";
                        } else if ((secondCard.isTreasure() || secondCard.isAction()) && secondCard.getCost() >= 5) {
                            choice = "discard";
                        } else {
                            choice = "back";
                        }
                    }
                }
                if (cardAction.getPlayerId() == player.getUserId()) {
                    if (choice.equals("discard")) {
                        choice = "back";
                    } else {
                        choice = "discard";
                    }
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
                //todo determine when to reorder
                List<Integer> cardIds = new ArrayList<Integer>();
                for (Card card : cardAction.getCards()) {
                    cardIds.add(card.getCardId());
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Scheme")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                if (card.getCost() > 4 || !card.isTerminalAction()) {
                    cardIds.add(card.getCardId());
                }
                if (cardIds.size() == cardAction.getNumCards()) {
                    break;
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Spice Merchant")) {
            if (type == CardAction.TYPE_TRASH_UP_TO_FROM_HAND) {
                //todo determine when it is worth it to trash treasure cards other than Copper
                List<Integer> cardIds = new ArrayList<Integer>();
                if (computer.getPlayer().getTreasureCards().contains(game.getCopperCard())) {
                    cardIds.add(Card.COPPER_ID);
                }
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            } else if (type == CardAction.TYPE_CHOICES) {
                //todo better logic for which choice is best
                String choice;
                if (player.getCoins() >= 4 && player.getCoins() < 6 && computer.getGoldsBought() < 2) {
                    choice = "money";
                } else {
                    choice = "cards";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            }
        } else if (cardName.equals("Stables")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            if (computer.getPlayer().getTreasureCards().contains(game.getCopperCard())) {
                cardIds.add(Card.COPPER_ID);
            } else if (computer.getPlayer().getTreasureCards().contains(game.getSilverCard())) {
                cardIds.add(Card.SILVER_ID);
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Trader")) {
            if (type == CardAction.TYPE_CHOICES) {
                String choice;
                if (cardAction.getCards().get(0).getCost() < 3) {
                    choice = "silver";
                } else {
                    choice = "no_reveal";
                }
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, choice, -1);
            } else if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                //todo better logic for determining which card to trash
                CardActionHandler.handleSubmittedCardAction(game, player, computer.getCardsToTrash(cardAction.getCards(), 1), null, null, -1);
            }
        } else if (cardName.equals("Tunnel")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        } else {
            throw new RuntimeException("Hinterlands Card Action not handled for card: " + cardAction.getCardName() + " and type: " + type);
        }

    }
}
