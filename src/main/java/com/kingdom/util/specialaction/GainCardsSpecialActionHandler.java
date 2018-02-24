package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.*;

public class GainCardsSpecialActionHandler {

    public static CardAction getCardAction(Game game, Player player, Card card) {
        Map<Integer, Card> supplyMap = game.getSupplyMap();

        if (card.getName().equals("Border Village")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            cardAction.setGainCardAction(true);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            int cost = game.getCardCost(card);
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) < cost && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                return cardAction;
            }
        } else if (card.getName().equals("Cache")) {
            for (int i = 0; i < 2; i++) {
                if (game.getSupply().get(Card.COPPER_ID) > 0) {
                    game.playerGainedCard(player, game.getCopperCard());
                }
            }
        } else if (card.getName().equals("Duchy")) {
            Card duchessCard = game.getKingdomCardMap().get("Duchess");
            if (game.isCheckDuchess() && game.isCardInSupply(duchessCard)) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setGainCardAction(true);
                cardAction.setDeck(Card.DECK_REACTION);
                cardAction.setCardName("Duchess for Duchy");
                cardAction.setAssociatedCard(card);
                cardAction.getCards().add(duchessCard);
                cardAction.setInstructions("Do you want to gain a Duchess?");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Embassy")) {
            int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (playerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = game.getPlayers().get(playerIndex);
                if (game.isCardInSupply(Card.SILVER_ID)) {
                    game.playerGainedCard(nextPlayer, game.getSilverCard());
                    game.refreshDiscard(nextPlayer);
                }
                playerIndex = game.calculateNextPlayerIndex(playerIndex);
            }
        } else if (card.getName().equals("Inn")) {
            if (!player.getDiscard().isEmpty()) {
                List<Card> cards = new ArrayList<Card>();
                for (Card c : player.getDiscard()) {
                    if (c.isAction()) {
                        cards.add(c);
                    }
                }
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                cardAction.setGainCardAction(true);
                cardAction.setDeck(Card.DECK_HINTERLANDS);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(cards.size());
                cardAction.setCards(cards);
                cardAction.setInstructions("Select the Action cards from your discard pile that you want to shuffle into your deck and then click Done.");
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getErrorDialog("There were no Action cards in your discard pile."));
                }
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.getErrorDialog("Your discard pile is empty."));
            }
        } else if (card.getName().equals("Ill-Gotten Gains")) {
            int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (playerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = game.getPlayers().get(playerIndex);
                if (game.isCardInSupply(Card.CURSE_ID)) {
                    game.playerGainedCard(nextPlayer, game.getCurseCard());
                    game.refreshDiscard(nextPlayer);
                }
                playerIndex = game.calculateNextPlayerIndex(playerIndex);
            }
        } else if (card.getName().equals("Mandarin")) {
            if (!game.getTreasureCardsPlayed().isEmpty() && player.getUserId() == game.getCurrentPlayerId()) {
                Set<Card> cards = new HashSet<Card>(game.getTreasureCardsPlayed());
                if (cards.size() == 1) {
                    //put all cards on top of deck since they are all the same
                    game.addHistory(player.getUsername(), " added ", KingdomUtil.getPlural(game.getTreasureCardsPlayed().size(), "treasure card"), " from play on top of ", player.getPronoun(), " deck");
                    player.getDeck().addAll(0, game.getTreasureCardsPlayed());
                    game.getCardsPlayed().removeAll(game.getTreasureCardsPlayed());
                    game.getTreasureCardsPlayed().clear();
                    game.refreshAllPlayersCardsPlayed();
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    cardAction.setGainCardAction(true);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setHideOnSelect(true);
                    cardAction.setNumCards(game.getTreasureCardsPlayed().size());
                    cardAction.setCardName(card.getName());
                    cardAction.setAssociatedCard(card);
                    cardAction.setCards(game.getTreasureCardsPlayed());
                    cardAction.setButtonValue("Done");
                    cardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                    return cardAction;
                }
            }
        }

        return null;
    }

    public static CardAction getFoolsGoldCardAction() {
        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
        cardAction.setDeck(Card.DECK_HINTERLANDS);
        cardAction.setCardName("Fool's Gold");
        cardAction.setInstructions("Do you want to trash your Fool's Gold to gain a Gold on top of your deck?");

        return cardAction;
    }
}
