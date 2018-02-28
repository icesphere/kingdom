package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SalvationSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card) {

        IncompleteCard incompleteCard = null;
        Player player = game.getCurrentPlayer();

        if (card.getName().equals("Alms")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a treasure card to trash.");
            cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(player.getTreasureCards()));
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("You don't have any treasure cards to trash."));
            }
        } else if (card.getName().equals("Archbishop")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Choose one: +2 Actions; remove 1 sin; or each other player gains 1 sin.");
            cardAction.getChoices().add(new CardActionChoice("+2 Actions", "actions"));
            cardAction.getChoices().add(new CardActionChoice("Remove 1 Sin", "remove"));
            cardAction.getChoices().add(new CardActionChoice("Others +1 Sin", "sins"));
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Assassin")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(otherPlayer.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                        game.addHistory(otherPlayer.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                        cardAction.setDeck(Deck.Salvation);
                        cardAction.setCardName(card.getName());
                        cardAction.setButtonValue("Done");
                        cardAction.setNumCards(1);
                        cardAction.setInstructions("Select an attack card to trash.");
                        for (Card c : otherPlayer.getActionCards()) {
                            if (c.isAttack()) {
                                cardAction.getCards().add(c);
                            }
                        }
                        if (!cardAction.getCards().isEmpty()) {
                            game.setPlayerCardAction(otherPlayer, cardAction);
                        } else {
                            incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                            game.addHistory(otherPlayer.getUsername(), " did not have any attack cards");
                        }
                    } else {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                        if (otherPlayer.hasLighthouse()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        } else if (card.getName().equals("Baptistry")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Choose a card.");
            cardAction.getCards().addAll(game.getCardMap().values());
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Catacombs")) {
            List<Card> cards = new ArrayList<Card>();
            Collections.shuffle(player.getDiscard());
            game.refreshDiscard(player);
            if (player.getDiscard().size() <= 4) {
                cards.addAll(player.getDiscard());
            } else {
                cards.addAll(player.getDiscard().subList(player.getDiscard().size() - 4, player.getDiscard().size()));
            }
            if (cards.isEmpty()) {
                game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("Your discard pile was empty."));
            } else {
                game.addHistory("Catacombs revealed ", KingdomUtil.INSTANCE.groupCards(cards, true));
                if (cards.size() == 1) {
                    game.playerGainedCardToHand(player, cards.get(0), false);
                    player.getDiscard().removeLastOccurrence(cards.get(0));
                } else if (cards.size() > 0) {
                    CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                    cardAction.setDeck(Deck.Salvation);
                    cardAction.setCardName(card.getName());
                    cardAction.setButtonValue("Done");
                    cardAction.setNumCards(1);
                    cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(cards));
                    cardAction.setInstructions("Select a card to gain from your discard pile.");
                    game.setPlayerCardAction(player, cardAction);
                }
            }
        } else if (card.getName().equals("Edict")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Choose an action card for the Edict.");
            for (Card c : game.getCardMap().values()) {
                if (c.isAction()) {
                    cardAction.getCards().add(c);
                }
            }
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Graverobber")) {
            incompleteCard = new SinglePlayerIncompleteCard(card.getName(), game);
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(otherPlayer.getUserId())) {
                        game.addHistory(otherPlayer.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse() && !otherPlayer.getDiscard().isEmpty()) {
                        int randomIndex1 = 0;
                        int randomIndex2 = -1;
                        if (otherPlayer.getDiscard().size() > 1) {
                            randomIndex1 = KingdomUtil.INSTANCE.getRandomNumber(0, otherPlayer.getDiscard().size() - 1);
                            randomIndex2 = KingdomUtil.INSTANCE.getRandomNumber(0, otherPlayer.getDiscard().size() - 1);
                            while (randomIndex2 == randomIndex1) {
                                randomIndex2 = KingdomUtil.INSTANCE.getRandomNumber(0, otherPlayer.getDiscard().size() - 1);
                            }
                        }
                        for (int i = 1; i <= 2; i++) {
                            Card revealedCard = null;
                            int index = 0;
                            if (i == 1) {
                                revealedCard = otherPlayer.getDiscard().get(randomIndex1);
                                index = randomIndex1;
                            } else if (i == 2 && randomIndex2 >= 0) {
                                revealedCard = otherPlayer.getDiscard().get(randomIndex2);
                                index = randomIndex2;
                            }
                            if (revealedCard != null) {
                                CardAction nextCardAction;
                                if (revealedCard.isTreasure()) {
                                    nextCardAction = new CardAction(CardAction.TYPE_YES_NO);
                                    nextCardAction.setInstructions("The graverobber revealed this treasure card from " + otherPlayer.getUsername() + "'s discard pile. Do you want to gain this card?");
                                } else {
                                    nextCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                                    nextCardAction.setNumCards(0);
                                    nextCardAction.setButtonValue("Continue");
                                    nextCardAction.setInstructions("The graverobber revealed this non-treasure card from " + otherPlayer.getUsername() + "'s discard pile. Click Continue.");
                                }
                                nextCardAction.setCardId(index);
                                nextCardAction.setDeck(Deck.Salvation);
                                nextCardAction.setPlayerId(otherPlayer.getUserId());
                                nextCardAction.setCardName(card.getName());
                                nextCardAction.getCards().add(revealedCard);
                                incompleteCard.getExtraCardActions().add(nextCardAction);
                            }
                        }
                    } else {
                        if (otherPlayer.hasLighthouse()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else if (otherPlayer.hasMoat()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        } else {
                            game.addHistory(otherPlayer.getUsername(), "'s discard pile was empty");
                        }
                    }
                }
            }
            if (!incompleteCard.getExtraCardActions().isEmpty()) {
                CardAction cardAction = incompleteCard.getExtraCardActions().remove();
                cardAction.setDeck(Deck.Salvation);
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Indulgence")) {
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    otherPlayer.addSins(-1);
                }
            }
            game.refreshAllPlayersPlayers();
            game.addHistory("All other players removed one sin");
        } else if (card.getName().equals("Inquisitor")) {
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(otherPlayer.getUserId())) {
                        game.addHistory(otherPlayer.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    } else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse()) {
                        if (otherPlayer.getSins() >= 2) {
                            int cursesInSupply = game.getSupply().get(Card.CURSE_ID);
                            if (cursesInSupply >= 2) {
                                otherPlayer.addSins(-2);
                                game.addHistory(otherPlayer.getUsername(), " removed 2 sins and gained 2 ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR), " cards from the Inquisitor");
                                game.playerGainedCard(otherPlayer, game.getCurseCard());
                                game.playerGainedCard(otherPlayer, game.getCurseCard());
                            } else if (cursesInSupply == 1) {
                                otherPlayer.addSins(-1);
                                game.addHistory(otherPlayer.getUsername(), " removed 1 sin and gained 1 ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR), " card from the Inquisitor");
                                game.playerGainedCard(otherPlayer, game.getCurseCard());
                            } else {
                                otherPlayer.addSins(1);
                                game.addHistory(otherPlayer.getUsername(), " gained a sin from the Inquisitor");
                            }
                        } else {
                            otherPlayer.addSins(1);
                            game.addHistory(otherPlayer.getUsername(), " gained a sin from the Inquisitor");
                        }
                        game.refreshHandArea(otherPlayer);
                    } else {
                        if (otherPlayer.hasLighthouse()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        } else {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                }
            }
            game.refreshAllPlayersPlayers();
        } else if (card.getName().equals("Mendicant")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select a card to gain from the trash pile and then click Done, or just click Done if you do not want to gain a card.");
            cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(game.getTrashedCards()));
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("The trash pile is empty."));
            }
        } else if (card.getName().equals("Scriptorium")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
            cardAction.setDeck(Deck.Salvation);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Chose an action card to discard from your hand.");
            cardAction.setCards(KingdomUtil.INSTANCE.uniqueCardList(player.getActionCards()));
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            } else {
                game.setPlayerInfoDialog(player, InfoDialog.Companion.getInfoDialog("You do not have any action cards in your hand."));
            }
        }

        return incompleteCard;
    }
}
