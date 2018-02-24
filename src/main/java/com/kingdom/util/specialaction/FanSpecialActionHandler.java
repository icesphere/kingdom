package com.kingdom.util.specialaction;

import com.kingdom.model.*;

import java.util.HashSet;
import java.util.Set;

public class FanSpecialActionHandler {

    public static void handleSpecialAction(Game game, Card card) {
        Player player = game.getCurrentPlayer();

        if (card.getName().equals("Archivist")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
            cardAction.setDeck(Card.DECK_FAN);
            cardAction.setCardName(card.getName());
            cardAction.setInstructions("Choose one: Draw until you have 6 cards in hand; or +$1 and discard 1 or more cards.");
            cardAction.getChoices().add(new CardActionChoice("Draw", "draw"));
            cardAction.getChoices().add(new CardActionChoice("+$1 and discard", "discard"));
            game.setPlayerCardAction(player, cardAction);
        } else if (card.getName().equals("Museum")) {
            Set<Card> cards = new HashSet<Card>();
            for (Card c : player.getHand()) {
                if (!player.getMuseumCards().contains(c)) {
                    cards.add(c);
                }
            }
            if (!cards.isEmpty()) {
                CardAction museumCardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
                museumCardAction.setDeck(Card.DECK_FAN);
                museumCardAction.setCardName(card.getName());
                museumCardAction.getCards().addAll(cards);
                museumCardAction.setNumCards(1);
                museumCardAction.setInstructions("Select a card to be added to your Museum mat and then click Done, or just click Done if you don't want to add a card.");
                museumCardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, museumCardAction);
            } else if (player.getMuseumCards().size() >= 4) {
                CardAction museumCardAction = new CardAction(CardAction.TYPE_YES_NO);
                museumCardAction.setDeck(Card.DECK_FAN);
                museumCardAction.setCardName("Museum Trash Cards");
                museumCardAction.setInstructions("Do you want to trash 4 cards from your Museum mat to gain a Prize and a Duchy?");
                game.setPlayerCardAction(player, museumCardAction);
            }
        }
    }
}
