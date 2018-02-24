package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlchemySpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        Map<Integer, Card> supplyMap = game.getSupplyMap();
        Map<Integer, Integer> supply = game.getSupply();
        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();
        int currentPlayerIndex = game.getCurrentPlayerIndex();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Apothecary")) {
            Player player = game.getCurrentPlayer();
            List<Card> cards = new ArrayList<Card>();
            boolean hasMoreCards = true;
            int cardsRevealed = 0;
            List<Card> revealedCards = new ArrayList<Card>();
            while (hasMoreCards && cardsRevealed < 4) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    cardsRevealed++;
                    revealedCards.add(c);
                    if (c.isCopper() || c.isPotion()) {
                        player.addCardToHand(c);
                    } else {
                        cards.add(c);
                    }
                }
            }
            if (revealedCards.size() > 0) {
                game.addHistory("Apothecary revealed ", KingdomUtil.groupCards(revealedCards, true));
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to reveal");
            }
            if (cards.size() == 1) {
                player.addCardToTopOfDeck(cards.get(0));
            } else if (cards.size() > 0) {
                game.refreshHand(player);
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                cardAction.setDeck(Deck.Alchemy);
                cardAction.setHideOnSelect(true);
                cardAction.setNumCards(cards.size());
                cardAction.setCardName(card.getName());
                cardAction.setCards(cards);
                cardAction.setButtonValue("Done");
                cardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Apprentice")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Alchemy);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards");
            }
        } else if (card.getName().equals("Familiar")) {
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        Card topCard = player.removeTopDeckCard();
                        if (topCard != null) {
                            player.addCardToDiscard(topCard);
                            game.playerDiscardedCard(player, topCard);
                            if (game.isCardInSupply(Card.CURSE_ID)) {
                                game.playerGainedCard(player, game.getCurseCard());
                                game.refreshDiscard(player);
                            }
                        }
                    } else {
                        if (player.hasLighthouse()) {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
        } else if (card.getName().equals("Golem")) {
            Player player = game.getCurrentPlayer();
            List<Card> revealedCards = new ArrayList<Card>();
            List<Card> setAsideCards = new ArrayList<Card>();
            List<Card> cards = new ArrayList<Card>();
            boolean hasMoreCards = true;
            int actionCardsFound = 0;
            while (hasMoreCards && actionCardsFound < 2) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    revealedCards.add(c);
                    if (c.isAction() && !c.getName().equals("Golem")) {
                        actionCardsFound++;
                        cards.add(c);
                    } else {
                        setAsideCards.add(c);
                    }
                }
            }
            if (revealedCards.size() > 0) {
                game.addHistory(player.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                player.getDiscard().addAll(setAsideCards);
                for (Card c : setAsideCards) {
                    game.playerDiscardedCard(player, c);
                }
            }
            if (cards.size() == 1) {
                game.getGolemActions().push(cards.get(0));
            } else if (cards.size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Deck.Alchemy);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setCards(cards);
                cardAction.setInstructions("Select which action you would like to play first and then click Done.");
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory("No actions were found for the ", KingdomUtil.getCardWithBackgroundColor(card), " to play.");
            }
        } else if (card.getName().equals("Scrying Pool")) {
            incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
            Player currentPlayer = game.getCurrentPlayer();
            if (currentPlayer.lookAtTopDeckCard() != null) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Alchemy);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("You are looking at the top card of your deck. Do you want to discard it?");
                cardAction.getCards().add(currentPlayer.lookAtTopDeckCard());
                cardAction.setPlayerId(currentPlayer.getUserId());
                incompleteCard.getExtraCardActions().add(cardAction);
            } else {
                game.addHistory(currentPlayer.getUsername(), " did not have a card to draw");
            }
            int nextPlayerIndex = game.getNextPlayerIndex();
            while (nextPlayerIndex != currentPlayerIndex) {
                Player nextPlayer = players.get(nextPlayerIndex);
                if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                    game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                    if (nextPlayer.lookAtTopDeckCard() != null) {
                        CardAction nextCardAction = new CardAction(CardAction.TYPE_YES_NO);
                        nextCardAction.setDeck(Deck.Alchemy);
                        nextCardAction.setCardName(card.getName());
                        nextCardAction.setInstructions("You are looking at the top card of " + nextPlayer.getUsername() + "'s deck. Do you want to discard it?");
                        nextCardAction.getCards().add(nextPlayer.lookAtTopDeckCard());
                        nextCardAction.setPlayerId(nextPlayer.getUserId());
                        incompleteCard.getExtraCardActions().add(nextCardAction);
                    } else {
                        game.addHistory(nextPlayer.getUsername(), " did not have a card to draw");
                    }
                } else {
                    if (nextPlayer.hasLighthouse()) {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                    } else {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                    }
                }
                if (nextPlayerIndex == players.size() - 1) {
                    nextPlayerIndex = 0;
                } else {
                    nextPlayerIndex++;
                }
            }
            if (!incompleteCard.getExtraCardActions().isEmpty()) {
                CardAction cardAction = incompleteCard.getExtraCardActions().remove();
                game.setPlayerCardAction(currentPlayer, cardAction);
            } else {
                boolean foundNonActionCard = false;
                while (!foundNonActionCard) {
                    Card topDeckCard = currentPlayer.removeTopDeckCard();
                    if (topDeckCard == null) {
                        break;
                    }
                    game.addHistory(currentPlayer.getUsername(), " revealed ", KingdomUtil.getArticleWithCardName(topDeckCard));
                    if (!topDeckCard.isAction()) {
                        foundNonActionCard = true;
                    }
                    currentPlayer.addCardToHand(topDeckCard);
                }
            }
        } else if (card.getName().equals("Transmute")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Alchemy);
                cardAction.setCardName(card.getName());
                cardAction.setCardId(card.getCardId());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards");
            }
        } else if (card.getName().equals("University")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY);
            cardAction.setDeck(Deck.Alchemy);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select one of the following cards to gain and then click Done, or just click Done if you don't want to gain a card.");
            for (Card c : supplyMap.values()) {
                if (c.isAction() && game.getCardCost(c) <= 5 && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        }

        return incompleteCard;
    }
}