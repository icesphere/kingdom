package com.kingdom.util.specialaction;

import com.kingdom.model.*;

public class GainCardsReactionHandler {

    public static CardAction getCardAction(String action, Game game, Player player, Card card, String destination) {

        if (action.equals("Royal Seal")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setGainCardAction(true);
            cardAction.setDeck(Deck.Prosperity);
            cardAction.setCardName("Royal Seal");
            cardAction.setAssociatedCard(card);
            if (destination.equals("hand")) {
                cardAction.setInstructions("Do you want to put this card on top of your deck instead of in your hand?");
            } else {
                cardAction.setInstructions("Do you want to put this card on top of your deck instead of in your discard pile?");
            }
            cardAction.setDestination(destination);
            cardAction.getCards().add(card);
            return cardAction;
        } else if (action.equals("Tinker")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setGainCardAction(true);
            cardAction.setDeck(Deck.FairyTale);
            cardAction.setCardName("Tinker");
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Do you want to add this card under your Tinker?");
            cardAction.setDestination(destination);
            cardAction.getCards().add(card);
            return cardAction;
        } else if (action.equals("Trader")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setGainCardAction(true);
            cardAction.setDeck(Deck.Hinterlands);
            cardAction.setCardName("Trader");
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Do you want to reveal your Trader to gain a Silver instead?");
            cardAction.setDestination(destination);
            cardAction.getCards().add(card);
            cardAction.getChoices().add(new CardActionChoice("Don't Reveal", "no_reveal"));
            cardAction.getChoices().add(new CardActionChoice("Gain Silver", "silver"));
            return cardAction;
        } else if (action.equals("Watchtower")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setGainCardAction(true);
            cardAction.setDeck(Deck.Prosperity);
            cardAction.setCardName("Watchtower");
            cardAction.setAssociatedCard(card);
            cardAction.setInstructions("Do you want to reveal your Watchtower to trash this card, or put this card on top of your deck?");
            cardAction.setDestination(destination);
            cardAction.getCards().add(card);
            cardAction.getChoices().add(new CardActionChoice("Don't Reveal", "no_reveal"));
            cardAction.getChoices().add(new CardActionChoice("Trash Card", "trash"));
            cardAction.getChoices().add(new CardActionChoice("Top of Deck", "deck"));
            return cardAction;
        }
        return null;
    }
}
