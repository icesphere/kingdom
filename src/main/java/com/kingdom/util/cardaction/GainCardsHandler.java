package com.kingdom.util.cardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Jul 31, 2010
 * Time: 12:40:51 PM
 */
public class GainCardsHandler {
    public static void handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds){

        int type = cardAction.getType();
        Map<Integer, Card> cardMap = game.getCardMap();

        if (!cardAction.getCardName().equals("Tournament") && !cardAction.getCardName().equals("Museum")) {
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY || type == CardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY) {
                    game.playerGainedCard(player, card);
                }
                else {
                    game.playerGainedCard(player, card, false);
                }
            }
        }

        if (cardAction.getCardName().equals("Black Market")) {
            Card cardBought = cardMap.get(selectedCardIds.get(0));
            game.boughtBlackMarketCard(cardBought);
            game.getBlackMarketCardsToBuy().remove(cardBought);
            CardAction chooseOrderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
            chooseOrderCardAction.setDeck(Card.DECK_PROMO);
            chooseOrderCardAction.setHideOnSelect(true);
            chooseOrderCardAction.setNumCards(game.getBlackMarketCardsToBuy().size());
            chooseOrderCardAction.setCardName("Black Market");
            chooseOrderCardAction.setCards(game.getBlackMarketCardsToBuy());
            chooseOrderCardAction.setButtonValue("Done");
            chooseOrderCardAction.setInstructions("Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)");
            game.setPlayerCardAction(player, chooseOrderCardAction);
        }
        else if (cardAction.getCardName().equals("Develop")) {
            if (cardAction.getPhase() < 3) {
                CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                gainCardAction.setDeck(Card.DECK_HINTERLANDS);
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
                }
                else if (cardAction.getPhase() == 2) {
                    cost = cost + 1;
                }
                for (Card c : game.getSupplyMap().values()) {
                    if (game.getCardCost(c) == cost && cardAction.getAssociatedCard().isCostIncludesPotion() == c.isCostIncludesPotion() && game.getSupply().get(c.getCardId()) > 0) {
                        cards.add(c);
                    }
                }

                gainCardAction.setCards(cards);
                game.setPlayerCardAction(player, gainCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Horn of Plenty")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            if (card.isVictory()) {
                ((LinkedList)game.getCardsPlayed()).removeLastOccurrence(cardAction.getAssociatedCard());
                game.getTreasureCardsPlayed().remove(cardAction.getAssociatedCard());
                game.getTrashedCards().add(cardAction.getAssociatedCard());
                game.playerLostCard(player, cardAction.getAssociatedCard());
                game.addHistory(player.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(cardAction.getAssociatedCard()), " was trashed");
                game.refreshAllPlayersCardsPlayed();
            }
        }
        else if (cardAction.getCardName().equals("Ironworks")) {
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
        }
        else if (cardAction.getCardName().equals("Museum")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCard(player, card, false);
            game.getPrizeCards().remove(card);
        }
        else if (cardAction.getCardName().equals("Tournament")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCardToTopOfDeck(player, card, false);
            game.getPrizeCards().remove(card);
        }
        else if (cardAction.getCardName().equals("University")) {
            if (selectedCardIds.isEmpty()) {
                game.addHistory(player.getUsername(), " chose not to gain a card with ", KingdomUtil.getWordWithBackgroundColor("University", Card.ACTION_COLOR));
            }
        }
    }
}
