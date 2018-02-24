package com.kingdom.util.computercardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.model.computer.ComputerPlayer;
import com.kingdom.util.cardaction.CardActionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/7/11
 * Time: 8:05 PM
 */
public class FairyTaleComputerCardActionHandler {
    public static void handleCardAction(CardAction cardAction, ComputerPlayer computer) {

        Game game = computer.getGame();
        Player player = computer.getPlayer();

        String cardName = cardAction.getCardName();
        int type = cardAction.getType();

        if (cardName.equals("Bridge Troll")) {
            //todo better algorithm for deciding which card to add token to
            List<Integer> cardIds = new ArrayList<Integer>();
            Card card = computer.getRandomHighestCostCardFromCostMap(8, false);
            if (card != null) {
                cardIds.add(card.getCardId());
            } else {
                cardIds.add(cardAction.getCards().get(0).getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Druid")) {
            //todo determine when not to discard a victory card
            List<Integer> cardsToDiscard = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardsToDiscard.add(card.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardsToDiscard, null, null, -1);
        } else if (cardName.equals("Enchanted Palace")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        } else if (cardName.equals("Lost Village 1")) {
            //todo determine when it is good to get +2 actions
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "draw", -1);
        } else if (cardName.equals("Lost Village")) {
            //todo determine when it is good to discard
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "draw", -1);
        } else if (cardName.equals("Magic Beans")) {
            if (type == CardAction.TYPE_CHOICES) {
                //todo determine when it is good to return to supply
                CardActionHandler.handleSubmittedCardAction(game, player, null, null, "trash", -1);
            } else {
                Card cardToGain = computer.getHighestCostCard(cardAction.getCards());
                List<Integer> cardIds = new ArrayList<Integer>();
                cardIds.add(cardToGain.getCardId());
                CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
            }
        } else if (cardName.equals("Master Huntsman")) {
            //todo determine best action to discard
            Card cardToDiscard = computer.getLowestCostCard(cardAction.getCards());
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(cardToDiscard.getCardId());
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Quest")) {
            //todo determine best choice
            List<Integer> cardIds = new ArrayList<Integer>();
            cardIds.add(Card.ESTATE_ID);
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Sorceress")) {
            //todo determine best choices
            CardActionHandler.handleSubmittedCardAction(game, player, null, null, "coins", -1);
        } else if (cardName.equals("Storybook")) {
            List<Integer> cardIds = new ArrayList<Integer>();
            for (Card card : cardAction.getCards()) {
                cardIds.add(card.getCardId());
            }
            CardActionHandler.handleSubmittedCardAction(game, player, cardIds, null, null, -1);
        } else if (cardName.equals("Tinker")) {
            CardActionHandler.handleSubmittedCardAction(game, player, null, "yes", null, -1);
        }
    }
}
