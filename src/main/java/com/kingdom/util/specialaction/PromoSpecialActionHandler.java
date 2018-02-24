package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;

public class PromoSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        List<Player> players = game.getPlayers();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Black Market")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction;
            List<Card> cards = new ArrayList<Card>();
            Card blackMarketCard = game.removeNextBlackMarketCard();
            while (blackMarketCard != null && cards.size() < 3) {
                cards.add(blackMarketCard);
                blackMarketCard = game.removeNextBlackMarketCard();
            }
            boolean canAffordAny = false;
            if (cards.size() > 0) {
                game.addHistory("Black Market cards: " + KingdomUtil.getCardNames(cards));
                game.setBlackMarketCardsToBuy(cards);
                int coins = player.getCoins();
                int potions = player.getPotions();
                if (game.isPlayTreasureCards()) {
                    coins += player.getCoinsInHand();
                    potions += player.getPotionsInHand();
                }
                for (Card c : cards) {
                    if (coins >= game.getCardCostBuyPhase(c) && (!c.isCostIncludesPotion() || potions > 0)) {
                        canAffordAny = true;
                        break;
                    }
                }
                if (canAffordAny) {
                    cardAction = new CardAction(CardAction.TYPE_YES_NO);
                    cardAction.setDeck(Deck.Promo);
                    StringBuffer instructions = new StringBuffer("You have ");
                    instructions.append(KingdomUtil.getPlural(coins, "coin"));
                    if (game.isUsePotions()) {
                        instructions.append(" and ").append(KingdomUtil.getPlural(potions, "potion"));
                    }
                    instructions.append(". Do you want to buy one of these cards?");
                    cardAction.setInstructions(instructions.toString());
                } else {
                    cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    cardAction.setDeck(Deck.Promo);
                    cardAction.setHideOnSelect(true);
                    cardAction.setNumCards(cards.size());
                    cardAction.setButtonValue("Done");
                    cardAction.setInstructions("You don't have enough coins to buy any of these black market cards. Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)");
                }
                cardAction.setCards(cards);
            } else {
                cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Deck.Promo);
                cardAction.setNumCards(0);
                cardAction.setButtonValue("Continue");
                cardAction.setInstructions("There are no more black market cards to buy. Click Continue.");
            }
            cardAction.setCardName(card.getName());
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Envoy")) {
            Player player = game.getCurrentPlayer();
            Player playerToLeft = players.get(game.getNextPlayerIndex());
            List<Card> cards = new ArrayList<Card>();
            boolean hasMoreCards = true;
            while (hasMoreCards && cards.size() < 5) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    cards.add(c);
                }
            }
            if (cards.size() > 0) {
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, playerToLeft.getUserId());
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Deck.Promo);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setCards(cards);
                cardAction.setInstructions("Select the card you want " + player.getUsername() + " to discard and then click Done.");
                game.setPlayerCardAction(playerToLeft, cardAction);
                incompleteCard.allActionsSet();
            }
        } else if (card.getName().equals("Governor")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Deck.Promo);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Choose one: you get the version in parentheses. Each player gets +1 (+3) cards; or each player gains a Silver (Gold); or each player may trash a card from his hand and gain a card costing exactly 1 (2) more.");
            cardAction.getChoices().add(new CardActionChoice("Cards", "cards"));
            cardAction.getChoices().add(new CardActionChoice("Silver and Gold", "money"));
            cardAction.getChoices().add(new CardActionChoice("Trash Card", "trash"));
            game.setPlayerCardAction(player, cardAction);
        }

        return incompleteCard;
    }
}