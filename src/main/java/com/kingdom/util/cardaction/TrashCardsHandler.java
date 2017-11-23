package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrashCardsHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        IncompleteCard incompleteCard = null;

        Map<Integer, Card> cardMap = game.getCardMap();
        Map<Integer, Card> supplyMap = game.getSupplyMap();
        Map<Integer, Integer> supply = game.getSupply();

        if (selectedCardIds.size() == 0) {
            game.addHistory(player.getUsername(), " did not trash a card");
            return null;
        }

        List<Card> cardsTrashed = new ArrayList<Card>();
        for (Integer selectedCardId : selectedCardIds) {
            Card cardToTrash = player.getCardFromHandById(selectedCardId);
            player.removeCardFromHand(cardToTrash);
            game.getTrashedCards().add(cardToTrash);
            game.playerLostCard(player, cardToTrash);
            cardsTrashed.add(cardToTrash);
        }
        if (!cardsTrashed.isEmpty()) {
            game.addHistory(player.getUsername(), " trashed ", KingdomUtil.groupCards(cardsTrashed, true));
        }

        Card trashedCard = cardMap.get(selectedCardIds.get(0));
        if (cardAction.getCardName().equals("Alms")) {
            player.addSins(-1);
            game.refreshAllPlayersPlayers();
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    game.playerGainedCardToHand(otherPlayer, game.getCopperCard());
                    game.refreshHand(otherPlayer);
                }
            }
        }
        else if (cardAction.getCardName().equals("Apprentice")) {
            int numCardsToDraw = game.getCardCost(trashedCard);
            player.drawCards(numCardsToDraw);
            if (trashedCard.isCostIncludesPotion()) {
                player.drawCards(2);
            }
        }
        else if (cardAction.getCardName().equals("Assassin")) {
            game.getCurrentPlayer().addSins(2);
            game.refreshAllPlayersPlayers();
            game.refreshHandArea(game.getCurrentPlayer());
            game.addHistory(game.getCurrentPlayer().getUsername(), " gained 2 sins");
        }
        else if (cardAction.getCardName().equals("Bishop")) {
            int victoryCoinsGained = (int) Math.floor(game.getCardCost(trashedCard) / 2);
            game.addHistory(player.getUsername(), " gained ", KingdomUtil.getPlural(victoryCoinsGained, "Victory Coin")," from the ", KingdomUtil.getWordWithBackgroundColor("Bishop", Card.ACTION_COLOR), " card");
            player.addVictoryCoins(victoryCoinsGained);
            game.refreshAllPlayersPlayers();

            incompleteCard = new MultiPlayerIncompleteCard(cardAction.getCardName(), game, false);
            for (Player otherPlayer : game.getPlayers()) {
                if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                    if (otherPlayer.getHand().size() > 0) {
                        CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                        trashCardAction.setDeck(Card.DECK_PROSPERITY);
                        trashCardAction.setCardName("Bishop 2");
                        trashCardAction.setCards(KingdomUtil.uniqueCardList(otherPlayer.getHand()));
                        trashCardAction.setNumCards(1);
                        trashCardAction.setInstructions("Select a card to trash and then click Done, or just click Done if you don't want to trash a card.");
                        trashCardAction.setButtonValue("Done");
                        game.setPlayerCardAction(otherPlayer, trashCardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(otherPlayer.getUserId());
                    }
                }
            }
            incompleteCard.allActionsSet();
        }
        else if (cardAction.getCardName().equals("Develop")) {
            List<Card> cardsMore = new ArrayList<Card>();
            List<Card> cardsLess = new ArrayList<Card>();

            int cost = game.getCardCost(trashedCard);
            cost = cost + 1;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cardsMore.add(c);
                }
            }

            cost = cost - 2;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cardsLess.add(c);
                }
            }

            if (!cardsLess.isEmpty() && !cardsMore.isEmpty()) {
                CardAction chooseWhichCardAction = new CardAction(CardAction.TYPE_CHOICES);
                chooseWhichCardAction.setDeck(Card.DECK_HINTERLANDS);
                chooseWhichCardAction.setCardName(cardAction.getCardName());
                chooseWhichCardAction.setInstructions("Which do you want to do first: Gain a card costing $1 more than the trashed card, or Gain a card costing $1 less than the trashed card?");
                chooseWhichCardAction.getChoices().add(new CardActionChoice("$1 More", "more"));
                chooseWhichCardAction.getChoices().add(new CardActionChoice("$1 Less", "less"));
                chooseWhichCardAction.getCards().add(trashedCard);
                chooseWhichCardAction.setAssociatedCard(trashedCard);
                game.setPlayerCardAction(player, chooseWhichCardAction);
            }
            else {
                CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                gainCardAction.setDeck(Card.DECK_HINTERLANDS);
                gainCardAction.setCardName(cardAction.getCardName());
                gainCardAction.setButtonValue("Done");
                gainCardAction.setNumCards(1);
                gainCardAction.setAssociatedCard(trashedCard);
                gainCardAction.setPhase(3);
                gainCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
                if (!cardsLess.isEmpty()) {
                    gainCardAction.setCards(cardsLess);
                    game.setPlayerCardAction(player, gainCardAction);
                }
                else if (!cardsMore.isEmpty()) {
                    gainCardAction.setCards(cardsMore);
                    game.setPlayerCardAction(player, gainCardAction);
                }
                else {
                    game.addHistory("There were no cards that cost $1 more and no cards that cost $1 less than ", KingdomUtil.getCardWithBackgroundColor(trashedCard));
                }
            }
        }
        else if (cardAction.getCardName().equals("Expand")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_PROSPERITY);
            secondCardAction.setCardName("Expand");
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards and then click Done.");
            int highestCost = game.getCardCost(trashedCard) + 3;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) <= highestCost && (trashedCard.isCostIncludesPotion() || !c.isCostIncludesPotion()) && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, secondCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Farmland")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_HINTERLANDS);
            secondCardAction.setCardName(cardAction.getCardName());
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards and then click Done.");
            secondCardAction.setGainCardAfterBuyAction(cardAction.isGainCardAfterBuyAction());
            secondCardAction.setAssociatedCard(cardAction.getAssociatedCard());
            int cost = game.getCardCost(trashedCard);
            cost = cost + 2;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() == 1) {
                game.playerGainedCard(player, secondCardAction.getCards().get(0));
            }
            else if (!secondCardAction.getCards().isEmpty()) {
                cardAction.setGainCardAfterBuyAction(false);
                game.setPlayerCardAction(player, secondCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Forge")) {
            if(selectedCardIds.size() > 0) {
                int cost = 0;
                for (Integer selectedCardId : selectedCardIds) {
                    Card card = cardMap.get(selectedCardId);
                    cost += game.getCardCost(card);
                }
                CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
                secondCardAction.setDeck(Card.DECK_PROSPERITY);
                secondCardAction.setCardName("Forge");
                secondCardAction.setButtonValue("Done");
                secondCardAction.setNumCards(1);
                secondCardAction.setInstructions("Select one of the following cards and then click Done.");
                for (Card c : supplyMap.values()) {
                    if (game.getCardCost(c) == cost && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                        secondCardAction.getCards().add(c);
                    }
                }
                if (secondCardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(player, secondCardAction);
                }
                else {
                    game.addHistory("There were no cards that had a cost equal to the cost of the cards trashed");
                }
            }
            else {
                game.addHistory(player.getUsername(), " chose to not trash any cards");
            }
        }
        else if (cardAction.getCardName().equals("Governor")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_PROMO);
            secondCardAction.setCardName(cardAction.getCardName());
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            int cost = game.getCardCost(trashedCard);
            if (game.isCurrentPlayer(player)) {
                cost = cost + 2;
            }
            else {
                cost = cost + 1;
            }
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, secondCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Mine")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_INTO_HAND_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_KINGDOM);
            secondCardAction.setCardName("Mine");
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards to put into your hand and then click Done.");
            for (Card card : game.getAvailableTreasureCardsInSupply()) {
                if ((game.getCardCost(trashedCard) + 3) >= game.getCardCost(card) && (trashedCard.isCostIncludesPotion() || !card.isCostIncludesPotion())) {
                    secondCardAction.getCards().add(card);
                }
            }
            if (!secondCardAction.getCards().isEmpty()) {
                game.setPlayerCardAction(player, secondCardAction);
            }
            else {
                game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("There were no treasure cards to gain."));
            }
        }
        else if (cardAction.getCardName().equals("Remake")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_CORNUCOPIA);
            secondCardAction.setCardName(cardAction.getCardName());
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards and then click Done.");
            int cost = game.getCardCost(trashedCard);
            cost = cost + 1;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, secondCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Remodel")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_KINGDOM);
            secondCardAction.setCardName("Remodel");
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards and then click Done.");
            int highestCost = game.getCardCost(trashedCard) + 2;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) <= highestCost && (trashedCard.isCostIncludesPotion() || !c.isCostIncludesPotion()) && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, secondCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Salvager")) {
            player.addCoins(game.getCardCost(trashedCard));
        }
        else if (cardAction.getCardName().equals("Sorceress")) {
            int cursesRemaining = game.getSupply().get(Card.CURSE_ID);
            if (cursesRemaining > 0 || cardAction.getPhase() == 1 && cardAction.getChoices().size() > 1) {
                CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                nextCardAction.setDeck(Card.DECK_FAIRYTALE);
                nextCardAction.setCardName(cardAction.getCardName());

                if (cursesRemaining == 0) {
                    nextCardAction.setInstructions("There are no curses remaining so you may only choose one more effect.");
                }
                else {
                    nextCardAction.setInstructions("Choose another effect to apply (you will gain a curse), or click None if you don't want to apply any more effects.");
                }

                nextCardAction.getChoices().addAll(cardAction.getChoices());
                nextCardAction.setPhase(cardAction.getPhase()+1);
                game.setPlayerCardAction(player, nextCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Spice Merchant")) {
            if (!selectedCardIds.isEmpty()) {
                CardAction choicesAction = new CardAction(CardAction.TYPE_CHOICES);
                choicesAction.setDeck(Card.DECK_HINTERLANDS);
                choicesAction.setCardName(cardAction.getCardName());
                choicesAction.setInstructions("Choose one: +2 Cards and +1 Action; or +$2 and +1 Buy.");
                choicesAction.getChoices().add(new CardActionChoice("+2 Cards and +1 Action", "cards"));
                choicesAction.getChoices().add(new CardActionChoice("+$2 and +1 Buy", "money"));
                game.setPlayerCardAction(player, choicesAction);
            }
        }
        else if (cardAction.getCardName().equals("Trader")) {
            int numSilversToGain = game.getCardCost(trashedCard);
            while (game.isCardInSupply(Card.SILVER_ID) && numSilversToGain > 0) {
                game.playerGainedCard(player, game.getSilverCard());
                numSilversToGain--;
            }
        }
        else if (cardAction.getCardName().equals("Trading Post")) {
            if (game.isCardInSupply(Card.SILVER_ID)) {
                game.playerGainedCardToHand(player, game.getSilverCard());
            }
            else {
                game.addHistory("There were no more ", KingdomUtil.getCardWithBackgroundColor(game.getSilverCard()), " cards in the supply");
            }
        }
        else if (cardAction.getCardName().equals("Transmute")) {
            if (trashedCard.isAction()) {
                if (game.isCardInSupply(Card.DUCHY_ID)) {
                    game.playerGainedCard(player, game.getDuchyCard());
                }
            }
            if (trashedCard.isTreasure()) {
                if (game.isCardInSupply(cardAction.getCardId())) {
                    game.playerGainedCard(player, cardMap.get(cardAction.getCardId()));
                }
            }
            if (trashedCard.isVictory()) {
                if (game.isCardInSupply(Card.GOLD_ID)) {
                    game.playerGainedCard(player, game.getGoldCard());
                }
            }
        }
        else if (cardAction.getCardName().equals("Upgrade")) {
            CardAction secondCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            secondCardAction.setDeck(Card.DECK_INTRIGUE);
            secondCardAction.setCardName("Upgrade");
            secondCardAction.setButtonValue("Done");
            secondCardAction.setNumCards(1);
            secondCardAction.setInstructions("Select one of the following cards and then click Done.");
            int cost = game.getCardCost(trashedCard);
            cost = cost + 1;
            for (Card c : supplyMap.values()) {
                if (game.getCardCost(c) == cost && trashedCard.isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    secondCardAction.getCards().add(c);
                }
            }
            if (secondCardAction.getCards().size() == 1) {
                game.playerGainedCard(player, secondCardAction.getCards().get(0));
            }
            else if (!secondCardAction.getCards().isEmpty()) {
                game.setPlayerCardAction(player, secondCardAction);
            }
        }

        return incompleteCard;
    }
}
