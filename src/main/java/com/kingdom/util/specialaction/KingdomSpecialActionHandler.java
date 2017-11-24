package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KingdomSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card, boolean repeatedAction){
        Map<Integer, Card> supplyMap = game.getSupplyMap();
        List<Player> players = game.getPlayers();
        int currentPlayerId = game.getCurrentPlayerId();
        int currentPlayerIndex = game.getCurrentPlayerIndex();
        IncompleteCard incompleteCard = null;

        switch (card.getName()) {
            case "Adventurer": {
                Player player = game.getCurrentPlayer();
                int treasureCardsFound = 0;
                List<Card> setAsideCards = new ArrayList<>();
                while (treasureCardsFound < 2) {
                    Card revealedCard = player.removeTopDeckCard();
                    if (revealedCard == null) {
                        break;
                    }
                    game.addHistory(player.getUsername(), " revealed ", KingdomUtil.getArticleWithCardName(revealedCard));
                    if (revealedCard.isTreasure()) {
                        player.addCardToHand(revealedCard);
                        game.addHistory(player.getUsername(), " got a ", revealedCard.getName(), " from the ", KingdomUtil.getWordWithBackgroundColor("Adventurer", Card.ACTION_COLOR), " action");
                        treasureCardsFound++;
                    } else {
                        setAsideCards.add(revealedCard);
                    }
                }
                player.getDiscard().addAll(setAsideCards);
                for (Card c : setAsideCards) {
                    game.playerDiscardedCard(player, c);
                }
                game.refreshHand(player);
                break;
            }
            case "Artisan": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following cards to gain to your hand and then click Done.");
                for (Card c : supplyMap.values()) {
                    if (game.getCardCost(c) <= 5 && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                        cardAction.getCards().add(c);
                    }
                }
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Bureaucrat": {
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
                for (Player player : game.getPlayers()) {
                    if (player.getUserId() != currentPlayerId) {
                        if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                        } else if (!player.hasMoat() && player.hasVictoryCard() && !player.hasLighthouse()) {
                            List<Card> victoryCards = KingdomUtil.uniqueCardList(player.getVictoryCards());
                            if (victoryCards.size() == 1) {
                                incompleteCard.setPlayerActionCompleted(player.getUserId());
                                player.putCardFromHandOnTopOfDeck(player.getVictoryCards().get(0));
                                game.refreshHand(player);
                                game.addHistory(player.getUsername(), " added 1 Victory card on top of ", player.getPronoun(), " deck");
                            } else {
                                CardAction cardAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
                                cardAction.setDeck(Card.DECK_KINGDOM);
                                cardAction.setCardName(card.getName());
                                cardAction.setCards(victoryCards);
                                cardAction.setNumCards(1);
                                cardAction.setInstructions("Select a card to be placed on top of your deck and then click Done.");
                                cardAction.setButtonValue("Done");
                                game.setPlayerCardAction(player, cardAction);
                            }
                        } else {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            if (player.hasLighthouse()) {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                            } else if (player.hasMoat()) {
                                game.addHistory(player.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                            } else {
                                game.addHistory(player.getUsername(), " did not have a Victory card");
                            }
                        }
                    }
                }
                incompleteCard.allActionsSet();
                Player player = game.getCurrentPlayer();
                game.playerGainedCardToTopOfDeck(player, game.getSilverCard());
                break;
            }
            case "Cellar": {
                Player player = game.getCurrentPlayer();
                if (player.getHand().size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
                    cardAction.setDeck(Card.DECK_KINGDOM);
                    cardAction.setCardName(card.getName());
                    cardAction.getCards().addAll(player.getHand());
                    cardAction.setNumCards(player.getHand().size());
                    cardAction.setInstructions("Discard any number of cards. +1 Card per card discarded. Select the Cards you want to discard and then click Done.");
                    cardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Chancellor": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Would you like to put your deck into your discard pile?");
                game.setPlayerCardAction(player, cardAction);
                break;
            }
            case "Chapel": {
                Player player = game.getCurrentPlayer();
                if (player.getHand().size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                    cardAction.setDeck(Card.DECK_KINGDOM);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(4);
                    cardAction.setInstructions("Trash up to 4 cards.");
                    cardAction.setCards(player.getHand());
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Council Room":
                for (Player player : players) {
                    if (player.getUserId() != currentPlayerId) {
                        player.drawCardAndAddToHand();
                        game.refreshHand(player);
                    }
                }
                break;
            case "Feast": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following cards and then click Done.");
                for (Card c : supplyMap.values()) {
                    if (game.getCardCost(c) <= 5 && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                        cardAction.getCards().add(c);
                    }
                }
                if (!repeatedAction) {
                    game.removePlayedCard(card);
                    game.getTrashedCards().add(card);
                    game.playerLostCard(player, card);
                    game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " was trashed");
                }
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Library": {
                Player player = game.getCurrentPlayer();
                while (player.getHand().size() < 7) {
                    Card topCard = player.removeTopDeckCard();
                    if (topCard == null) {
                        break;
                    }
                    if (topCard.isAction()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                        cardAction.setDeck(Card.DECK_KINGDOM);
                        cardAction.setCardName(card.getName());
                        cardAction.getCards().add(topCard);
                        cardAction.setInstructions("Do you want to set aside this action card?");
                        game.setPlayerCardAction(player, cardAction);
                        break;
                    } else {
                        player.addCardToHand(topCard);
                    }
                }
                game.refreshHand(player);
                break;
            }
            case "Militia":
                incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
                for (Player player : players) {
                    if (player.getUserId() != currentPlayerId) {
                        if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(player.getUserId())) {
                            incompleteCard.setPlayerActionCompleted(player.getUserId());
                            game.addHistory(player.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                        } else if (!player.hasMoat() && player.getHand().size() > 3 && !player.hasLighthouse()) {
                            CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND);
                            cardAction.setDeck(Card.DECK_KINGDOM);
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
                break;
            case "Mine": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a treasure card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getTreasureCards()));
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                } else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("You don't have any treasure cards to trash."));
                }
                break;
            }
            case "Moneylender": {
                Player player = game.getCurrentPlayer();
                for (Card handCard : player.getHand()) {
                    if (handCard.getName().equals("Copper")) {
                        player.removeCardFromHand(handCard);
                        game.getTrashedCards().add(handCard);
                        game.playerLostCard(player, handCard);
                        player.addCoins(3);
                        game.addHistory(player.getUsername(), " trashed ", KingdomUtil.getArticleWithCardName(handCard), " and gained 3 coins");
                        game.refreshHand(player);
                        game.refreshDiscard(player);
                        game.refreshCardsBought(player);
                        break;
                    }
                }
                break;
            }
            case "Remodel": {
                Player player = game.getCurrentPlayer();
                if (player.getHand().size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                    cardAction.setDeck(Card.DECK_KINGDOM);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(1);
                    cardAction.setInstructions("Select a card to trash.");
                    cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Spy": {
                incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
                Player currentPlayer = game.getCurrentPlayer();
                if (currentPlayer.lookAtTopDeckCard() != null) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                    cardAction.setDeck(Card.DECK_KINGDOM);
                    cardAction.setCardName(card.getName());
                    cardAction.setInstructions("You are spying the top card of your deck. Do you want to discard it?");
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
                            nextCardAction.setDeck(Card.DECK_KINGDOM);
                            nextCardAction.setCardName(card.getName());
                            nextCardAction.setInstructions("You are spying the top card of " + nextPlayer.getUsername() + "'s deck. Do you want to discard it?");
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
                }
                break;
            }
            case "Thief": {
                incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
                Player currentPlayer = game.getCurrentPlayer();
                int nextPlayerIndex = game.getNextPlayerIndex();
                while (nextPlayerIndex != currentPlayerIndex) {
                    Player nextPlayer = players.get(nextPlayerIndex);
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                        game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                        nextCardAction.setDeck(Card.DECK_KINGDOM);
                        nextCardAction.setPlayerId(nextPlayer.getUserId());
                        nextCardAction.setCardName(card.getName());
                        Card card1 = nextPlayer.removeTopDeckCard();
                        Card card2 = nextPlayer.removeTopDeckCard();
                        String instructions;
                        if (card1 != null) {
                            instructions = "These are the top two cards from " + nextPlayer.getUsername() + "'s deck.";
                            if (!card1.isTreasure()) {
                                card1.setDisableSelect(true);
                            }
                            nextCardAction.getCards().add(card1);
                            if (card2 != null) {
                                if (!card2.isTreasure()) {
                                    card2.setDisableSelect(true);
                                }
                                nextCardAction.getCards().add(card2);
                            }
                        } else {
                            instructions = nextPlayer.getUsername() + " did not have any cards to draw.";
                        }
                        if ((card1 != null && card1.isTreasure()) || (card2 != null && card2.isTreasure())) {
                            instructions += " Select a treasure card to trash and then click Done.";
                            nextCardAction.setButtonValue("Done");
                            nextCardAction.setNumCards(1);
                        } else {
                            instructions += " There are no treasure cards to trash. Click Continue.";
                            nextCardAction.setButtonValue("Continue");
                            nextCardAction.setNumCards(0);
                        }
                        nextCardAction.setInstructions(instructions);
                        incompleteCard.getExtraCardActions().add(nextCardAction);
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
                }
                break;
            }
            case "Throne Room": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following actions to play twice and then click Done.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getActionCards()));
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
            case "Witch": {
                int nextPlayerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
                while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                    Player nextPlayer = players.get(nextPlayerIndex);
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                        game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        if (game.isCardInSupply(Card.CURSE_ID)) {
                            game.playerGainedCard(nextPlayer, game.getCurseCard());
                            game.refreshDiscard(nextPlayer);
                        }
                    } else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                    nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex);
                }
                break;
            }
            case "Workshop": {
                Player player = game.getCurrentPlayer();
                CardAction cardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                cardAction.setDeck(Card.DECK_KINGDOM);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select one of the following cards to gain and then click Done.");
                for (Card c : supplyMap.values()) {
                    if (game.getCardCost(c) <= 4 && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                        cardAction.getCards().add(c);
                    }
                }
                if (cardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, cardAction);
                }
                break;
            }
        }

        return incompleteCard;
    }
}