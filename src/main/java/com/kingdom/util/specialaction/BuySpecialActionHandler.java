package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;

public class BuySpecialActionHandler {

    public static CardAction getCardAction(Game game, Player player, Card card) {

        if (card.getName().equals("Botanical Gardens")) {
            if (player.getCoins() >= 6) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Do you want to pay an additional 3 coins to gain another Botanical Gardens or an additional 6 coins to gain two more Botanical Gardens?");
                cardAction.getChoices().add(new CardActionChoice("3 more coins", "3"));
                cardAction.getChoices().add(new CardActionChoice("6 more coins", "6"));
                cardAction.getChoices().add(new CardActionChoice("No", "no"));
                return cardAction;
            } else if (player.getCoins() >= 3) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Do you want to pay an additional 3 coins to gain another Botanical Gardens?");
                return cardAction;
            }
        } else if (card.getName().equals("City Planner")) {
            if (player.getCoins() >= 2 && !player.getVictoryCards().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Do you want to pay an additional 2 coins to set aside a victory card from your hand?");
                return cardAction;
            }
        } else if (card.getName().equals("Farmland")) {
            if (!player.getHand().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Hinterlands);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                return cardAction;
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
        } else if (card.getName().equals("Orchard")) {
            if (player.getCoins() > 1) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Do you want to pay an additional 2 coins to gain two fruit tokens?");
                return cardAction;
            }
        } else if (card.getName().equals("Rancher")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            cardAction.setDeck(Deck.Proletariat);
            cardAction.setCardName(cardAction.getCardName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            int maxCost = player.getHand().size() * 2;
            for (Card c : game.getSupplyMap().values()) {
                if (c.isAction() && game.getCardCost(c) <= maxCost && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                return cardAction;
            }
        } else if (card.getName().equals("Squatter")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setDeck(Deck.Proletariat);
            cardAction.setCardName(card.getName());
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Do you want to return this card to the supply and have each other player gain a Squatter?");
            return cardAction;
        } else if (card.getName().equals("Shepherd")) {
            if (player.getCoins() >= 2) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Proletariat);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(card);
                cardAction.setInstructions("Do you want to pay an additional 2 coins to gain 2 cattle tokens?");
                return cardAction;
            }
        }

        return null;
    }

    public static CardAction getHagglerCardAction(Game game, Card card) {
        CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
        cardAction.setDeck(Deck.Hinterlands);
        cardAction.setCardName("Haggler");
        cardAction.setButtonValue("Done");
        cardAction.setNumCards(1);
        cardAction.setAssociatedCard(card);
        cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
        int cost = game.getCardCost(card);
        for (Card c : game.getSupplyMap().values()) {
            if (!c.isVictory() && game.getCardCost(c) < cost && (!c.isCostIncludesPotion() || card.isCostIncludesPotion()) && game.isCardInSupply(c)) {
                cardAction.getCards().add(c);
            }
        }
        if (cardAction.getCards().size() > 0) {
            return cardAction;
        }
        return null;
    }

    public static void setNobleBrigandCardAction(Game game, Player player) {
        int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
        while (playerIndex != game.getCurrentPlayerIndex()) {
            Player nextPlayer = game.getPlayers().get(playerIndex);
            if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
            } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                List<Card> cards = new ArrayList<Card>();
                Card card1 = nextPlayer.removeTopDeckCard();
                Card card2 = null;
                if (card1 != null) {
                    cards.add(card1);
                    card2 = nextPlayer.removeTopDeckCard();
                    if (card2 != null) {
                        cards.add(card2);
                    }
                }
                boolean revealedTreasure = false;
                if (!cards.isEmpty()) {
                    int numApplicableCards = 0;
                    for (Card c : cards) {
                        if (c.isTreasure()) {
                            revealedTreasure = true;
                            if (c.isSilver() || c.isGold()) {
                                numApplicableCards++;
                            }
                        }
                    }
                    game.addHistory(nextPlayer.getUsername(), " revealed ", KingdomUtil.groupCards(cards, true));
                    if (numApplicableCards == 1 || (numApplicableCards == 2 && (card1.getCardId() == card2.getCardId()))) {
                        Card applicableCard;
                        if (card1.isSilver() || card1.isGold()) {
                            applicableCard = card1;
                        } else {
                            applicableCard = card2;
                        }
                        game.getTrashedCards().add(applicableCard);
                        if (numApplicableCards == 2) {
                            player.addCardToDiscard(applicableCard);
                            game.playerDiscardedCard(nextPlayer, applicableCard);
                            game.refreshDiscard(nextPlayer);
                        }
                        game.addHistory(player.getUsername(), " trashed ", nextPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(applicableCard));
                        game.playerGainedCard(player, applicableCard);
                    } else if (numApplicableCards == 2) {
                        CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                        nextCardAction.setDeck(Deck.Hinterlands);
                        nextCardAction.setPlayerId(nextPlayer.getUserId());
                        nextCardAction.setCardName("Noble Brigand");
                        nextCardAction.getChoices().add(new CardActionChoice("Gold", "gold"));
                        nextCardAction.getChoices().add(new CardActionChoice("Silver", "silver"));
                        nextCardAction.setInstructions("Do you want to trash " + nextPlayer.getUsername() + "'s Gold or Silver?");
                        nextCardAction.getCards().addAll(cards);
                        game.setPlayerCardAction(player, nextCardAction);
                    } else {
                        for (Card c : cards) {
                            nextPlayer.addCardToDiscard(c);
                            game.playerDiscardedCard(nextPlayer, c);
                        }
                        game.refreshDiscard(nextPlayer);
                    }
                }
                if (!revealedTreasure) {
                    game.playerGainedCard(nextPlayer, game.getCopperCard());
                }
            } else {
                if (nextPlayer.hasLighthouse()) {
                    game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                } else {
                    game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                }
            }
            playerIndex = game.calculateNextPlayerIndex(playerIndex);
        }
    }
}
