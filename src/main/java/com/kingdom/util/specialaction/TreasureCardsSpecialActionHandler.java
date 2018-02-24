package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreasureCardsSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        IncompleteCard incompleteCard = null;
        Player player = game.getCurrentPlayer();

        if (card.getName().equals("Contraband")) {
            Player nextPlayer = game.getPlayers().get(game.getNextPlayerIndex());
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, nextPlayer.getUserId());
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_PROSPERITY);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a card that " + player.getUsername() + " can't buy this turn.");
            cardAction.getCards().addAll(game.getSupplyMap().values());
            game.setPlayerCardAction(nextPlayer, cardAction);
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Diadem")) {
            if (player.getActions() > 0) {
                player.addCoins(player.getActions());
                game.addHistory(player.getUsername(), " gained ", KingdomUtil.getPlural(player.getActions(), "Coin"), " from ", KingdomUtil.getCardWithBackgroundColor(card));
            } else {
                game.addHistory(player.getUsername(), " did not have any unused actions");
            }
        } else if (card.getName().equals("Fool's Gold")) {
            if (player.isFoolsGoldPlayed()) {
                player.addCoins(4);
            } else {
                player.addCoins(1);
                player.setFoolsGoldPlayed(true);
            }
        } else if (card.getName().equals("Horn of Plenty")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            cardAction.setDeck(Card.DECK_CORNUCOPIA);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            cardAction.setAssociatedCard(card);
            Set<String> cardNames = new HashSet<String>();
            for (Card c : game.getCardsPlayed()) {
                cardNames.add(c.getName());
            }
            for (Card c : game.getSupplyMap().values()) {
                if (game.getCardCost(c) <= cardNames.size() && !c.isCostIncludesPotion() && game.getSupply().get(c.getCardId()) > 0) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Ill-Gotten Gains")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName("Ill-Gotten Gains");
            cardAction.setInstructions("Do you want to gain a Copper card into your hand?");
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Loan")) {
            boolean treasureCardFound = false;
            List<Card> revealedCards = new ArrayList<Card>();
            List<Card> setAsideCards = new ArrayList<Card>();
            Card treasureCard = null;
            while (!treasureCardFound) {
                Card revealedCard = player.removeTopDeckCard();
                if (revealedCard == null) {
                    break;
                }
                revealedCards.add(revealedCard);
                if (revealedCard.isTreasure()) {
                    treasureCardFound = true;
                    treasureCard = revealedCard;
                } else {
                    setAsideCards.add(revealedCard);
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
            } else {
                game.addHistory(player.getUsername(), " did not have any cards");
            }
            player.getDiscard().addAll(setAsideCards);
            for (Card c : setAsideCards) {
                game.playerDiscardedCard(player, c);
            }
            if (treasureCard != null) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Card.DECK_PROSPERITY);
                cardAction.setCardName(card.getName());
                cardAction.getCards().add(treasureCard);
                cardAction.setInstructions("Do you want to discard or trash this card?");
                cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                cardAction.getChoices().add(new CardActionChoice("Trash", "trash"));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Quarry")) {
            game.incrementActionCardDiscount(2);
            game.refreshAllPlayersSupply();
            game.refreshAllPlayersPlayingArea();
            game.refreshAllPlayersHandArea();
        } else if (card.getName().equals("Storybook")) {
            if (!player.getVictoryCards().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                cardAction.setDeck(Card.DECK_FAIRYTALE);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getVictoryCards());
                cardAction.setNumCards(player.getVictoryCards().size());
                cardAction.setInstructions("Choose the victory cards from your hand to put under Storybook and then click Done. +1 coin per victory card added.");
                cardAction.setButtonValue("Done");
                cardAction.setAssociatedCard(card);
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Venture")) {
            boolean treasureCardFound = false;
            List<Card> revealedCards = new ArrayList<Card>();
            List<Card> setAsideCards = new ArrayList<Card>();
            while (!treasureCardFound) {
                Card revealedCard = player.removeTopDeckCard();
                if (revealedCard == null) {
                    break;
                }
                revealedCards.add(revealedCard);
                if (revealedCard.isTreasure()) {
                    treasureCardFound = true;
                    game.playTreasureCard(player, revealedCard, false, true);
                } else {
                    setAsideCards.add(revealedCard);
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
            } else {
                game.addHistory(player.getUsername(), " did not have any cards");
            }
            player.getDiscard().addAll(setAsideCards);
            for (Card c : setAsideCards) {
                game.playerDiscardedCard(player, c);
            }
        }

        return incompleteCard;
    }
}
