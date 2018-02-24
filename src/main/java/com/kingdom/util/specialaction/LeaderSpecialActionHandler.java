package com.kingdom.util.specialaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 8:09 PM
 */
public class LeaderSpecialActionHandler {

    public static void handleSpecialAction(Game game, Card card) {
        Player player = game.getCurrentPlayer();

        if (card.getName().equals("Archimedes")) {
            player.setBuyBonusTurns(2);
            player.setEnableVictoryCardDiscount(true);
        } else if (card.getName().equals("Aristotle")) {
            player.setCardBonusTurns(2);
        } else if (card.getName().equals("Bilkis")) {
            CardAction cardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
            cardAction.setDeck(Card.DECK_LEADERS);
            cardAction.setCardName(card.getName());
            cardAction.setButtonValue("Done");
            cardAction.setNumCards(1);
            cardAction.setInstructions("Select one of the following cards to gain on top of your deck and then click Done.");
            for (Card c : game.getSupplyMap().values()) {
                if (game.getCardCost(c) <= 6 && !c.isCostIncludesPotion() && game.getSupply().get(c.getCardId()) > 0) {
                    cardAction.getCards().add(c);
                }
            }
            if (cardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Imhotep")) {
            player.setBuyBonusTurns(2);
            player.setEnableTreasureCardDiscount(true);
        } else if (card.getName().equals("Leonidas")) {
            player.setBuyBonusTurns(2);
            player.setEnableActionCardDiscount(true);
        } else if (card.getName().equals("Maecenas")) {
            player.setLeaderDiscount(3);
        } else if (card.getName().equals("Plato")) {
            if (player.getHand().size() > 0) {
                CardAction cardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                cardAction.setDeck(Card.DECK_LEADERS);
                cardAction.setCardName(card.getName());
                cardAction.setButtonValue("Done");
                cardAction.setNumCards(2);
                cardAction.setInstructions("Trash up to 2 cards.");
                cardAction.setCards(player.getHand());
                game.setPlayerCardAction(player, cardAction);
            }
        } else if (card.getName().equals("Tomyris")) {
            int nextPlayerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = game.getPlayers().get(nextPlayerIndex);
                int numInSupply = game.getSupply().get(Card.CURSE_ID);
                if (numInSupply > 0) {
                    game.playerGainedCard(nextPlayer, game.getCurseCard());
                    game.refreshDiscard(nextPlayer);
                }
                nextPlayerIndex = game.calculateNextPlayerIndex(nextPlayerIndex);
            }
        } else if (card.getName().equals("Xenophon")) {
            player.addCoins(2);
            player.addBuys(1);
            game.refreshAllPlayersPlayingArea();
        }
    }
}
