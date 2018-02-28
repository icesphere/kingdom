package com.kingdom.util.cardaction;

import com.kingdom.model.Card;
import com.kingdom.model.CardAction;
import com.kingdom.model.Game;
import com.kingdom.model.Player;
import com.kingdom.util.KingdomUtil;

public class ChooseNumberBetweenHandler {
    public static void handleCardAction(Game game, Player player, CardAction cardAction, int numberChosen) {
        if (cardAction.getCardName().equals("Counting House")) {
            if (numberChosen > 0) {
                game.addHistory(KingdomUtil.INSTANCE.getWordWithBackgroundColor("Counting House", Card.ACTION_COLOR), " added ", KingdomUtil.INSTANCE.getPlural(numberChosen, "Copper"), " to ", player.getUsername(), "'s hand");
                for (int i = 0; i < numberChosen; i++) {
                    player.getDiscard().remove(game.getCopperCard());
                    player.addCardToHand(game.getCopperCard());
                }
            }
        } else if (cardAction.getCardName().equals("Use Cattle Tokens")) {
            if (numberChosen > 0 && player.getCattleTokens() >= numberChosen) {
                player.addCattleTokens(numberChosen * (-1));
                int timesToApplyBonus = numberChosen / 2;
                player.drawCards(timesToApplyBonus);
                player.addActions(timesToApplyBonus);
                game.refreshHandArea(player);
                game.refreshAllPlayersCardsPlayed();
                game.addHistory(player.getUsername(), " used ", KingdomUtil.INSTANCE.getPlural(numberChosen, "Cattle Token"));
            }
        } else if (cardAction.getCardName().equals("Use Fruit Tokens")) {
            if (numberChosen > 0 && player.getFruitTokens() >= numberChosen) {
                player.addFruitTokens(numberChosen * (-1));
                player.addCoins(numberChosen);
                game.addFruitTokensPlayed(numberChosen);
                game.refreshAllPlayersCardsBought();
                game.addHistory(player.getUsername(), " used ", KingdomUtil.INSTANCE.getPlural(numberChosen, "Fruit Token"));
            }
        }
    }
}
