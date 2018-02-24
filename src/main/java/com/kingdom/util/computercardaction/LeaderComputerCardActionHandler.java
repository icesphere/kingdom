package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 8:10 PM
 */
public class LeaderComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();

        if (cardName.equals("Setup Leaders")) {
            List<Card> cards = new ArrayList<Card>(cardAction.getCards());
            Collections.shuffle(cards);

            List<Integer> cardIds = new ArrayList<Integer>();
            for (int i = 0; i < cardAction.getNumCards(); i++) {
                cardIds.add(cards.get(i).getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Bilkis")) {
            Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToGain.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Plato")) {
            List<Integer> cardsToTrash = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                if (computer.isCardToTrash(card)) {
                    cardsToTrash.add(card.getCardId());
                }
                if (cardsToTrash.size() == 2) {
                    break;
                }
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardsToTrash, null, null, -1);
        }
    }
}
