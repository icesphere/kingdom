package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeasideSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card, boolean repeatedAction) {

        Map<Integer, Card> supplyMap = game.getSupplyMap();
        Map<Integer, Integer> supply = game.getSupply();
        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Ambassador")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select the card that you want other players to receive and then click Done.");
                if (!game.getBlackMarketCards().isEmpty()) {
                    cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                } else {
                    for (Card c : KingdomUtil.uniqueCardList(player.getHand())) {
                        if (supply.get(c.getCardId()) != null) {
                            cardAction.getCards().add(c);
                        }
                    }
                }
                if (!cardAction.getCards().isEmpty()) {
                    game.setPlayerCardAction(player, cardAction);
                } else {
                    game.addHistory(player.getUsername(), " did not have any cards from the supply in ", player.getPronoun(), " hand");
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
        } else if (card.getName().equals("Cutpurse")) {
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && !player.hasLighthouse()) {
                        boolean hasCopper = false;
                        for (Card handCard : player.getHand()) {
                            if (handCard.getName().equals("Copper")) {
                                hasCopper = true;
                                player.discardCardFromHand(handCard);
                                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Cutpurse", Card.ACTION_COLOR), " discarded ", player.getUsername(), "'s ", KingdomUtil.getWordWithBackgroundColor("Copper", Card.TREASURE_COLOR));
                                game.refreshHand(player);
                                game.refreshDiscard(player);
                                break;
                            }
                        }
                        if (!hasCopper) {
                            game.addHistory(player.getUsername(), " did not have a ", KingdomUtil.getWordWithBackgroundColor("Copper", Card.TREASURE_COLOR));
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
        } else if (card.getName().equals("Embargo")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select the card you want to place an embargo token on and then click Done.");
            for (Card c : supplyMap.values()) {
                if (game.isCardInSupply(c)) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
            if (!repeatedAction) {
                game.removePlayedCard(card);
                game.getTrashedCards().add(card);
                game.playerLostCard(player, card);
            }
        } else if (card.getName().equals("Explorer")) {
            Player player = game.getCurrentPlayer();
            boolean hasProvince = false;
            for (Card c : player.getHand()) {
                if (c.getCardId() == Card.PROVINCE_ID) {
                    hasProvince = true;
                    break;
                }
            }
            if (hasProvince) {
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Do you want to reveal a Province and gain a Gold into your hand, or do you want to gain a silver into your hand?");
                cardAction.getChoices().add(new CardActionChoice("Gold", "gold"));
                cardAction.getChoices().add(new CardActionChoice("Silver", "silver"));
                game.setPlayerCardAction(player, cardAction);
            } else {
                if (game.isCardInSupply(Card.SILVER_ID)) {
                    game.playerGainedCardToHand(player, game.getSilverCard());
                    game.refreshHand(player);
                }
            }
        } else if (card.getName().equals("Ghost Ship")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player player : players) {
                if (player.getUserId() != currentPlayerId) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                        game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!player.hasMoat() && player.getHand().size() >= 4 && !player.hasLighthouse()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
                        cardAction.setDeck(Card.DECK_SEASIDE);
                        cardAction.setCardName(card.getName());
                        cardAction.getCards().addAll(player.getHand());
                        cardAction.setNumCards(player.getHand().size() - 3);
                        cardAction.setInstructions("Select down to 3 cards. Select the Cards you want to go on top of your deck and then click Done.");
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
        } else if (card.getName().equals("Haven")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a card to set aside until your next turn.");
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Island")) {
            Player player = game.getCurrentPlayer();
            if (!repeatedAction) {
                game.removePlayedCard(card);
                player.getIslandCards().add(card);
            }
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a card to be added to your island and then click Done.");
            cardAction.setButtonValue("Done");
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.refreshHandArea(player);
            }
        } else if (card.getName().equals("Lookout")) {
            Player player = game.getCurrentPlayer();
            List<Card> cards = new ArrayList<Card>();
            boolean hasMoreCards = true;
            while (hasMoreCards && cards.size() < 3) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    cards.add(c);
                }
            }
            if (cards.size() == 1) {
                game.getTrashedCards().add(cards.get(0));
                game.playerLostCard(player, cards.get(0));
                game.addHistory("The ", KingdomUtil.getWordWithBackgroundColor("Lookout", Card.ACTION_COLOR), " trashed ", player.getUsername(), "'s ", cards.get(0).getName());
            } else if (cards.size() > 0) {
                game.refreshHand(player);
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setHideOnSelect(true);
                cardAction.setNumCards(cards.size());
                cardAction.setCardName(card.getName());
                cardAction.setCards(cards);
                cardAction.setButtonValue("Done");
                String instructions = "Click on the card you want to trash, then click on the card you want to discard, ";
                if (cards.size() == 3) {
                    instructions += "then click on the card you want to put on top of your deck, ";
                }
                instructions += "then click Done.";
                cardAction.setInstructions(instructions);
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to draw");
            }
        } else if (card.getName().equals("Native Village")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Do you want to put your top deck card on your Native Village, or put all the cards from your Native Village into your hand?");
            cardAction.getChoices().add(new CardActionChoice("Top Deck Card", "card"));
            cardAction.getChoices().add(new CardActionChoice("Add Cards To Hand", "hand"));
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Navigator")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Do you want to discard these cards, or put them back on top of your deck?");
            cardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
            cardAction.getChoices().add(new CardActionChoice("Put Back", "deck"));
            List<Card> topDeckCards = new ArrayList<Card>();
            for (int i = 0; i < 5; i++) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    break;
                }
                topDeckCards.add(c);
            }
            if (topDeckCards.size() > 0) {
                cardAction.getCards().addAll(topDeckCards);
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in " + player.getPronoun(), " deck");
            }
        } else if (card.getName().equals("Pearl Diver")) {
            Player player = game.getCurrentPlayer();
            Card bottomCard = player.lookAtBottomDeckCard();
            if (bottomCard != null) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.getCards().add(bottomCard);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("This is the bottom card from your deck, do you want to put it on top of your deck?");
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in " + player.getPronoun(), " deck");
            }
        } else if (card.getName().equals("Pirate Ship")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_SEASIDE);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Do you want to attack other players and try to get a Pirate Ship Coin, or do you want to get " + KingdomUtil.getPlural(player.getPirateShipCoins(), "Coin") + " from your Pirate Ship Coins?");
            cardAction.getChoices().add(new CardActionChoice("Attack", "attack"));
            cardAction.getChoices().add(new CardActionChoice("Use Coins", "coins"));
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Salvager")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Sea Hag")) {
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
                                game.playerGainedCardToTopOfDeck(player, game.getCurseCard());
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
        } else if (card.getName().equals("Smugglers")) {
            Player player = game.getCurrentPlayer();
            CardAction cardAction;
            List<Card> cards = new ArrayList<Card>();
            for (Card c : game.getSmugglersCards()) {
                if (game.isCardInSupply(c) && !c.isCostIncludesPotion()) {
                    cards.add(c);
                }
            }
            if (cards.size() > 0) {
                cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setCards(KingdomUtil.uniqueCardList(cards));
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            } else {
                cardAction = new CardAction(CardAction.TYPE_INFO);
                cardAction.setDeck(Card.DECK_SEASIDE);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("The player to your right did not gain any cards costing 6 or less that are still in the supply.");
                cardAction.setButtonValue("Close");
            }
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Tactician")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                for (Card c : player.getHand()) {
                    game.playerDiscardedCard(player, c);
                }
                player.discardHand();
                player.setTacticianBonus(true);
            }
        } else if (card.getName().equals("Treasure Map")) {
            Player player = game.getCurrentPlayer();
            Card secondTreasureMap = null;
            for (Card c : player.getHand()) {
                if (c.getName().equals("Treasure Map")) {
                    secondTreasureMap = c;
                    break;
                }
            }
            game.removePlayedCard(card);
            game.getTrashedCards().add(card);
            game.playerLostCard(player, card);
            if (secondTreasureMap != null) {
                player.removeCardFromHand(secondTreasureMap);
                game.getTrashedCards().add(secondTreasureMap);
                game.playerLostCard(player, secondTreasureMap);
                int goldCardsGained = 0;
                for (int i = 0; i < 4; i++) {
                    if (game.isCardInSupply(Card.GOLD_ID)) {
                        goldCardsGained++;
                        game.playerGainedCardToTopOfDeck(player, game.getGoldCard());
                    } else {
                        break;
                    }
                }
                game.addHistory(player.getUsername(), " trashed two ", KingdomUtil.getWordWithBackgroundColor("Treasure Maps", Card.ACTION_COLOR), " and gained " + goldCardsGained + " " + KingdomUtil.getCardWithBackgroundColor(game.getGoldCard()) + " cards on top of ", player.getPronoun(), " deck");
            } else {
                game.addHistory(player.getUsername(), " trashed a ", KingdomUtil.getWordWithBackgroundColor("Treasure Map", Card.ACTION_COLOR));
            }
        } else if (card.getName().equals("Warehouse")) {
            Player player = game.getCurrentPlayer();
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded 1 card");
                    Card cardToDiscard = player.getHand().get(0);
                    player.discardCardFromHand(cardToDiscard);
                    game.playerDiscardedCard(player, cardToDiscard);
                } else {
                    int cardsToDiscard = 3;
                    if (player.getHand().size() < 3) {
                        cardsToDiscard = player.getHand().size();
                    }
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Card.DECK_SEASIDE);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(cardsToDiscard);
                    cardAction.setInstructions("Select " + KingdomUtil.getPlural(cardsToDiscard, "card") + " to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        }

        return incompleteCard;
    }
}