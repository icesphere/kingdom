package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GainCardsHandler {
    public static void handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        int type = cardAction.getType();
        Map<Integer, Card> cardMap = game.getCardMap();

        if (!cardAction.getCardName().equals("Tournament") && !cardAction.getCardName().equals("Museum") && !cardAction.getCardName().equals("Artisan")) {
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY || type == CardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY) {
                    game.playerGainedCard(player, card);
                } else {
                    game.playerGainedCard(player, card, false);
                }
            }
        }

        if (cardAction.getCardName().equals("Artisan")) {
            Card card = cardMap.get(selectedCardIds.get(0));

            game.playerGainedCardToHand(player, card);

            game.refreshPlayingArea(player);

            CardAction putCardOnTopOfDeckAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
            putCardOnTopOfDeckAction.setDeck(Deck.Kingdom);
            putCardOnTopOfDeckAction.setCardName("Artisan");
            putCardOnTopOfDeckAction.getCards().addAll(player.getHand());
            putCardOnTopOfDeckAction.setButtonValue("Done");
            putCardOnTopOfDeckAction.setNumCards(1);
            putCardOnTopOfDeckAction.setInstructions("Select a card from your hand to put on top of your deck.");
            game.setPlayerCardAction(player, putCardOnTopOfDeckAction);
        }
        if (cardAction.getCardName().equals("Black Market")) {
            Card cardBought = cardMap.get(selectedCardIds.get(0));
            game.boughtBlackMarketCard(cardBought);
            game.getBlackMarketCardsToBuy().remove(cardBought);
            CardAction chooseOrderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
            chooseOrderCardAction.setDeck(Deck.Promo);
            chooseOrderCardAction.setHideOnSelect(true);
            chooseOrderCardAction.setNumCards(game.getBlackMarketCardsToBuy().size());
            chooseOrderCardAction.setCardName("Black Market");
            chooseOrderCardAction.setCards(game.getBlackMarketCardsToBuy());
            chooseOrderCardAction.setButtonValue("Done");
            chooseOrderCardAction.setInstructions("Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)");
            game.setPlayerCardAction(player, chooseOrderCardAction);
        } else if (cardAction.getCardName().equals("Develop")) {
            if (cardAction.getPhase() < 3) {
                CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                gainCardAction.setDeck(Deck.Hinterlands);
                gainCardAction.setCardName(cardAction.getCardName());
                gainCardAction.setButtonValue("Done");
                gainCardAction.setNumCards(1);
                gainCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
                gainCardAction.setAssociatedCard(cardAction.getAssociatedCard());
                gainCardAction.setPhase(3);

                List<Card> cards = new ArrayList<Card>();
                int cost = game.getCardCost(gainCardAction.getAssociatedCard());
                if (cardAction.getPhase() == 1) {
                    cost = cost - 1;
                } else if (cardAction.getPhase() == 2) {
                    cost = cost + 1;
                }
                for (Card c : game.getSupplyMap().values()) {
                    if (game.getCardCost(c) == cost && cardAction.getAssociatedCard().getCostIncludesPotion() == c.getCostIncludesPotion() && game.getSupply().get(c.getCardId()) > 0) {
                        cards.add(c);
                    }
                }

                gainCardAction.setCards(cards);
                game.setPlayerCardAction(player, gainCardAction);
            }
        } else if (cardAction.getCardName().equals("Horn of Plenty")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            if (card.isVictory()) {
                ((LinkedList) game.getCardsPlayed()).removeLastOccurrence(cardAction.getAssociatedCard());
                game.getTreasureCardsPlayed().remove(cardAction.getAssociatedCard());
                game.getTrashedCards().add(cardAction.getAssociatedCard());
                game.playerLostCard(player, cardAction.getAssociatedCard());
                game.addHistory(player.getUsername(), "'s ", KingdomUtil.INSTANCE.getCardWithBackgroundColor(cardAction.getAssociatedCard()), " was trashed");
                game.refreshAllPlayersCardsPlayed();
            }
        } else if (cardAction.getCardName().equals("Ironworks")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            if (card.isAction()) {
                player.addActions(1);
                game.refreshAllPlayersCardsPlayed();
            }
            if (card.isTreasure()) {
                player.addCoins(1);
            }
            if (card.isVictory()) {
                player.drawCards(1);
            }
        } else if (cardAction.getCardName().equals("Museum")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCard(player, card, false);
            game.getPrizeCards().remove(card);
        } else if (cardAction.getCardName().equals("Tournament")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCardToTopOfDeck(player, card, false);
            game.getPrizeCards().remove(card);
        } else if (cardAction.getCardName().equals("University")) {
            if (selectedCardIds.isEmpty()) {
                game.addHistory(player.getUsername(), " chose not to gain a card with ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("University", Card.ACTION_COLOR));
            }
        }
    }
}
