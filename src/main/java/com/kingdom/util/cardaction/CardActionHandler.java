package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.List;
import java.util.Map;

public class CardActionHandler {
    public static void handleSubmittedCardAction(Game game, Player player, List<Integer> selectedCardIds, String yesNoAnswer, String choice, int numberChosen) {

        player.setShowCardAction(false);

        Map<Integer, Card> supplyMap = game.getSupplyMap();
        CardAction cardAction = player.getCardAction();
        int type = cardAction.getType();
        IncompleteCard incompleteCard = null;

        if (cardAction.isDiscard()) {
            incompleteCard = DiscardCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY || type == CardAction.TYPE_GAIN_UP_TO_FROM_SUPPLY || type == CardAction.TYPE_GAIN_CARDS || type == CardAction.TYPE_GAIN_CARDS_UP_TO) {
            GainCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_TRASH_CARDS_FROM_HAND || type == CardAction.TYPE_TRASH_UP_TO_FROM_HAND) {
            incompleteCard = TrashCardsHandler.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY) {
            for (Integer selectedCardId : selectedCardIds) {
                Card card = supplyMap.get(selectedCardId);
                game.playerGainedCardToHand(player, card);
            }
            game.refreshPlayingArea(player);
        } else if (type == CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK) {
            if (selectedCardIds.size() > 1 && cardAction.getCardName().equals("Ghost Ship")) {
                CardAction reorderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                reorderCardAction.setDeck(Deck.Seaside);
                reorderCardAction.setHideOnSelect(true);
                reorderCardAction.setNumCards(selectedCardIds.size());
                reorderCardAction.setCardName(cardAction.getCardName());
                for (Integer selectedCardId : selectedCardIds) {
                    Card card = supplyMap.get(selectedCardId);
                    player.removeCardFromHand(card);
                    reorderCardAction.getCards().add(card);
                }
                reorderCardAction.setButtonValue("Done");
                reorderCardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                game.setPlayerCardAction(player, reorderCardAction);
            } else if (cardAction.getCardName().equals("Bureaucrat")) {
                Card card = supplyMap.get(selectedCardIds.get(0));
                game.addHistory(player.getUsername(), " added 1 Victory card on top of ", player.getPronoun(), " deck");
                player.putCardFromHandOnTopOfDeck(card);
            } else {
                for (Integer selectedCardId : selectedCardIds) {
                    Card card = player.getCardFromHandById(selectedCardId);
                    player.putCardFromHandOnTopOfDeck(card);
                }
                game.addHistory(player.getUsername(), " added ", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "card"), " on top of ", player.getPronoun(), " deck");
            }
        } else if (type == CardAction.TYPE_CHOOSE_CARDS || type == CardAction.TYPE_SETUP_LEADERS) {
            incompleteCard = ChooseCardsHandler.INSTANCE.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_YES_NO) {
            incompleteCard = YesNoHandler.handleCardAction(game, player, cardAction, yesNoAnswer);
        } else if (type == CardAction.TYPE_CHOICES) {
            incompleteCard = ChoicesHandler.INSTANCE.handleCardAction(game, player, cardAction, choice);
        } else if (type == CardAction.TYPE_CHOOSE_IN_ORDER) {
            incompleteCard = ChooseInOrderHandler.INSTANCE.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_CHOOSE_UP_TO) {
            incompleteCard = ChooseUpToHandler.handleCardAction(game, player, cardAction, selectedCardIds);
        } else if (type == CardAction.TYPE_CHOOSE_NUMBER_BETWEEN || type == CardAction.TYPE_CHOOSE_EVEN_NUMBER_BETWEEN) {
            ChooseNumberBetweenHandler.INSTANCE.handleCardAction(game, player, cardAction, numberChosen);
        }

        if (cardAction.isGainCardAction()) {
            game.finishedGainCardAction(player, cardAction);
        }

        if (game.hasIncompleteCard()) {
            game.getIncompleteCard().actionFinished(player);
        }

        game.refreshHandArea(player);
        game.refreshCardsBought(player);


        if (!player.isShowCardAction() && !player.getExtraCardActions().isEmpty()) {
            game.setPlayerCardAction(player, player.getExtraCardActions().remove());
        } else if (!player.isShowCardAction() && cardAction.isGainCardAction() && game.hasUnfinishedGainCardActions()) {
            if (!cardAction.getAssociatedCard().getGainCardActions().isEmpty()) {
                game.setPlayerGainCardAction(player, cardAction.getAssociatedCard());
            } else {
                game.setPlayerGainCardAction(player, game.getCardWithUnfinishedGainCardActions());
            }
        }

        if (cardAction.isGainCardAfterBuyAction()) {
            game.playerGainedCard(player, cardAction.getAssociatedCard());
        }

        //check for throne room/king's court/golem actions
        if (!game.hasIncompleteCard() && !game.getCurrentPlayer().isShowCardAction()) {
            if (!game.getRepeatedActions().isEmpty()) {
                game.playRepeatedAction(game.getCurrentPlayer(), false);
            } else if (!game.getGolemActions().isEmpty()) {
                game.playGolemActionCard(game.getCurrentPlayer());
            }
        }

        if (!game.hasIncompleteCard() && !player.isShowCardAction() && !game.isCurrentPlayer(player) && game.getPlayersWithCardActions().isEmpty()) {
            if (game.getCurrentPlayer().isShowCardAction() && game.getCurrentPlayer().getCardAction().isWaitingForPlayers()) {
                game.closeCardActionDialog(game.getCurrentPlayer());
                game.closeLoadingDialog(game.getCurrentPlayer());
            }
        }

        if (incompleteCard != null && incompleteCard.isEndTurn()) {
            game.setEndingTurn(false);
            game.endPlayerTurn(player, false);
        }
    }
}
