package com.kingdom.util.specialaction;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.IncompleteCard;

public class SpecialActionHandler {

    public static void handleSpecialAction(Game game, Card card) {
        handleSpecialAction(game, card, false);
    }

    public static void handleSpecialAction(Game game, Card card, boolean repeatedAction) {
        IncompleteCard incompleteCard = null;
        if (card.isKingdom()) {
            incompleteCard = KingdomSpecialActionHandler.handleSpecialAction(game, card, repeatedAction);
        } else if (card.isIntrigue()) {
            incompleteCard = IntrigueSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isSeaside()) {
            incompleteCard = SeasideSpecialActionHandler.handleSpecialAction(game, card, repeatedAction);
        } else if (card.isAlchemy()) {
            incompleteCard = AlchemySpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isProsperity()) {
            incompleteCard = ProsperitySpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isCornucopia()) {
            incompleteCard = CornucopiaSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isHinterlands()) {
            incompleteCard = HinterlandsSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isPromo()) {
            incompleteCard = PromoSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isSalvation()) {
            incompleteCard = SalvationSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isFairyTale()) {
            incompleteCard = FairyTaleSpecialActionHandler.handleSpecialAction(game, card, repeatedAction);
        } else if (card.isLeader()) {
            LeaderSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isProletariat()) {
            incompleteCard = ProletariatSpecialActionHandler.handleSpecialAction(game, card);
        } else if (card.isFan()) {
            FanSpecialActionHandler.handleSpecialAction(game, card);
        }

        if (incompleteCard != null) {
            incompleteCard.actionFinished(game.getCurrentPlayer());
        }

        if (!game.hasIncompleteCard() && !game.getCurrentPlayer().isShowCardAction()) {
            if (!game.getRepeatedActions().isEmpty()) {
                game.playRepeatedAction(game.getCurrentPlayer(), false);
            } else if (!game.getGolemActions().isEmpty()) {
                game.playGolemActionCard(game.getCurrentPlayer());
            }
        }
    }
}
