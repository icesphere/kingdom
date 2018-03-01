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

        switch (cardName) {
            case "Artisan":
                if (cardAction.getType() == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY) {
                    chooseHighestCostCard(cardAction, computer, game, player);
                } else {
                    if (player.getActions() == 0 && !player.getActionCards().isEmpty()) {
                        Card card = computer.getHighestCostCard(player.getActionCards());
                        chooseCard(cardAction, game, player, card);
                    } else {
                        chooseLowestCostCard(cardAction, computer, game, player);
                    }
                }
                break;
            case "Bureaucrat": {
                List<Integer> cardIds = computer.getCardsNotNeeded(cardAction.getCards(), 1);
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                break;
            }
            case "Cellar":
                List<Integer> cardsToDiscard = new ArrayList<>();
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToDiscard(card)) {
                        cardsToDiscard.add(card.getCardId());
                    }
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
                break;
            case "Chancellor":
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, "yes", null, -1);
                break;
            case "Chapel":
                List<Integer> cardsToTrash = new ArrayList<>();
                for (Card card : cardAction.getCards()) {
                    if (computer.isCardToTrash(card)) {
                        cardsToTrash.add(card.getCardId());
                    }
                    if (cardsToTrash.size() == 4) {
                        break;
                    }
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1);
                break;
            case "Feast":
                chooseHighestCostCard(cardAction, computer, game, player);
                break;
            case "Library":
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, null, "yes", null, -1);
                break;
            case "Militia":
                int numCardsToDiscard = player.getHand().size() - 3;
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, computer.getCardsToDiscard(cardAction.getCards(), numCardsToDiscard), null, null, -1);
                break;
            case "Mine":
                if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                    chooseLowestCostCard(cardAction, computer, game, player);
                } else {
                    chooseHighestCostCard(cardAction, computer, game, player);
                }
                break;
            case "Remodel":
                if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND) {
                    chooseLowestCostCard(cardAction, computer, game, player);
                } else {
                    chooseHighestCostCard(cardAction, computer, game, player);
                }
                break;
            case "Spy":
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
                break;
            case "Thief": {
                List<Integer> cardIds = new ArrayList<>();
                if (type == CardAction.TYPE_CHOOSE_CARDS) {
                    if (cardAction.getNumCards() >= 1) {
                        Card card1 = cardAction.getCards().get(0);
                        if (cardAction.getCards().size() == 2) {
                            Card card2 = cardAction.getCards().get(1);
                            if (card1.isTreasure() && card2.isTreasure()) {
                                if (card1.getCost() > card2.getCost()) {
                                    cardIds.add(card1.getCardId());
                                } else {
                                    cardIds.add(card2.getCardId());
                                }
                            } else if (card1.isTreasure()) {
                                cardIds.add(card1.getCardId());
                            } else {
                                cardIds.add(card2.getCardId());
                            }
                        } else {
                            cardIds.add(card1.getCardId());
                        }
                    }
                } else {
                    if (cardAction.getNumCards() > 0) {
                        for (Card card : cardAction.getCards()) {
                            if (card.getCost() > 0) {
                                cardIds.add(card.getCardId());
                            }
                        }
                    }
                }
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                break;
            }
            case "Throne Room": {
                Card cardToPlay = computer.getActionToDuplicate(cardAction.getCards(), 2);
                chooseCard(cardAction, game, player, cardToPlay);
                break;
            }
            case "Workshop": {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
                break;
            }
            default:
                throw new RuntimeException("Kingdom Card Action not handled for card: " + cardAction.getCardName() + " and type: " + cardAction.getType());
        }
    }

    private static void chooseLowestCostCard(CardAction cardAction, ComputerPlayer computer, Game game, Player player) {
        Card card = computer.getLowestCostCard(cardAction.getCards());
        chooseCard(cardAction, game, player, card);
    }

    private static void chooseHighestCostCard(CardAction cardAction, ComputerPlayer computer, Game game, Player player) {
        Card card = computer.getHighestCostCard(cardAction.getCards());
        chooseCard(cardAction, game, player, card);
    }

    private static void chooseCard(CardAction cardAction, Game game, Player player, Card card) {
        if (card == null) {
            card = cardAction.getCards().get(0);
        }
        List<Integer> cardIds = new ArrayList<>();
        cardIds.add(card.getCardId());
        CardActionHandler.INSTANCE.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
    }
}
