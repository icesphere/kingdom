package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.*;

public class ProsperitySpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();
        Map<Integer, Integer> supply = game.getSupply();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Bishop")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Prosperity);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("City")) {
            Player player = game.getCurrentPlayer();
            if (game.getNumEmptyPiles() >= 1) {
                player.drawCards(1);
            }
            if (game.getNumEmptyPiles() >= 2) {
                player.addCoins(1);
                player.addBuys(1);
            }
        } else if (card.getName().equals("Counting House")) {
            Player player = game.getCurrentPlayer();
            int numCoppersInDiscard = 0;
            for (Card c : player.getDiscard()) {
                if (c.getCardId() == Card.COPPER_ID) {
                    numCoppersInDiscard++;
                }
            }
            if (numCoppersInDiscard > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_NUMBER_BETWEEN);
                cardAction.setDeck(Deck.Prosperity);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setStartNumber(0);
                cardAction.setEndNumber(numCoppersInDiscard);
                cardAction.setInstructions("Click the number of Coppers you want to add to your hand from your discard pile.");
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("There were no Coppers in your discard pile."));
            }
        } else if (card.getName().equals("Expand")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Prosperity);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Forge")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                cardAction.setDeck(Deck.Prosperity);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(player.getHand().size());
                cardAction.setInstructions("Select the cards you want to trash. You will then gain a card in cost exactly equal to the total cost in coins of the trashed cards.");
                cardAction.setCards(player.getHand());
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Goons")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && player.getHand().size() > 3 && !player.hasLighthouse()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND);
                        cardAction.setDeck(Deck.Prosperity);
                        cardAction.setCardName(card.getName());
                        cardAction.getCards().addAll(player.getHand());
                        cardAction.setNumCards(3);
                        cardAction.setInstructions("Discard down to 3 cards. Select the Cards you want to discard and then click Done.");
                        cardAction.setButtonValue("Done");
                        game.setPlayerCardAction(player, cardAction);
                    } else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        if (player.hasLighthouse()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (player.hasMoat()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        } else {
                            game.addHistory(player.getUsername(), " had 3 or less cards");
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("King's Court")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
            cardAction.setDeck(Deck.Prosperity);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select one of the following actions to play three times and then click Done, or just click Done if you don't want to select an action.");
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getActionCards()));
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Mint")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Deck.Prosperity);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select the treasure card you want to gain a copy of.");
            Set<Card> treasureCards = new HashSet<Card>();
            for (Card c : player.getHand()) {
                if (c.isTreasure() && game.isCardInSupply(c)) {
                    treasureCards.add(c);
                }
            }
            cardAction.getCards().addAll(treasureCards);
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Mountebank")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        if (player.getCurseCardsInHand() > 0) {
                            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                            cardAction.setDeck(Deck.Prosperity);
                            cardAction.setCardName(card.getName());
                            cardAction.setInstructions("Choose one: Discard a curse OR gain a Curse and a Copper.");
                            cardAction.getChoices().add(new CardActionChoice("Discard Curse", "discard"));
                            cardAction.getChoices().add(new CardActionChoice("Gain Cards", "gain"));
                            game.setPlayerCardAction(player, cardAction);
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            if (game.isCardInSupply(Card.CURSE_ID)) {
                                game.playerGainedCard(player, game.getCurseCard());
                            }
                            if (game.isCardInSupply(Card.COPPER_ID)) {
                                game.playerGainedCard(player, game.getCopperCard());
                            }
                            game.refreshDiscard(player);
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        if (player.hasLighthouse()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Rabble")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        List<Card> cardsRevealed = new ArrayList<Card>();
                        while (cardsRevealed.size() < 3) {
                            Card topDeckCard = player.removeTopDeckCard();
                            if (topDeckCard == null) {
                                break;
                            }
                            cardsRevealed.add(topDeckCard);
                        }
                        if (cardsRevealed.size() > 0) {
                            game.addHistory("The top cards from ", player.getUsername(), "'s deck were ", KingdomUtil.getCardNames(cardsRevealed));
                            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                            cardAction.setDeck(Deck.Prosperity);
                            for (Card c : cardsRevealed) {
                                if (c.isTreasure() || c.isAction()) {
                                    player.addCardToDiscard(c);
                                    game.playerDiscardedCard(player, c);
                                } else {
                                    cardAction.getCards().add(c);
                                }
                            }
                            if (cardAction.getCards().size() == 1) {
                                incompleteCard.setPlayerActionCompleted(player.getUserId());
                                player.addCardToTopOfDeck(cardAction.getCards().get(0));
                                game.addHistory(player.getUsername(), " put one card back on top of " + player.getPronoun(), " deck");
                            } else if (cardAction.getCards().size() > 0) {
                                game.addHistory(player.getUsername(), " put " + cardAction.getCards().size() + " cards back on top of " + player.getPronoun(), " deck");
                                cardAction.setHideOnSelect(true);
                                cardAction.setNumCards(cardAction.getCards().size());
                                cardAction.setCardName(card.getName());
                                cardAction.setButtonValue("Done");
                                cardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                                game.setPlayerCardAction(player, cardAction);
                            } else {
                                incompleteCard.setPlayerActionCompleted(player.getUserId());
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        if (player.hasLighthouse()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (player.hasMoat()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Trade Route")) {
            Player player = game.getCurrentPlayer();
            if (game.getTradeRouteTokensOnMat() > 0) {
                player.addCoins(game.getTradeRouteTokensOnMat());
                game.addHistory(player.getUsername(), " gained +", KingdomUtil.getPlural(game.getTradeRouteTokensOnMat(), "Coin"), " from playing Trade Route");
            }
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Prosperity);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Vault")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
            cardAction.setDeck(Deck.Prosperity);
            cardAction.setCardName(card.getName());
            cardAction.getCards().addAll(player.getHand());
            cardAction.setNumCards(player.getHand().size());
            cardAction.setInstructions("Discard any number of cards. +1 Coin per card discarded. Select the Cards you want to discard and then click Done.");
            cardAction.setButtonValue("Done");
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Watchtower")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() < 6) {
                player.drawCards(6 - player.getHand().size());
                game.refreshHand(player);
            }
        }

        return incompleteCard;
    }
}
