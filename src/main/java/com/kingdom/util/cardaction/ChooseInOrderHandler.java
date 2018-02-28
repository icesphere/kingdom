package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseInOrderHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        IncompleteCard incompleteCard = null;

        Map<Integer, Card> cardMap = game.getCardMap();
        if (cardAction.getCardName().equals("Apothecary") || cardAction.getCardName().equals("Navigator") || cardAction.getCardName().equals("Scout") || cardAction.getCardName().equals("Rabble") || cardAction.getCardName().equals("Ghost Ship") || cardAction.getCardName().equals("Mandarin") || cardAction.getCardName().equals("Cartographer") || cardAction.getCardName().equals("Oracle")) {
            List<Card> cards = new ArrayList<Card>();
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                cards.add(card);
            }
            player.getDeck().addAll(0, cards);
            if (cardAction.getCardName().equals("Ghost Ship")) {
                game.addHistory(player.getUsername(), " added ", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "card"), " on top of ", player.getPronoun(), " deck");
            } else if (cardAction.getCardName().equals("Mandarin")) {
                game.addHistory(player.getUsername(), " added ", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "treasure card"), " from play on top of ", player.getPronoun(), " deck");
                game.getCardsPlayed().removeAll(game.getTreasureCardsPlayed());
                game.getTreasureCardsPlayed().clear();
                game.refreshAllPlayersCardsPlayed();
            } else if (cardAction.getCardName().equals("Oracle")) {
                if ((!game.hasIncompleteCard() || game.getIncompleteCard().getExtraCardActions().isEmpty()) && game.isCurrentPlayer(player)) {
                    player.drawCards(2);
                }
            }
        } else if (cardAction.getCardName().equals("Black Market")) {
            List<Card> cards = new ArrayList<Card>();
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                cards.add(card);
            }
            game.getBlackMarketCards().addAll(cards);
        } else if (cardAction.getCardName().equals("Black Market Treasure")) {
            boolean queueTreasureCards = false;
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                if (!queueTreasureCards && card.isAutoPlayTreasure()) {
                    game.playTreasureCard(player, card, true, true, false, true, true);
                } else {
                    game.getBlackMarketTreasureQueue().add(card);
                    queueTreasureCards = true;
                }
            }
            incompleteCard = new SinglePlayerIncompleteCard("Black Market", game);
            game.addNextAction("Buy Card");
            incompleteCard.setPlayerActionCompleted(player.getUserId());
        } else if (cardAction.getCardName().equals("Lookout")) {
            Card cardToTrash = cardMap.get(selectedCardIds.get(0));
            game.getTrashedCards().add(cardToTrash);
            game.playerLostCard(player, cardToTrash);
            game.addHistory("The ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lookout", Card.ACTION_COLOR), " trashed ", player.getUsername(), "'s ", cardToTrash.getName());
            Card cardToDiscard = cardMap.get(selectedCardIds.get(1));
            player.addCardToDiscard(cardToDiscard);
            game.playerDiscardedCard(player, cardToDiscard);
            game.addHistory("The ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lookout", Card.ACTION_COLOR), " discarded ", player.getUsername(), "'s ", cardToDiscard.getName());
            if (selectedCardIds.size() == 3) {
                Card cardToPutBack = cardMap.get(selectedCardIds.get(2));
                player.addCardToTopOfDeck(cardToPutBack);
            }
        }

        return incompleteCard;
    }
}
