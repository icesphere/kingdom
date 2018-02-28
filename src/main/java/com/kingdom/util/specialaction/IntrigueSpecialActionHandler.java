package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntrigueSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        Map<Integer, Card> cardMap = game.getCardMap();
        Map<Integer, Card> supplyMap = game.getSupplyMap();
        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();
        int currentPlayerIndex = game.getCurrentPlayerIndex();
        IncompleteCard incompleteCard = null;

        switch (card.getName()) {
            case "Baron": {
                Player player = game.getCurrentPlayer();
                boolean hasEstate = false;
                for (Card c : player.getHand()) {
                    if (c.getCardId() == Card.ESTATE_ID) {
                        hasEstate = true;
                        break;
                    }
                }
                if (hasEstate) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                    cardAction.setDeck(Deck.Intrigue);
                    cardAction.setCardName(card.getName());
                    cardAction.setInstructions("Do you want to discard an Estate card from your hand? If you do then you get +4 coins, otherwise you gain an Estate card.");
                    game.setPlayerCardAction(player, cardAction);
                } else {
                    if (game.isCardInSupply(Card.ESTATE_ID)) {
                        game.playerGainedCard(player, game.getEstateCard());
                        game.refreshDiscard(player);
                    }
                }
                break;
            }
            case "Bridge":
                game.incrementCostDiscount();
                game.refreshAllPlayersSupply();
                game.refreshAllPlayersPlayingArea();
                game.refreshAllPlayersHandArea();
                break;
            case "Conspirator":
                if (game.getNumActionsCardsPlayed() >= 3) {
                    Player player = game.getCurrentPlayer();
                    player.drawCards(1);
                    player.addActions(1);
                    game.addHistory(player.getUsername(), " gained +1 Card, +1 Action from ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Conspirator", Card.ACTION_COLOR));
                }
                break;
            case "Courtyard": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(player.getHand()));
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card from your hand to put on top of your deck.");
                if (!cardAction.getCards().isEmpty()) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Ironworks": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
                for (Card c : supplyMap.values()) {
                    if (game.getCardCost(c) <= 4 && !c.getCostIncludesPotion() && game.isCardInSupply(c)) {
                        cardAction.getCards().add(c);
                    }
                }
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Masquerade":
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, true);
                game.addNextAction("trashCard");
                game.getMasqueradeCards().clear();
                for (Player player : players) {
                    if (player.getHand().size() > 0) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                        cardAction.setDeck(Deck.Intrigue);
                        cardAction.setCardName(card.getName());
                        cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(player.getHand()));
                        cardAction.setNumCards(1);
                        cardAction.setInstructions("Choose a card to pass to the next player and then click Done.");
                        cardAction.setButtonValue("Done");
                        game.setPlayerCardAction(player, cardAction);
                    } else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                    }
                }
                incompleteCard.allActionsSet();
                break;
            case "Mining Village": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.getCards().add(card);
                cardAction.setInstructions("Do you want to trash this card to gain 2 coins?");
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Minion": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Choose one: +2 coins OR Discard your hand and draw 4 cards, and each other player with at least 5 cards in hand discards their hand and draws 4 cards.");
                cardAction.getChoices().add(new CardActionChoice("+2 Coins", "coins"));
                cardAction.getChoices().add(new CardActionChoice("Discard hand", "discard"));
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Nobles": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Choose one: +3 Cards OR +2 Actions.");
                cardAction.getChoices().add(new CardActionChoice("+3 Cards", "cards"));
                cardAction.getChoices().add(new CardActionChoice("+2 Actions", "actions"));
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Pawn": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Choose a combination.");
                cardAction.getChoices().add(new CardActionChoice("+1 Card, +1 Action", "cardAndAction"));
                cardAction.getChoices().add(new CardActionChoice("+1 Card, +1 Buy", "cardAndBuy"));
                cardAction.getChoices().add(new CardActionChoice("+1 Card, +1 Coin", "cardAndCoin"));
                cardAction.getChoices().add(new CardActionChoice("+1 Action, +1 Buy", "actionAndBuy"));
                cardAction.getChoices().add(new CardActionChoice("+1 Action, +1 Coin", "actionAndCoin"));
                cardAction.getChoices().add(new CardActionChoice("+1 Buy, +1 Coin", "buyAndCoin"));
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Saboteur":
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
                for (Player player : players) {
                    if (player.getUserId() != currentPlayerId) {
                        if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            List<Card> setAsideCards = new ArrayList<Card>();
                            Card c = player.removeTopDeckCard();
                            while (c != null && game.getCardCost(c) < 3) {
                                setAsideCards.add(c);
                                c = player.removeTopDeckCard();
                            }
                            player.getDiscard().addAll(setAsideCards);
                            for (Card setAsideCard : setAsideCards) {
                                game.playerDiscardedCard(player, setAsideCard);
                            }
                            if (c != null) {
                                game.getTrashedCards().add(c);
                                game.playerLostCard(player, c);
                                CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY);
                                cardAction.setDeck(Deck.Intrigue);
                                cardAction.setCardName(card.getName());
                                cardAction.setButtonValue("Done");
                                cardAction.setNumCards(1);
                                cardAction.setInstructions("The Saboteur trashed your " + c.getName() + ". Select one of the following cards to gain and then click Done. If you don't want to gain a card just click Done.");
                                game.addHistory("The ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Saboteur", Card.ACTION_COLOR), " trashed ", player.getUsername(), "'s ", c.getName());
                                int highestCost = game.getCardCost(c) - 2;
                                for (Card cardToGain : supplyMap.values()) {
                                    if (game.getCardCost(cardToGain) <= highestCost && (c.getCostIncludesPotion() || !cardToGain.getCostIncludesPotion()) && game.isCardInSupply(cardToGain)) {
                                        cardAction.getCards().add(cardToGain);
                                    }
                                }
                                if (cardAction.getCards().size() > 0) {
                                    game.setPlayerCardAction(player, cardAction);
                                } else {
                                    incompleteCard.setPlayerActionCompleted(player.getUserId());
                                }
                            } else {
                                game.addHistory(player.getUsername(), " did not have any cards costing 3 coins or more to trash");
                                incompleteCard.setPlayerActionCompleted(player.getUserId());
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            if (player.hasLighthouse()) {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                            } else {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet();
                break;
            case "Scout": {
                Player player = game.getCurrentPlayer();
                List<Card> cards = new ArrayList<Card>();
                boolean hasMoreCards = true;
                List<Card> revealedCards = new ArrayList<Card>();
                while (hasMoreCards && revealedCards.size() < 4) {
                    Card c = player.removeTopDeckCard();
                    if (c == null) {
                        hasMoreCards = false;
                    } else {
                        revealedCards.add(c);
                        if (c.isVictory()) {
                            player.addCardToHand(c);
                        } else {
                            cards.add(c);
                        }
                    }
                }
                if (!cards.isEmpty()) {
                    game.addHistory(KingdomUtil.INSTANCE.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.INSTANCE.groupCards(revealedCards, true));
                } else {
                    game.addHistory(player.getUsername(), " did not have any cards to reveal");
                }
                if (cards.size() == 1) {
                    player.addCardToTopOfDeck(cards.get(0));
                } else if (cards.size() > 0) {
                    game.refreshHand(player);
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    cardAction.setDeck(Deck.Intrigue);
                    cardAction.setHideOnSelect(true);
                    cardAction.setNumCards(cards.size());
                    cardAction.setCardName(card.getName());
                    cardAction.setCards(cards);
                    cardAction.setButtonValue("Done");
                    cardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Secret Chamber": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getHand());
                cardAction.setNumCards(player.getHand().size());
                cardAction.setInstructions("Discard any number of cards. +1 Coin per card discarded. Select the Cards you want to discard and then click Done.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Shanty Town": {
                Player player = game.getCurrentPlayer();
                boolean hasAction = false;
                for (Card c : player.getHand()) {
                    if (c.isAction()) {
                        hasAction = true;
                    }
                }
                if (!hasAction) {
                    player.drawCards(2);
                    game.addHistory(player.getUsername(), " did not have any action cards and got +2 cards.");
                }
                break;
            }
            case "Steward": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Intrigue);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Choose one:");
                cardAction.getChoices().add(new CardActionChoice("+2 Cards", "cards"));
                cardAction.getChoices().add(new CardActionChoice("+2 Coins", "coins"));
                cardAction.getChoices().add(new CardActionChoice("Trash 2 Cards", "trash"));
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Swindler":
                incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
                Player currentPlayer = game.getCurrentPlayer();
                int nextPlayerIndex = game.getNextPlayerIndex();
                while (nextPlayerIndex != currentPlayerIndex) {
                    Player nextPlayer = players.get(nextPlayerIndex);
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                        game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        Card topCard = nextPlayer.removeTopDeckCard();
                        if (topCard != null) {
                            game.getTrashedCards().add(topCard);
                            game.playerLostCard(nextPlayer, topCard);
                            game.addHistory("The ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Swindler", Card.ACTION_COLOR), " trashed ", nextPlayer.getUsername(), "'s ", KingdomUtil.INSTANCE.getCardWithBackgroundColor(topCard));
                            CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                            nextCardAction.setDeck(Deck.Intrigue);
                            nextCardAction.setNumCards(1);
                            nextCardAction.setButtonValue("Done");
                            nextCardAction.setCardName(card.getName());
                            nextCardAction.setInstructions("You trashed " + nextPlayer.getUsername() + "'s " + topCard.getName() + ". Select the card you want to give to " + nextPlayer.getUsername() + " and then click Done.");
                            nextCardAction.setPlayerId(nextPlayer.getUserId());
                            int cost = game.getCardCost(topCard);
                            for (Card c : supplyMap.values()) {
                                if (game.getCardCost(c) == cost && c.getCostIncludesPotion() == topCard.getCostIncludesPotion() && game.isCardInSupply(c)) {
                                    nextCardAction.getCards().add(c);
                                }
                            }
                            if (nextCardAction.getCards().size() > 0) {
                                incompleteCard.getExtraCardActions().add(nextCardAction);
                            } else {
                                game.addHistory(nextPlayer.getUsername(), " did not have any cards in the supply to gain");
                            }
                        } else {
                            game.addHistory(nextPlayer.getUsername(), " did not have a card to draw");
                        }
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
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
                    cardAction.setDeck(Deck.Intrigue);
                    game.setPlayerCardAction(currentPlayer, cardAction);
                }
                break;
            case "Torturer":
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
                for (Player player : players) {
                    if (player.getUserId() != currentPlayerId) {
                        if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                        } else if (!player.hasMoat() && !player.hasLighthouse()) {
                            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                            cardAction.setDeck(Deck.Intrigue);
                            cardAction.setCardName(card.getName());
                            cardAction.setInstructions("Choose one: Discard 2 cards OR Gain a Curse card into your hand.");
                            cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                            cardAction.getChoices().add(new CardActionChoice("Curse", "curse"));
                            game.setPlayerCardAction(player, cardAction);
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            if (player.hasLighthouse()) {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                            } else {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet();
                break;
            case "Trading Post": {
                Player player = game.getCurrentPlayer();
                if (player.getHand().size() == 1) {
                    game.getTrashedCards().add(player.getHand().get(0));
                    game.playerLostCard(player, player.getHand().get(0));
                    player.removeCardFromHand(player.getHand().get(0));
                    game.addHistory("Trading Post trashed the last card in ", player.getUsername(), "'s hand");
                } else if (player.getHand().size() > 1) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                    cardAction.setDeck(Deck.Intrigue);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(2);
                    cardAction.setInstructions("Select two cards to trash.");
                    cardAction.setCards(player.getHand());
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Tribute": {
                Player player = game.getCurrentPlayer();
                Player nextPlayer = players.get(game.getNextPlayerIndex());
                Card firstCard = nextPlayer.removeTopDeckCard();
                Card secondCard = nextPlayer.removeTopDeckCard();
                nextPlayer.addCardToDiscard(firstCard);
                game.playerDiscardedCard(nextPlayer, firstCard);
                nextPlayer.addCardToDiscard(secondCard);
                game.playerDiscardedCard(nextPlayer, secondCard);

                List<Card> cards = new ArrayList<Card>(2);
                if (firstCard != null) {
                    cards.add(firstCard);
                }
                if (secondCard != null && !firstCard.getName().equals(secondCard.getName())) {
                    cards.add(secondCard);
                }
                for (Card c : cards) {
                    if (c.isAction()) {
                        player.addActions(2);
                    }
                    if (c.isTreasure()) {
                        player.addCoins(2);
                    }
                    if (c.isVictory()) {
                        player.drawCards(2);
                    }
                }

                if (firstCard != null && secondCard != null) {
                    game.addHistory("The top two cards of ", nextPlayer.getUsername(), "'s deck for tribute were ", KingdomUtil.INSTANCE.getArticleWithCardName(firstCard), " and ", KingdomUtil.INSTANCE.getArticleWithCardName(secondCard));
                    game.refreshDiscard(nextPlayer);
                }
                break;
            }
            case "Upgrade": {
                Player player = game.getCurrentPlayer();
                if (player.getHand().size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                    cardAction.setDeck(Deck.Intrigue);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(1);
                    cardAction.setInstructions("Select a card to trash.");
                    cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(player.getHand()));
                    game.setPlayerCardAction(player, cardAction);
                } else {
                    game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
                }
                break;
            }
            case "Wishing Well": {
                Player player = game.getCurrentPlayer();
                if (player.getDeck().size() + player.getDiscard().size() == 0) {
                    game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("Your deck and discard piles are empty."));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                    cardAction.setDeck(Deck.Intrigue);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(1);
                    cardAction.setInstructions("Select the card that you think will be on top of your deck.");
                    cardAction.getCards().addAll(cardMap.values());
                    if (cardAction.getCards().size() > 0) {
                        game.setPlayerCardAction(player, cardAction);
                    }
                }
                break;
            }
        }

        return incompleteCard;
    }
}