package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;

public class HinterlandsSpecialActionHandler {

    public static IncompleteCard handleSpecialAction(Game game, Card card) {
        Player player = game.getCurrentPlayer();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Cartographer")) {
            List<Card> cards = new ArrayList<Card>();
            while (cards.size() < 4) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    break;
                }
                cards.add(c);
            }
            if (!cards.isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO);
                cardAction.setDeck(Card.DECK_HINTERLANDS);
                cardAction.setCardName(card.getName());
                cardAction.setCards(cards);
                cardAction.setNumCards(cards.size());
                cardAction.setInstructions("Select the cards you want to discard and then click Done.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Crossroads")) {
            if (!player.getHand().isEmpty()) {
                game.addHistory(player.getUsername(), "'s hand contains ", KingdomUtil.groupCards(player.getHand(), true));
                if (!player.getVictoryCards().isEmpty()) {
                    game.addHistory(player.getUsername(), " gained +", KingdomUtil.getPlural(player.getVictoryCards().size(), "card"));
                    player.drawCards(player.getVictoryCards().size());
                    game.refreshAllPlayersCardsBought();
                }
                if (game.getCrossroadsPlayed() == 1) {
                    game.addHistory(player.getUsername(), " gained +3 Actions");
                    player.addActions(3);
                    game.refreshAllPlayersCardsPlayed();
                }
            }
        } else if (card.getName().equals("Develop")) {
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Card.DECK_HINTERLANDS);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
        } else if (card.getName().equals("Duchess")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, true);
            for (Player p : game.getPlayers()) {
                Card topDeckCard = p.removeTopDeckCard();
                if (topDeckCard != null) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName(card.getName());
                    cardAction.setAssociatedCard(topDeckCard);
                    cardAction.getCards().add(topDeckCard);
                    cardAction.setInstructions("Do you want to discard the top card of your deck, or put it back?");
                    cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                    cardAction.getChoices().add(new CardActionChoice("Put it back", "back"));
                    game.setPlayerCardAction(p, cardAction);
                } else {
                    incompleteCard.setPlayerActionCompleted(p.getUserId());
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Embassy")) {
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded 1 card");
                    game.playerDiscardedCard(player, player.getHand().get(0));
                    player.discardCardFromHand(player.getHand().get(0));
                } else {
                    int cardsToDiscard = 3;
                    if (player.getHand().size() < 3) {
                        cardsToDiscard = player.getHand().size();
                    }
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(cardsToDiscard);
                    cardAction.setInstructions("Select " + KingdomUtil.getPlural(cardsToDiscard, "card") + " to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        } else if (card.getName().equals("Inn")) {
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded 1 card");
                    game.playerDiscardedCard(player, player.getHand().get(0));
                    player.discardCardFromHand(player.getHand().get(0));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(2);
                    cardAction.setInstructions("Select 2 cards to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        } else if (card.getName().equals("Jack of all Trades")) {
            if (game.isCardInSupply(Card.SILVER_ID)) {
                game.playerGainedCard(player, game.getSilverCard());
            }
            Card topDeckCard = player.removeTopDeckCard();
            if (topDeckCard != null) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Card.DECK_HINTERLANDS);
                cardAction.setCardName(card.getName());
                cardAction.setAssociatedCard(topDeckCard);
                cardAction.getCards().add(topDeckCard);
                cardAction.setInstructions("Do you want to discard the top card of your deck, or put it back?");
                cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                cardAction.getChoices().add(new CardActionChoice("Put it back", "back"));
                game.setPlayerCardAction(player, cardAction);
            } else {
                while (player.getHand().size() < 5) {
                    Card topCard = player.removeTopDeckCard();
                    if (topCard == null) {
                        break;
                    }
                    player.addCardToHand(topCard);
                }
                game.refreshHand(player);
                List<Card> cards = new ArrayList<Card>();
                for (Card c : player.getHand()) {
                    if (!c.isTreasure()) {
                        cards.add(c);
                    }
                }
                if (!cards.isEmpty()) {
                    CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                    trashCardAction.setDeck(Card.DECK_HINTERLANDS);
                    trashCardAction.setCardName(card.getName());
                    trashCardAction.setNumCards(1);
                    trashCardAction.getCards().addAll(KingdomUtil.uniqueCardList(cards));
                    trashCardAction.setInstructions("Select a card to trash from your hand and then click Done, or just click Done if you don't want to trash a card.");
                    trashCardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, trashCardAction);
                }
            }
        } else if (card.getName().equals("Mandarin")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName(card.getName());
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a card from your hand to put on top of your deck.");
            if (!cardAction.getCards().isEmpty()) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Margrave")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player p : game.getPlayers()) {
                if (!game.isCurrentPlayer(p)) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(p.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                        game.addHistory(p.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!p.hasMoat() && !p.hasLighthouse()) {
                        p.drawCards(1);
                        game.addHistory(p.getUsername(), " drew 1 card");
                        game.refreshHand(p);
                        if (p.getHand().size() > 3) {
                            CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND);
                            cardAction.setDeck(Card.DECK_HINTERLANDS);
                            cardAction.setCardName(card.getName());
                            cardAction.getCards().addAll(p.getHand());
                            cardAction.setNumCards(3);
                            cardAction.setInstructions("Discard down to 3 cards. Select the Cards you want to discard and then click Done.");
                            cardAction.setButtonValue("Done");
                            game.setPlayerCardAction(p, cardAction);
                        } else {
                            incompleteCard.setPlayerActionCompleted(p.getUserId());
                            game.addHistory(p.getUsername(), " had 3 or less cards");
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                        if (p.hasLighthouse()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (p.hasMoat()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Noble Brigand")) {
            BuySpecialActionHandler.setNobleBrigandCardAction(game, player);
        } else if (card.getName().equals("Oasis")) {
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded 1 card");
                    game.playerDiscardedCard(player, player.getHand().get(0));
                    player.discardCardFromHand(player.getHand().get(0));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(1);
                    cardAction.setInstructions("Select 1 card to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        } else if (card.getName().equals("Oracle")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, true);
            int playersProcessed = 0;
            int playerIndex = game.getCurrentPlayerIndex();
            while (playersProcessed < game.getNumPlayers()) {
                Player p = game.getPlayers().get(playerIndex);
                List<Card> topCards = new ArrayList<Card>();
                Card firstTopCard = p.removeTopDeckCard();
                if (firstTopCard != null) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                    cardAction.setDeck(Card.DECK_HINTERLANDS);
                    cardAction.setCardName(card.getName());
                    cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
                    cardAction.setPlayerId(p.getUserId());

                    topCards.add(firstTopCard);
                    Card secondTopCard = p.removeTopDeckCard();
                    if (secondTopCard != null) {
                        cardAction.getChoices().add(new CardActionChoice("Put them back", "back"));
                        topCards.add(secondTopCard);
                        game.addHistory(p.getUsername(), " revealed ", KingdomUtil.groupCards(topCards, true));
                        if (game.isCurrentPlayer(p)) {
                            cardAction.setInstructions("Do you want to discard the top two cards of your deck, or put them back?");
                        } else {
                            cardAction.setInstructions("Do you want to discard the top two cards of " + p.getUsername() + "'s deck, or put them back?");
                        }
                    } else {
                        cardAction.getChoices().add(new CardActionChoice("Put it back", "back"));
                        game.addHistory(p.getUsername(), " revealed ", KingdomUtil.getCardWithBackgroundColor(firstTopCard, true));
                        if (game.isCurrentPlayer(p)) {
                            cardAction.setInstructions("Do you want to discard the top card of your deck, or put it back?");
                        } else {
                            cardAction.setInstructions("Do you want to discard the top card of " + p.getUsername() + "'s deck, or put it back?");
                        }
                    }
                    cardAction.getCards().addAll(topCards);
                    incompleteCard.getExtraCardActions().add(cardAction);
                } else {
                    game.addHistory(p.getUsername(), " did not have any cards to reveal");
                    incompleteCard.setPlayerActionCompleted(p.getUserId());
                }

                playerIndex = game.calculateNextPlayerIndex(playerIndex);
                playersProcessed++;
            }

            if (!incompleteCard.getExtraCardActions().isEmpty()) {
                CardAction cardAction = incompleteCard.getExtraCardActions().remove();
                game.setPlayerCardAction(player, cardAction);
            } else {
                player.drawCards(2);
            }

            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Spice Merchant")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName(card.getName());
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getTreasureCards()));
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a treasure card to trash and then click Done, or just click Done if you don't want to trash a card.");
            cardAction.setButtonValue("Done");
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Stables")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
            cardAction.setDeck(Card.DECK_HINTERLANDS);
            cardAction.setCardName(card.getName());
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getTreasureCards()));
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a treasure card to discard and then click Done, or just click Done if you don't want to discard a card.");
            cardAction.setButtonValue("Done");
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Trader")) {
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Card.DECK_HINTERLANDS);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards");
            }
        }

        return incompleteCard;
    }
}
