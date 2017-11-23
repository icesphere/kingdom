package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Jul 31, 2010
 * Time: 6:33:06 PM
 */
public class DiscardCardsHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        IncompleteCard incompleteCard = null;

        if (selectedCardIds.isEmpty()) {
            game.addHistory(player.getUsername(), " did not discard a card");
        }
        else {
            game.addHistory(player.getUsername(), " discarded ", KingdomUtil.getPlural(selectedCardIds.size(), "card"));
        }

        for (Integer selectedCardId : selectedCardIds) {
            if (cardAction.getType() == CardAction.TYPE_DISCARD_UP_TO) {
                player.addCardToDiscard(game.getCardMap().get(selectedCardId));
                game.playerDiscardedCard(player, game.getCardMap().get(selectedCardId));
            }
            else {
                player.discardCardFromHand(selectedCardId);
                game.playerDiscardedCard(player, game.getCardMap().get(selectedCardId));
            }
        }
        if (cardAction.getCardName().equals("Cartographer")) {
            List<Card> cards = cardAction.getCards();
            for (Integer selectedCardId : selectedCardIds) {
                Card card = game.getCardMap().get(selectedCardId);
                cards.remove(card);
            }
            if (!cards.isEmpty()) {
                if (cards.size() == 1) {
                    player.addCardToTopOfDeck(cards.get(0));
                }
                else {
                    CardAction chooseOrderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    chooseOrderCardAction.setDeck(Card.DECK_HINTERLANDS);
                    chooseOrderCardAction.setHideOnSelect(true);
                    chooseOrderCardAction.setNumCards(cards.size());
                    chooseOrderCardAction.setCardName(cardAction.getCardName());
                    chooseOrderCardAction.setCards(cards);
                    chooseOrderCardAction.setButtonValue("Done");
                    chooseOrderCardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                    game.setPlayerCardAction(player, chooseOrderCardAction);
                }
            }
        }
        else if (cardAction.getCardName().equals("Cellar")) {
            player.drawCards(selectedCardIds.size());
        }
        else if (cardAction.getCardName().equals("Druid")) {
            player.addCoins(2*selectedCardIds.size());
        }
        else if (cardAction.getCardName().equals("Fruit Merchant")) {
            if (!selectedCardIds.isEmpty()) {
                player.addFruitTokens(selectedCardIds.size());
                game.addHistory(player.getUsername(), " gained ", KingdomUtil.getPlural(selectedCardIds.size(), "fruit token"));
            }
        }
        else if (cardAction.getCardName().equals("Hamlet")) {
            player.addActions(1);
            game.refreshAllPlayersCardsPlayed();
            game.addHistory(player.getUsername(), " gained +1 Action");
            if (player.getHand().isEmpty()) {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand to discard for +1 Buy");
            }
            else {
                incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
                game.addNextAction("discard for buy");
            }
        }
        else if (cardAction.getCardName().equals("Hamlet2")) {
            player.addBuys(1);
            game.refreshAllPlayersCardsBought();
            game.addHistory(player.getUsername(), " gained +1 Buy");
        }
        else if (cardAction.getCardName().equals("Scriptorium")) {
            Card selectedCard = game.getCardMap().get(selectedCardIds.get(0));
            game.playerGainedCard(player, selectedCard);
        }
        else if (cardAction.getCardName().equals("Secret Chamber")) {
            player.addCoins(selectedCardIds.size());
        }
        else if (cardAction.getCardName().equals("Stables")) {
            if (!selectedCardIds.isEmpty()) {
                game.addHistory(player.getUsername(), " gained +3 Cards and +1 Action");
                player.drawCards(3);
                player.addActions(1);
                game.refreshAllPlayersCardsPlayed();
            }
        }
        else if (cardAction.getCardName().equals("Vault")) {
            incompleteCard = new MultiPlayerIncompleteCard(cardAction.getCardName(), game, false);
            player.addCoins(selectedCardIds.size());
            game.addHistory(player.getUsername(), " gained +", KingdomUtil.getPlural(selectedCardIds.size(), "Coin"), " from playing ", KingdomUtil.getWordWithBackgroundColor("Vault", Card.ACTION_COLOR), "");

            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (otherPlayer.getHand().size() >= 1) {
                        CardAction yesNoCardAction = new CardAction(CardAction.TYPE_YES_NO);
                        yesNoCardAction.setDeck(Card.DECK_PROSPERITY);
                        yesNoCardAction.setCardName("Vault");
                        if (otherPlayer.getHand().size() == 1) {
                            yesNoCardAction.setNumCards(1);
                            yesNoCardAction.setInstructions("Do you want to discard the card in your hand?");
                        }
                        else {
                            yesNoCardAction.setNumCards(2);
                            yesNoCardAction.setInstructions("Do you want to discard two cards and draw one card?");
                        }
                        game.setPlayerCardAction(otherPlayer, yesNoCardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
        }
        else if (cardAction.getCardName().equals("Vault2")) {
            player.drawCards(1);
        }

        return incompleteCard;
    }
}
