package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;
import com.kingdom.util.specialaction.SpecialActionHandler;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/2/11
 * Time: 7:07 AM
 */
public class NextActionHandler {
    public static void handleAction(Game game, String cardName) {

        String nextAction = game.getNextAction();
        if (nextAction == null) {
            nextAction = "";
        }

        if (nextAction.equals("check bell tower")) {
            game.removeNextAction();
            MultiPlayerIncompleteCard incompleteCard = new MultiPlayerIncompleteCard("Bell Tower", game, false);
            Player currentPlayer = game.getCurrentPlayer();
            boolean needBonusCheck = false;
            for (Player player : game.getPlayers()) {
                if (player.getUserId() != game.getCurrentPlayerId()) {
                    if (player.hasBellTowerInHand()) {
                        needBonusCheck = true;
                        CardAction cardAction = new CardAction(CardAction.TYPE_CHOICES);
                        cardAction.setDeck(Card.DECK_SALVATION);
                        cardAction.setCardName("Bell Tower");
                        cardAction.setInstructions(currentPlayer.getUsername()+" played "+ KingdomUtil.getArticleWithCardName(game.getAttackCard())+". You may reveal your Bell Tower to gain +2 cards before or after the attack.");
                        cardAction.getChoices().add(new CardActionChoice("Before", "before"));
                        cardAction.getChoices().add(new CardActionChoice("After", "after"));
                        cardAction.getChoices().add(new CardActionChoice("Don't Reveal", "none"));
                        game.setPlayerCardAction(player, cardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
            if (needBonusCheck) {
                game.addNextAction("check bell tower bonus");
            }
            else {
                handleAction(game, "reaction");
            }
        }
        else if(nextAction.equals("check bell tower bonus")) {
            game.removeNextAction();
            if (!game.getPlayersWaitingForBellTowerBonus().isEmpty()) {
                for (Player player : game.getPlayersWaitingForBellTowerBonus()) {
                    player.drawCards(2);
                    game.addHistory(player.getUsername(), " revealed "+player.getPronoun()+" "+ KingdomUtil.getWordWithBackgroundColor("Bell Tower", Card.ACTION_REACTION_COLOR)+" to gain +2 Cards after the attack");
                }
                game.getPlayersWaitingForBellTowerBonus().clear();
            }
        }
        else if (nextAction.equals("check enchanted palace")) {
            game.removeNextAction();
            MultiPlayerIncompleteCard incompleteCard = new MultiPlayerIncompleteCard("Enchanted Palace", game, false);
            Player currentPlayer = game.getCurrentPlayer();
            boolean hasReaction = false;
            for (Player player : game.getPlayers()) {
                if (player.getUserId() != game.getCurrentPlayerId()) {
                    if (player.hasEnchantedPalaceInHand()) {
                        hasReaction = true;
                        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                        cardAction.setDeck(Card.DECK_FAIRYTALE);
                        cardAction.setCardName("Enchanted Palace");
                        cardAction.setInstructions(currentPlayer.getUsername()+" played "+ KingdomUtil.getArticleWithCardName(game.getAttackCard())+". Do you want to reveal your Enchanted Palace?");
                        int numTimesSet = 0;
                        while (numTimesSet < player.getEnchantedPalacesInHand()) {
                            game.setPlayerCardAction(player, cardAction);
                            numTimesSet++;
                        }
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
            if (!hasReaction) {
                handleAction(game, "reaction");
            }
        }
        else if (nextAction.equals("check horse traders")) {
            game.removeNextAction();
            MultiPlayerIncompleteCard incompleteCard = new MultiPlayerIncompleteCard("Horse Traders", game, false);
            Player currentPlayer = game.getCurrentPlayer();
            boolean hasReaction = false;
            for (Player player : game.getPlayers()) {
                if (player.getUserId() != game.getCurrentPlayerId()) {
                    if (player.hasHorseTradersInHand()) {
                        hasReaction = true;
                        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                        cardAction.setDeck(Card.DECK_CORNUCOPIA);
                        cardAction.setCardName("Horse Traders");
                        cardAction.setInstructions(currentPlayer.getUsername()+" played "+ KingdomUtil.getArticleWithCardName(game.getAttackCard())+". Do you want to set aside your Horse Traders?");
                        game.setPlayerCardAction(player, cardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
            if (!hasReaction) {
                handleAction(game, "reaction");
            }
        }
        else if (nextAction.equals("check secret chamber")) {
            game.removeNextAction();
            MultiPlayerIncompleteCard incompleteCard = new MultiPlayerIncompleteCard("Secret Chamber", game, false);
            Player currentPlayer = game.getCurrentPlayer();
            boolean hasReaction = false;
            for (Player player : game.getPlayers()) {
                if (player.getUserId() != game.getCurrentPlayerId()) {
                    if (player.hasSecretChamber()) {
                        hasReaction = true;
                        CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
                        cardAction.setDeck(Card.DECK_INTRIGUE);
                        cardAction.setCardName("Secret Chamber");
                        cardAction.setInstructions(currentPlayer.getUsername()+" played "+ KingdomUtil.getArticleWithCardName(game.getAttackCard())+", do you want to use your Secret Chamber?");
                        game.setPlayerCardAction(player, cardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(player.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
            if (!hasReaction) {
                handleAction(game, "reaction");
            }
        }
        else if (nextAction.equals("finish attack")) {
            game.removeNextAction();
            game.removeIncompleteCard();
            SpecialActionHandler.handleSpecialAction(game, game.getAttackCard());
        }
        else if (cardName.equals("Black Market")) {
            Player player = game.getCurrentPlayer();
            if (game.getBlackMarketTreasureQueue().isEmpty()) {
                game.removeNextAction();
                CardAction buyCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS);
                buyCardAction.setDeck(Card.DECK_PROMO);
                for (Card card : game.getBlackMarketCardsToBuy()) {
                    if (card.getName().equals("Grand Market")) {
                        boolean addCard = true;
                        for (Card treasureCard : game.getBlackMarketTreasureCardsPlayed()) {
                            if (treasureCard.getCardId() == Card.COPPER_ID) {
                                addCard = false;
                                break;
                            }
                        }
                        if (addCard && game.canBuyCard(player, card)) {
                            buyCardAction.getCards().add(card);
                        }
                    }
                    else if (game.canBuyCardNotInSupply(player, card)) {
                        buyCardAction.getCards().add(card);
                    }
                }
                if (buyCardAction.getCards().size() > 0) {
                    buyCardAction.setCardName("Black Market");
                    buyCardAction.setNumCards(1);
                    buyCardAction.setButtonValue("Done");
                    buyCardAction.setInstructions("Click on the card you want to buy and then click Done.");
                    game.setPlayerCardAction(player, buyCardAction);
                }
                else {
                    CardAction sortCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    sortCardAction.setDeck(Card.DECK_PROMO);
                    sortCardAction.setHideOnSelect(true);
                    sortCardAction.setNumCards(game.getBlackMarketCardsToBuy().size());
                    sortCardAction.setCards(game.getBlackMarketCardsToBuy());
                    sortCardAction.setButtonValue("Done");
                    sortCardAction.setInstructions("You don't have enough coins to buy any of these black market cards. Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)");
                    game.setPlayerCardAction(player, sortCardAction);
                }
            }
            else {
                Card treasureCard = game.getBlackMarketTreasureQueue().remove();
                while (treasureCard.isAutoPlayTreasure()) {
                    game.playTreasureCard(player, treasureCard, true, true, false, true, true);
                    if (game.getBlackMarketTreasureQueue().isEmpty()) {
                        treasureCard = null;
                        break;
                    }
                    treasureCard = game.getBlackMarketTreasureQueue().remove();
                }
                if (treasureCard != null) {
                    game.playTreasureCard(player, treasureCard, true, true, false, true, true);
                }
                else {
                    handleAction(game, "Black Market");
                }
            }
        }
        else if (cardName.equals("Hamlet")) {
            game.removeNextAction();
            CardAction cardAction = new CardAction(CardAction.TYPE_YES_NO);
            cardAction.setDeck(Card.DECK_CORNUCOPIA);
            cardAction.setCardName("Hamlet2");
            cardAction.setInstructions("Do you want to discard a card from your hand to gain +1 Buy?");
            game.setPlayerCardAction(game.getCurrentPlayer(), cardAction);
        }
        else if (cardName.equals("Masquerade")) {
            game.removeNextAction();
            List<Player> players = game.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                Card card = game.getMasqueradeCards().get(p.getUserId());
                if (card != null) {
                    int nextPlayerIndex = game.calculateNextPlayerIndex(i);
                    Player nextPlayer = players.get(nextPlayerIndex);
                    if (nextPlayer.getUserId() == game.getCurrentPlayerId() && card.getAddCoins() != 0) {
                        game.refreshAllPlayersCardsBought();
                    }
                    nextPlayer.addCardToHand(card);
                }
            }
            Player currentPlayer = game.getCurrentPlayer();
            CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
            trashCardAction.setDeck(Card.DECK_INTRIGUE);
            trashCardAction.getCards().addAll(currentPlayer.getHand());
            trashCardAction.setNumCards(1);
            trashCardAction.setCardName("Masquerade");
            trashCardAction.setInstructions("Select a card to trash and then click Done, or just click Done if you don't want to trash a card.");
            trashCardAction.setButtonValue("Done");
            currentPlayer.setShowCardAction(false);
            game.setPlayerCardAction(currentPlayer, trashCardAction);
            game.refreshAllPlayersHand();
        }
        else if (cardName.equals("Tournament")) {
            game.removeNextAction();
            if (game.isGainTournamentBonus()) {
                game.setGainTournamentBonus(false);
                game.getCurrentPlayer().drawCards(1);
                game.getCurrentPlayer().addCoins(1);
                game.refreshAllPlayersCardsBought();
                game.addHistory(game.getCurrentPlayer().getUsername(), " gained +1 Card, +1 Coin from the ", KingdomUtil.getWordWithBackgroundColor("Tournament", Card.ACTION_COLOR));
            }
        }
        else {
            GameError error = new GameError(GameError.GAME_ERROR, "unknown next action - card: "+cardName+ " next action: "+nextAction);
            game.logError(error, false);
            game.removeNextAction();
        }
    }
}
