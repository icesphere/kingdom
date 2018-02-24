package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.List;

public class ProletariatSpecialActionHandler {

    public static IncompleteCard handleSpecialAction(Game game, Card card) {
        Player player = game.getCurrentPlayer();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Cattle Farm")) {
            Card topDeckCard = player.removeTopDeckCard();
            if (topDeckCard != null) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(topDeckCard);
                cardAction.getCards().add(topDeckCard);
                cardAction.setInstructions("Do you want to discard the top card of your deck, or put it back?");
                cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                cardAction.getChoices().add(new CardActionChoice("Put it back", "back"));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("City Planner")) {
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded 1 card");
                    game.playerDiscardedCard(player, player.getHand().get(0));
                    player.discardCardFromHand(player.getHand().get(0));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Deck.Proletariat);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(1);
                    cardAction.setInstructions("Select 1 card to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        } else if (card.getName().equals("Fruit Merchant")) {
            if (!player.getHand().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getHand());
                cardAction.setNumCards(2);
                cardAction.setInstructions("Select up to 2 cards to discard to gain a fruit token per card discarded and then click Done, or just click Done if you don't want to discard a card.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Hooligans")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            List<Player> players = game.getPlayers();
            for (Player p : players) {
                if (!game.isCurrentPlayer(p)) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(p.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                        game.addHistory(p.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!p.hasMoat() && p.getHand().size() > 3 && !p.hasLighthouse()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                        cardAction.setDeck(Deck.Proletariat);
                        cardAction.setCardName(card.getName());
                        cardAction.getCards().addAll(p.getHand());
                        cardAction.setNumCards(1);
                        cardAction.setInstructions("Choose a card from your hand. " + player.getUsername() + " will choose to place it on top of your deck or discard it.");
                        cardAction.setButtonValue("Done");
                        game.setPlayerCardAction(p, cardAction);
                    } else {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                        if (p.hasLighthouse()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (p.hasMoat()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        } else {
                            game.addHistory(p.getUsername(), " had 3 or less cards");
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Rancher")) {
            if (!player.getVictoryCards().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getVictoryCards());
                cardAction.setNumCards(1);
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Choose a victory card to reveal from your hand to gain a cattle token or +1 Buy and then click Done, or just click Done if you don't want to reveal a card.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Refugee Camp")) {
            while (player.getHand().size() < 5) {
                Card topCard = player.removeTopDeckCard();
                if (topCard == null) {
                    break;
                }
                player.addCardToHand(topCard);
            }
            game.refreshHand(player);
        } else if (card.getName().equals("Squatter")) {
            game.addHistory(player.getUsername(), " returned ", KingdomUtil.getCardWithBackgroundColor(card), " to the supply");
            game.removePlayedCard(card);
            game.playerLostCard(player, card);
            game.addToSupply(card.getCardId());
        } else if (card.getName().equals("Trainee")) {
            if (!player.getHand().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getHand());
                cardAction.setNumCards(1);
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Choose a card from your hand to return to the supply with your Trainee in order to gain an action card costing up to the combined cost of the two cards, and then click Done.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
            }
        }

        return incompleteCard;
    }
}
