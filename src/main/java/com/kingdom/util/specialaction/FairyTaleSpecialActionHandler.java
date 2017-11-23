package com.kingdom.util.specialaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/7/11
 * Time: 8:07 PM
 */
public class FairyTaleSpecialActionHandler {
    public static IncompleteCard handleSpecialAction(Game game, Card card, boolean repeatedAction) {

        IncompleteCard incompleteCard = null;
        Player player = game.getCurrentPlayer();
        if (card.getName().equals("Bridge Troll")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_FAIRYTALE);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select the card you want to place a troll token on and then click Done.");
            for (Card c : game.getSupplyMap().values()) {
                int numTrollTokens = game.getTrollTokens().get(card.getCardId());
                if (game.getSupply().get(c.getCardId()) > 0 && numTrollTokens <= 2) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
            else {
                game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("There are no piles left to put the troll token on."));
            }
            if (!repeatedAction) {
                game.removePlayedCard(card);
                game.getTrashedCards().add(card);
                game.playerLostCard(player, card);
            }
        }
        else if (card.getName().equals("Druid")) {
            if (!player.getVictoryCards().isEmpty()) {
                CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_UP_TO_FROM_HAND);
                cardAction.setDeck(Card.DECK_FAIRYTALE);
                cardAction.setCardName(card.getName());
                cardAction.getCards().addAll(player.getVictoryCards());
                cardAction.setNumCards(2);
                cardAction.setInstructions("Discard up to 2 victory cards. +2 Coins per card discarded. Select the Cards you want to discard and then click Done.");
                cardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, cardAction);
            }
            else {
                player.drawCards(2);
                game.addHistory(player.getUsername(), " did not have any victory cards and got +2 cards.");
            }
        }
        else if (card.getName().equals("Lost Village")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_FAIRYTALE);
            cardAction.setCardName("Lost Village 1");
            cardAction.setInstructions("Choose one: +2 Actions, or +1 Action and set aside cards until you choose to draw one.");
            cardAction.getChoices().add(new CardActionChoice("+2 Actions", "actions"));
            cardAction.getChoices().add(new CardActionChoice("+1 Action and draw", "draw"));
            game.setPlayerCardAction(player, cardAction);
        }
        else if (card.getName().equals("Magic Beans")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_FAIRYTALE);
            cardAction.setCardName(card.getName());
            cardAction.getCards().add(card);
            cardAction.setInstructions("Do you want to trash your Magic Beans, or return it to the supply?");
            cardAction.getChoices().add(new CardActionChoice("Trash", "trash"));
            cardAction.getChoices().add(new CardActionChoice("Return to Supply", "supply"));
            game.setPlayerCardAction(player, cardAction);
        }
        else if (card.getName().equals("Master Huntsman")) {
            incompleteCard = new MultiPlayerIncompleteCard(card.getName(), game, false);
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(otherPlayer.getUserId())) {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                        game.addHistory(otherPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    }
                    else if (!otherPlayer.hasMoat() && !otherPlayer.hasLighthouse() && otherPlayer.getHand().size() >= 4 && !otherPlayer.getActionCards().isEmpty()) {
                        CardAction cardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                        cardAction.setDeck(Card.DECK_FAIRYTALE);
                        cardAction.setCardName(card.getName());
                        cardAction.setButtonValue("Done");
                        cardAction.setNumCards(1);
                        cardAction.setInstructions("Select an action card to discard.");
                        cardAction.getCards().addAll(otherPlayer.getActionCards());
                        game.setPlayerCardAction(otherPlayer, cardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                        if (otherPlayer.hasLighthouse()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        }
                        else if(otherPlayer.hasMoat()) {
                            game.addHistory(otherPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                        else if(otherPlayer.getHand().size() < 4) {
                            game.addHistory(otherPlayer.getUsername(), " had less than 4 cards");
                        }
                        else {
                            game.addHistory(otherPlayer.getUsername(), " did not have any action cards");
                        }
                    }
                }
            }
            incompleteCard.allActionsSet();
        }
        else if (card.getName().equals("Quest")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_FAIRYTALE);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select the card you want to name for your Quest.");
            for (Card c : game.getCardMap().values()) {
                if (c.isAction() || c.isVictory()) {
                    cardAction.getCards().add(c);
                }
            }
            cardAction.setAssociatedCard(card);
            game.setPlayerCardAction(player, cardAction);
        }
        else if (card.getName().equals("Sorceress")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_FAIRYTALE);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Choose first effect to apply.");
            cardAction.getChoices().add(new CardActionChoice("+2 Cards", "cards"));
            cardAction.getChoices().add(new CardActionChoice("+2 Actions", "actions"));
            cardAction.getChoices().add(new CardActionChoice("+2 Coins", "coins"));
            cardAction.getChoices().add(new CardActionChoice("+2 Buys", "buys"));
            cardAction.getChoices().add(new CardActionChoice("Trash 2 Cards", "trash"));
            cardAction.setPhase(1);
            game.setPlayerCardAction(player, cardAction);
        }
        else if (card.getName().equals("Tinker")) {
            player.setPlayedTinker(true);
        }

        return incompleteCard;
    }
}
