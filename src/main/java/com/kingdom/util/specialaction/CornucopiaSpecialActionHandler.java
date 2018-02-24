package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CornucopiaSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        List<Player> players = game.getPlayers();
        Player player = game.getCurrentPlayer();
        IncompleteCard incompleteCard = null;

        if (card.getName().equals("Bag of Gold")) {
            if (game.getSupply().get(Card.GOLD_ID) > 0) {
                game.playerGainedCardToTopOfDeck(player, game.getGoldCard());
            } else {
                game.addHistory("The supply did not have any ", KingdomUtil.getCardWithBackgroundColor(game.getGoldCard()));
            }
        } else if (card.getName().equals("Farming Village")) {
            List<Card> revealedCards = new ArrayList<Card>();
            List<Card> setAsideCards = new ArrayList<Card>();
            Card topDeckCard = null;
            boolean foundActionOrTreasure = false;
            while (!foundActionOrTreasure) {
                topDeckCard = player.removeTopDeckCard();
                if (topDeckCard == null) {
                    break;
                }
                revealedCards.add(topDeckCard);
                if (topDeckCard.isAction() || topDeckCard.isTreasure()) {
                    foundActionOrTreasure = true;
                    player.addCardToHand(topDeckCard);
                } else {
                    setAsideCards.add(topDeckCard);
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                player.getDiscard().addAll(setAsideCards);
                for (Card c : setAsideCards) {
                    game.playerDiscardedCard(player, c);
                }
                if (foundActionOrTreasure) {
                    game.addHistory(player.getUsername(), " added ", KingdomUtil.getArticleWithCardName(topDeckCard), " to ", player.getPronoun(), " hand");
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to draw");
            }
        } else if (card.getName().equals("Followers")) {
            if (game.getSupply().get(Card.ESTATE_ID) > 0) {
                game.playerGainedCard(player, game.getEstateCard());
            }
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            int nextPlayerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = players.get(nextPlayerIndex);
                if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                    incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                    game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                    int cursesInSupply = game.getSupply().get(Card.CURSE_ID);
                    if (cursesInSupply > 0) {
                        game.playerGainedCard(nextPlayer, game.getCurseCard());
                        game.refreshDiscard(nextPlayer);
                    }
                    if (nextPlayer.getHand().size() > 3) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_DOWN_TO_FROM_HAND);
                        cardAction.setDeck(Deck.Cornucopia);
                        cardAction.setCardName(card.getName());
                        cardAction.getCards().addAll(nextPlayer.getHand());
                        cardAction.setNumCards(3);
                        cardAction.setInstructions("Discard down to 3 cards. Select the Cards you want to discard and then click Done.");
                        cardAction.setButtonValue("Done");
                        game.setPlayerCardAction(nextPlayer, cardAction);
                    } else {
                        incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                        game.addHistory(nextPlayer.getUsername(), " had 3 or less cards");
                    }
                } else {
                    incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                    if (nextPlayer.hasLighthouse()) {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                    } else if (nextPlayer.hasMoat()) {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                    }
                }
                nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex);
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Fortune Teller")) {
            for (Player p : players) {
                if (p.getUserId() != player.getUserId()) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(p.getUserId())) {
                        game.addHistory(p.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!p.hasMoat() && !p.hasLighthouse()) {
                        List<Card> revealedCards = new ArrayList<Card>();
                        List<Card> setAsideCards = new ArrayList<Card>();
                        boolean foundVictoryOrCurse = false;
                        Card topDeckCard = null;
                        while (!foundVictoryOrCurse) {
                            topDeckCard = p.removeTopDeckCard();
                            if (topDeckCard == null) {
                                break;
                            }
                            revealedCards.add(topDeckCard);
                            if (topDeckCard.isVictory() || topDeckCard.isCurseOnly()) {
                                foundVictoryOrCurse = true;
                                p.addCardToTopOfDeck(topDeckCard);
                            } else {
                                setAsideCards.add(topDeckCard);
                            }
                        }
                        if (!revealedCards.isEmpty()) {
                            game.addHistory(p.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                            p.getDiscard().addAll(setAsideCards);
                            for (Card c : setAsideCards) {
                                game.playerDiscardedCard(p, c);
                            }
                            game.refreshDiscard(p);
                            if (foundVictoryOrCurse) {
                                game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " added ", KingdomUtil.getArticleWithCardName(topDeckCard), " on top of ", p.getUsername(), "'s deck");
                            }
                        } else {
                            game.addHistory(p.getUsername(), " did not have any cards to draw");
                        }
                    } else {
                        if (p.hasLighthouse()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (p.hasMoat()) {
                            game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
        } else if (card.getName().equals("Hamlet")) {
            if (player.getHand().isEmpty()) {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            } else {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Cornucopia);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Do you want to discard a card from your hand to gain +1 Action?");
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Harvest")) {
            boolean hasMoreCards = true;
            List<Card> revealedCards = new ArrayList<Card>();
            Set<String> cardNames = new HashSet<String>();
            while (hasMoreCards && revealedCards.size() < 4) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    revealedCards.add(c);
                    cardNames.add(c.getName());
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                player.getDiscard().addAll(revealedCards);
                for (Card c : revealedCards) {
                    game.playerDiscardedCard(player, c);
                }
                player.addCoins(cardNames.size());
                game.refreshAllPlayersCardsBought();
                game.addHistory(player.getUsername(), " gained +", KingdomUtil.getPlural(cardNames.size(), "Coin"), " from ", KingdomUtil.getCardWithBackgroundColor(card));
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to reveal");
            }
        } else if (card.getName().equals("Horse Traders")) {
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.addHistory(player.getUsername(), " discarded ", KingdomUtil.getArticleWithCardName(player.getHand().get(0)));
                    player.discardCardFromHand(player.getHand().get(0));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Deck.Cornucopia);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(2);
                    cardAction.setInstructions("Select two cards to discard.");
                    cardAction.setCards(player.getHand());
                    game.setPlayerCardAction(player, cardAction);
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to discard");
            }
        } else if (card.getName().equals("Hunting Party")) {
            Set<String> cardNames = new HashSet<String>();
            if (!player.getHand().isEmpty()) {
                game.addHistory(player.getUsername(), "'s current hand contains ", KingdomUtil.groupCards(player.getHand(), true));
                for (Card c : player.getHand()) {
                    cardNames.add(c.getName());
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
            List<Card> revealedCards = new ArrayList<Card>();
            List<Card> setAsideCards = new ArrayList<Card>();
            Card topDeckCard = null;
            boolean foundCard = false;
            while (!foundCard) {
                topDeckCard = player.removeTopDeckCard();
                if (topDeckCard == null) {
                    break;
                }
                revealedCards.add(topDeckCard);
                if (!cardNames.contains(topDeckCard.getName())) {
                    foundCard = true;
                    player.addCardToHand(topDeckCard);
                } else {
                    setAsideCards.add(topDeckCard);
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                player.getDiscard().addAll(setAsideCards);
                for (Card c : setAsideCards) {
                    game.playerDiscardedCard(player, c);
                }
                if (foundCard) {
                    game.addHistory(player.getUsername(), " added ", KingdomUtil.getArticleWithCardName(topDeckCard), " to ", player.getPronoun(), " hand");
                } else {
                    game.addHistory(player.getUsername(), " did not have any cards that weren't duplicates of ones in ", player.getPronoun(), " hand");
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to draw");
            }
        } else if (card.getName().equals("Jester")) {
            incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
            int nextPlayerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = players.get(nextPlayerIndex);
                if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                    game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                    Card topDeckCard = nextPlayer.removeTopDeckCard();
                    if (topDeckCard != null) {
                        nextPlayer.addCardToDiscard(topDeckCard);
                        game.playerDiscardedCard(nextPlayer, topDeckCard);
                        game.refreshDiscard(nextPlayer);
                        game.addHistory(KingdomUtil.getCardWithBackgroundColor(card), " discarded ", nextPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(topDeckCard));
                        if (topDeckCard.isVictory()) {
                            if (game.getSupply().get(Card.CURSE_ID) > 0) {
                                game.playerGainedCard(nextPlayer, game.getCurseCard());
                            }
                        } else {
                            if (game.getSupply().get(topDeckCard.getCardId()) == null || game.getSupply().get(topDeckCard.getCardId()) == 0) {
                                game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(topDeckCard));
                            } else {
                                CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                                nextCardAction.setDeck(Deck.Cornucopia);
                                nextCardAction.setCardName(card.getName());
                                nextCardAction.setInstructions("Your Jester discarded " + nextPlayer.getUsername() + "'s " + topDeckCard.getName() + ". Do you want to gain a copy of this card, or do you want " + nextPlayer.getUsername() + " to gain a copy of this card?");
                                nextCardAction.getCards().add(topDeckCard);
                                nextCardAction.getChoices().add(new CardActionChoice("I want it", "me"));
                                nextCardAction.getChoices().add(new CardActionChoice("Give one to " + nextPlayer.getUsername(), "them"));
                                nextCardAction.setPlayerId(nextPlayer.getUserId());
                                incompleteCard.getExtraCardActions().add(nextCardAction);
                            }
                        }
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
                nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex);
            }
            game.refreshAllPlayersDiscard();
            if (!incompleteCard.getExtraCardActions().isEmpty()) {
                CardAction cardAction = incompleteCard.getExtraCardActions().remove();
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Menagerie")) {
            Set<String> cardNames = new HashSet<String>();
            if (!player.getHand().isEmpty()) {
                game.addHistory(player.getUsername(), "'s current hand contains ", KingdomUtil.groupCards(player.getHand(), true));
                for (Card c : player.getHand()) {
                    cardNames.add(c.getName());
                }
                if (cardNames.size() == player.getHand().size()) {
                    game.addHistory(player.getUsername(), "'s hand did not contain any duplicates");
                    game.addHistory(player.getUsername(), " gained +3 Cards");
                    player.drawCards(3);
                } else {
                    game.addHistory(player.getUsername(), "'s hand contained duplicates");
                    game.addHistory(player.getUsername(), " gained +1 Card");
                    player.drawCards(1);
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
        } else if (card.getName().equals("Princess")) {
            game.princessCardPlayed();
            game.refreshAllPlayersSupply();
            game.refreshAllPlayersPlayingArea();
            game.refreshAllPlayersHandArea();
        } else if (card.getName().equals("Remake")) {
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                cardAction.setDeck(Deck.Cornucopia);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(1);
                cardAction.setInstructions("Select a card to trash.");
                cardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                game.setPlayerCardAction(player, cardAction);
                if (player.getHand().size() > 1) {
                    incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
                    CardAction secondCardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                    secondCardAction.setDeck(Deck.Cornucopia);
                    secondCardAction.setCardName(card.getName());
                    secondCardAction.setButtonValue("Done");
                    secondCardAction.setNumCards(1);
                    secondCardAction.setInstructions("Select a card to trash.");
                    secondCardAction.setCards(player.getHand());
                    incompleteCard.getExtraCardActions().add(secondCardAction);
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards in ", player.getPronoun(), " hand");
            }
        } else if (card.getName().equals("Tournament")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, player.hasProvinceInHand());
            game.setGainTournamentBonus(true);
            if (player.hasProvinceInHand()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                cardAction.setDeck(Deck.Cornucopia);
                cardAction.setCardName(card.getName());
                cardAction.setInstructions("Do you want to reveal and discard a Province to gain a Prize or a Duchy?");
                game.setPlayerCardAction(player, cardAction);
            }
            for (Player p : players) {
                if (p.getUserId() != player.getUserId()) {
                    if (p.hasProvinceInHand()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                        cardAction.setDeck(Deck.Cornucopia);
                        cardAction.setCardName(card.getName());
                        cardAction.setInstructions("Do you want to reveal a Province to prevent " + player.getUsername() + " from gaining +1 Card, +1 Coin?");
                        game.setPlayerCardAction(p, cardAction);
                    } else {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                    }
                }
            }
            game.addNextAction("gain bonuses");
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Trusty Steed")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Deck.Cornucopia);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Choose a combination.");
            cardAction.getChoices().add(new CardActionChoice("+2 Cards, +2 Actions", "cardsAndActions"));
            cardAction.getChoices().add(new CardActionChoice("+2 Cards, +2 Coins", "cardsAndCoins"));
            cardAction.getChoices().add(new CardActionChoice("+2 Cards, 4 Silvers", "cardsAndSilvers"));
            cardAction.getChoices().add(new CardActionChoice("+2 Actions, +2 Coins", "actionsAndCoins"));
            cardAction.getChoices().add(new CardActionChoice("+2 Actions, 4 Silvers", "actionsAndSilvers"));
            cardAction.getChoices().add(new CardActionChoice("+2 Coins, 4 Silvers", "coinsAndSilvers"));
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Young Witch")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, player.getHand().size() >= 2);
            if (player.getHand().size() > 0) {
                if (player.getHand().size() == 1) {
                    game.playerDiscardedCard(player, player.getHand().get(0));
                    player.discardCardFromHand(player.getHand().get(0));
                    game.addHistory(player.getUsername(), " discarded ", KingdomUtil.getArticleWithCardName(player.getHand().get(0)));
                } else {
                    CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    cardAction.setDeck(Deck.Cornucopia);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(2);
                    cardAction.setInstructions("Select two cards to discard.");
                    cardAction.setCards(player.getHand());
                    game.setPlayerCardAction(player, cardAction);
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to discard");
            }
            int nextPlayerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = players.get(nextPlayerIndex);
                if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                    incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                    game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                    if (nextPlayer.hasBaneCardInHand()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                        cardAction.setDeck(Deck.Cornucopia);
                        cardAction.setCardName(card.getName());
                        cardAction.setInstructions("Do you want to reveal your Bane card, or do you want to gain a Curse?");
                        cardAction.getChoices().add(new CardActionChoice("Reveal Bane Card", "reveal"));
                        cardAction.getChoices().add(new CardActionChoice("Gain a Curse", "curse"));
                        game.setPlayerCardAction(nextPlayer, cardAction);
                    } else {
                        if (game.getSupply().get(Card.CURSE_ID) > 0) {
                            game.playerGainedCard(nextPlayer, game.getCurseCard());
                            game.refreshDiscard(nextPlayer);
                        }
                        incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                    }
                } else {
                    incompleteCard.setPlayerActionCompleted(nextPlayer.getUserId());
                    if (nextPlayer.hasLighthouse()) {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                    } else {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                    }
                }
                nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex);
            }
            incompleteCard.allActionsSet();
        }

        return incompleteCard;
    }
}