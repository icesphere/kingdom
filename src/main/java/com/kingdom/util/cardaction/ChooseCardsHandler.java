package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseCardsHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        Map<Integer, Card> cardMap = game.getCardMap();
        Map<Integer, Player> playerMap = game.getPlayerMap();
        IncompleteCard incompleteCard = null;

        if (cardAction.getCardName().equals("Ambassador")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            CardAction addToSupplyCardAction = new CardAction(CardAction.TYPE_CHOOSE_UP_TO);
            addToSupplyCardAction.setDeck(Deck.Seaside);
            addToSupplyCardAction.setInstructions("Select the cards that you want to return to the supply and then click Done.");
            addToSupplyCardAction.setButtonValue("Done");
            addToSupplyCardAction.setCardName("Ambassador");
            for (Card card : player.getHand()) {
                if (addToSupplyCardAction.getCards().size() == 2) {
                    break;
                }
                if (card.getCardId() == selectedCard.getCardId()) {
                    addToSupplyCardAction.getCards().add(card);
                }
            }
            addToSupplyCardAction.setNumCards(addToSupplyCardAction.getCards().size());
            game.setPlayerCardAction(player, addToSupplyCardAction);
        } else if (cardAction.getCardName().equals("Baptistry")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.addHistory(player.getUsername(), " chose ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " for Baptistry");

            boolean hasMoreCards = true;
            List<Card> revealedCards = new ArrayList<Card>();
            while (hasMoreCards && revealedCards.size() < 5) {
                Card c = player.removeTopDeckCard();
                if (c == null) {
                    hasMoreCards = false;
                } else {
                    revealedCards.add(c);
                }
            }
            if (!revealedCards.isEmpty()) {
                game.addHistory("Baptistry revealed ", KingdomUtil.groupCards(revealedCards, true));
                int cardsTrashed = 0;
                for (Card revealedCard : revealedCards) {
                    if (revealedCard.getCardId() == selectedCard.getCardId()) {
                        game.getTrashedCards().add(revealedCard);
                        game.playerLostCard(player, revealedCard);
                        cardsTrashed++;
                    } else {
                        player.addCardToDiscard(revealedCard);
                        game.playerDiscardedCard(player, revealedCard);
                    }
                }
                if (cardsTrashed > 0) {
                    game.addHistory(player.getUsername(), " trashed ", KingdomUtil.getPlural(cardsTrashed, "card"), " and removed 1 sin");
                    player.addSins(-1);
                    game.refreshAllPlayersPlayers();
                }
            } else {
                game.addHistory(player.getUsername(), " did not have any cards to reveal");
            }
        } else if (cardAction.getCardName().equals("Bilkis")) {
            Card card = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCardToTopOfDeck(player, card);
        } else if (cardAction.getCardName().equals("Bridge Troll")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.addTrollToken(selectedCard.getCardId());
            game.addHistory(player.getUsername(), " added a troll token to the ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " card");
        } else if (cardAction.getCardName().equals("Catacombs")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.playerGainedCardToHand(player, selectedCard, false);
            player.getDiscard().remove(selectedCard);
            game.refreshHandArea(player);
        } else if (cardAction.getCardName().equals("City Planner")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            player.removeCardFromHand(selectedCard);
            player.getCityPlannerCards().add(selectedCard);
            game.addHistory(player.getUsername(), " paid an extra $2 to set aside ", KingdomUtil.getArticleWithCardName(selectedCard));
        } else if (cardAction.getCardName().equals("Contraband")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.addHistory(game.getCurrentPlayer().getUsername(), " can't buy ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " this turn");
            game.getContrabandCards().add(selectedCard);
        } else if (cardAction.getCardName().equals("Edict")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.getEdictCards().add(selectedCard);
            player.getEdictCards().add(selectedCard);
            game.addHistory(player.getUsername(), " played an Edict on ", KingdomUtil.getCardWithBackgroundColor(selectedCard));
        } else if (cardAction.getCardName().equals("Embargo")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.addEmbargoToken(selectedCard.getCardId());
            game.addHistory(player.getUsername(), " added an embargo token to the ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " card");
        } else if (cardAction.getCardName().equals("Envoy")) {
            Player currentPlayer = game.getCurrentPlayer();
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            currentPlayer.addCardToDiscard(selectedCard);
            game.playerDiscardedCard(currentPlayer, selectedCard);
            cardAction.getCards().remove(selectedCard);
            for (Card card : cardAction.getCards()) {
                currentPlayer.addCardToHand(card);
            }
            game.refreshHandArea(currentPlayer);
            game.refreshCardsBought(currentPlayer);
            game.addHistory(player.getUsername(), " chose to discard ", currentPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(selectedCard));
        } else if (cardAction.getCardName().equals("Golem")) {
            Card firstAction = cardMap.get(selectedCardIds.get(0));
            Card secondAction;
            if (cardAction.getCards().get(0).getCardId() == firstAction.getCardId()) {
                secondAction = cardAction.getCards().get(1);
            } else {
                secondAction = cardAction.getCards().get(0);
            }
            game.getGolemActions().push(secondAction);
            game.getGolemActions().push(firstAction);
            game.playGolemActionCard(player);
        } else if (cardAction.getCardName().equals("Graverobber")) {
            Player affectedPlayer = playerMap.get(cardAction.getPlayerId());
            game.addHistory("The Graverobber revealed ", KingdomUtil.getArticleWithCardName(cardAction.getCards().get(0)), " from ", affectedPlayer.getUsername(), "'s discard pile");
        } else if (cardAction.getCardName().equals("Haven")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            player.removeCardFromHand(selectedCard);
            player.getHavenCards().add(selectedCard);
        } else if (cardAction.getCardName().equals("Hooligans")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            player.removeCardFromHand(selectedCard);
            CardAction chooseDestinationCardAction = new CardAction(CardAction.TYPE_CHOICES);
            chooseDestinationCardAction.setDeck(Deck.Proletariat);
            chooseDestinationCardAction.setCardName(cardAction.getCardName());
            chooseDestinationCardAction.setAssociatedCard(selectedCard);
            chooseDestinationCardAction.setPlayerId(player.getUserId());
            chooseDestinationCardAction.getCards().add(selectedCard);
            chooseDestinationCardAction.setInstructions(player.getUsername() + " revealed " + KingdomUtil.getArticleWithCardName(selectedCard) + ". Do you want to discard it, or put it on top of " + player.getPronoun() + " deck?");
            chooseDestinationCardAction.getChoices().add(new CardActionChoice("Discard", "discard"));
            chooseDestinationCardAction.getChoices().add(new CardActionChoice("Top of deck", "deck"));
            game.setPlayerCardAction(game.getCurrentPlayer(), chooseDestinationCardAction);
        } else if (cardAction.getCardName().equals("Island")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            player.removeCardFromHand(selectedCard);
            player.getIslandCards().add(selectedCard);
        } else if (cardAction.getCardName().equals("Masquerade")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            player.removeCardFromHand(selectedCard);
            game.playerLostCard(player, selectedCard);
            game.refreshHand(player);
            game.getMasqueradeCards().put(player.getUserId(), selectedCard);
        } else if (cardAction.getCardName().equals("Mint")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            game.addHistory(player.getUsername(), " minted ", KingdomUtil.getArticleWithCardName(selectedCard));
            game.playerGainedCard(player, selectedCard);
        } else if (cardAction.getCardName().equals("Museum Trash Cards")) {
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                game.getTrashedCards().add(card);
                player.getMuseumCards().remove(card);
            }
            game.playerGainedCard(player, game.getDuchyCard());
            if (game.getPrizeCards().isEmpty()) {
                game.addHistory("There were no more prizes available");
            } else if (game.getPrizeCards().size() == 1) {
                game.playerGainedCard(player, game.getPrizeCards().get(0), false);
                game.getPrizeCards().clear();
            } else {
                CardAction choosePrizeCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS);
                choosePrizeCardAction.setDeck(Deck.Fan);
                choosePrizeCardAction.setCardName("Museum");
                choosePrizeCardAction.setNumCards(1);
                choosePrizeCardAction.setButtonValue("Done");
                choosePrizeCardAction.setInstructions("Select one of the following prize cards to gain and then click Done.");
                choosePrizeCardAction.getCards().addAll(game.getPrizeCards());
                game.setPlayerCardAction(player, choosePrizeCardAction);
            }
        } else if (cardAction.getCardName().equals("Pirate Ship")) {
            Player currentPlayer = game.getCurrentPlayer();
            Player affectedPlayer = playerMap.get(cardAction.getPlayerId());
            int selectedCardId = 0;
            if (selectedCardIds.size() > 0) {
                selectedCardId = selectedCardIds.get(0);
            }
            boolean foundSelectedCard = false;
            game.addHistory("The top two cards from ", affectedPlayer.getUsername(), "'s deck were ", cardAction.getCards().get(0).getName(), " and ", cardAction.getCards().get(1).getName());
            for (Card card : cardAction.getCards()) {
                card.setDisableSelect(false);
                if (!foundSelectedCard && card.getCardId() == selectedCardId) {
                    foundSelectedCard = true;
                    game.getTrashedTreasureCards().add(card);
                    game.getTrashedCards().add(card);
                    game.addHistory(currentPlayer.getUsername(), " trashed ", affectedPlayer.getUsername(), "'s ", card.getName());
                } else {
                    affectedPlayer.addCardToDiscard(card);
                    game.playerDiscardedCard(affectedPlayer, card);
                    game.refreshDiscard(affectedPlayer);
                }
            }
            if (game.getIncompleteCard().getExtraCardActions().isEmpty()) {
                if (game.getTrashedTreasureCards().size() > 0) {
                    currentPlayer.addPirateShipCoin();
                    game.addHistory(currentPlayer.getUsername(), " gained a Pirate Ship Coin, and now has ", KingdomUtil.getPlural(currentPlayer.getPirateShipCoins(), "Coin"));
                } else {
                    game.addHistory(currentPlayer.getUsername(), " did not gain a Pirate Ship Coin");
                }
                game.getTrashedTreasureCards().clear();
            }
        } else if (cardAction.getCardName().equals("Quest")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            cardAction.getAssociatedCard().getAssociatedCards().add(selectedCard);
            game.addHistory(player.getUsername(), " named ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " for ", player.getPronoun(), " ", KingdomUtil.getCardWithBackgroundColor(cardAction.getAssociatedCard()));
        } else if (cardAction.getCardName().equals("Setup Leaders")) {
            for (Integer selectedCardId : selectedCardIds) {
                Card selectedCard = cardMap.get(selectedCardId);
                player.getLeaders().add(new Card(selectedCard));
            }
        } else if (cardAction.getCardName().equals("Swindler")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            if (game.getSupply().get(selectedCard.getCardId()) == 0) {
                CardAction chooseAgainCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                chooseAgainCardAction.setDeck(Deck.Intrigue);
                chooseAgainCardAction.setNumCards(1);
                chooseAgainCardAction.setButtonValue("Done");
                chooseAgainCardAction.setCardName("Swindler");
                chooseAgainCardAction.setInstructions("The card you selected is no longer in the supply. Select one of the following cards and then click Done.");
                chooseAgainCardAction.setPlayerId(cardAction.getPlayerId());
                Card card = cardAction.getCards().get(0);
                int cost = card.getCost();
                for (Card c : game.getSupplyMap().values()) {
                    if (game.getCardCost(c) == cost && c.isCostIncludesPotion() == card.isCostIncludesPotion() && game.getSupply().get(c.getCardId()) > 0) {
                        chooseAgainCardAction.getCards().add(c);
                    }
                }
                if (chooseAgainCardAction.getCards().size() > 0) {
                    game.setPlayerCardAction(game.getCurrentPlayer(), chooseAgainCardAction);
                } else {
                    game.addHistory(game.getPlayers().get(cardAction.getPlayerId()).getUsername(), " did not have any cards in the supply to gain");
                }
            } else {
                Player cardActionPlayer = playerMap.get(cardAction.getPlayerId());
                game.playerGainedCard(cardActionPlayer, selectedCard);
                game.refreshDiscard(cardActionPlayer);
                game.addHistory("The Swindler gave ", cardActionPlayer.getUsername(), " ", KingdomUtil.getArticleWithCardName(selectedCard));
            }
        } else if (cardAction.getCardName().equals("Thief")) {
            Player currentPlayer = game.getCurrentPlayer();
            Player affectedPlayer = playerMap.get(cardAction.getPlayerId());
            int selectedCardId = 0;
            if (selectedCardIds.size() > 0) {
                selectedCardId = selectedCardIds.get(0);
            }
            boolean foundSelectedCard = false;
            if (cardAction.getCards().size() == 2) {
                game.addHistory("The top two cards from ", affectedPlayer.getUsername(), "'s deck were ", KingdomUtil.getCardWithBackgroundColor(cardAction.getCards().get(0)), " and ", KingdomUtil.getCardWithBackgroundColor(cardAction.getCards().get(1)));
            } else if (cardAction.getCards().size() == 1) {
                game.addHistory("The top card from ", affectedPlayer.getUsername(), "'s deck was ", KingdomUtil.getCardWithBackgroundColor(cardAction.getCards().get(0)));
            } else {
                game.addHistory(affectedPlayer.getUsername(), " did not have any cards in ", affectedPlayer.getPronoun(), " deck");
            }
            for (Card card : cardAction.getCards()) {
                card.setDisableSelect(false);
                if (!foundSelectedCard && card.getCardId() == selectedCardId) {
                    foundSelectedCard = true;
                    game.getTrashedTreasureCards().add(card);
                    game.addHistory(currentPlayer.getUsername(), " trashed ", affectedPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(card));
                } else {
                    affectedPlayer.addCardToDiscard(card);
                    game.playerDiscardedCard(affectedPlayer, card);
                    game.refreshDiscard(affectedPlayer);
                }
            }
            if (game.getIncompleteCard().getExtraCardActions().isEmpty()) {
                CardAction selectFromTrashedCardsAction = new CardAction(CardAction.TYPE_GAIN_CARDS_UP_TO);
                selectFromTrashedCardsAction.setDeck(Deck.Kingdom);
                selectFromTrashedCardsAction.setCardName("Thief");
                selectFromTrashedCardsAction.getCards().addAll(game.getTrashedTreasureCards());
                selectFromTrashedCardsAction.setNumCards(game.getTrashedTreasureCards().size());
                if (game.getTrashedTreasureCards().size() > 0) {
                    selectFromTrashedCardsAction.setInstructions("Select any of the trashed treasure cards you want to gain and then click Done.");
                } else {
                    selectFromTrashedCardsAction.setInstructions("There were no treasure cards trashed. Click Done.");
                }
                selectFromTrashedCardsAction.setButtonValue("Done");
                game.setPlayerCardAction(currentPlayer, selectFromTrashedCardsAction);
                game.getTrashedTreasureCards().clear();
            }
        } else if (cardAction.getCardName().equals("Throne Room")) {
            Card actionCard = player.getCardFromHandById(selectedCardIds.get(0));
            Card cardCopy;
            if (game.isCheckQuest() && actionCard.getName().equals("Quest")) {
                cardCopy = new Card(actionCard);
                game.setCopiedPlayedCard(true);
            } else {
                cardCopy = actionCard;
            }
            RepeatedAction action1 = new RepeatedAction(cardCopy);
            action1.setFirstAction(true);
            RepeatedAction action2 = new RepeatedAction(cardCopy);
            game.getRepeatedActions().push(action2);
            game.getRepeatedActions().push(action1);
            if (actionCard.isDuration()) {
                game.getDurationCardsPlayed().add(game.getThroneRoomCard());
            }
            game.addHistory(player.getUsername(), " throne roomed ", KingdomUtil.getArticleWithCardName(actionCard));
            game.playRepeatedAction(player, true);
        } else if (cardAction.getCardName().equals("Trainee")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            int combinedCost = game.getCardCost(selectedCard) + game.getCardCost(cardAction.getAssociatedCard());
            player.removeCardFromHand(selectedCard);
            game.removePlayedCard(cardAction.getAssociatedCard());
            game.addToSupply(selectedCard.getCardId());
            game.addToSupply(cardAction.getAssociatedCard().getCardId());
            game.refreshAllPlayersCardsPlayed();
            game.refreshAllPlayersSupply();
            game.addHistory(player.getUsername(), " returned ", KingdomUtil.getCardWithBackgroundColor(cardAction.getAssociatedCard()), " and ", KingdomUtil.getCardWithBackgroundColor(selectedCard), " to the supply");
            CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            gainCardAction.setDeck(Deck.Proletariat);
            gainCardAction.setCardName(cardAction.getCardName());
            gainCardAction.setButtonValue("Done");
            gainCardAction.setNumCards(1);
            gainCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            for (Card c : game.getSupplyMap().values()) {
                if (c.isAction() && game.getCardCost(c) <= combinedCost && (!c.isCostIncludesPotion() || selectedCard.isCostIncludesPotion() || cardAction.getAssociatedCard().isCostIncludesPotion()) && game.isCardInSupply(c)) {
                    gainCardAction.getCards().add(c);
                }
            }
            if (gainCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, gainCardAction);
            }
        } else if (cardAction.getCardName().equals("Wishing Well")) {
            Card selectedCard = cardMap.get(selectedCardIds.get(0));
            Card topDeckCard = player.lookAtTopDeckCard();
            if (topDeckCard != null) {
                boolean guessedRight = (selectedCard.getCardId() == topDeckCard.getCardId());
                if (guessedRight) {
                    player.drawCards(1);
                    game.addHistory(player.getUsername(), " correctly guessed the top card of ", player.getPronoun(), " deck was ", KingdomUtil.getArticleWithCardName(selectedCard));
                } else {
                    game.addHistory(player.getUsername(), " guessed the top card of ", player.getPronoun(), " deck was ", KingdomUtil.getArticleWithCardName(selectedCard), ", but it was ", KingdomUtil.getArticleWithCardName(topDeckCard));
                }
            }
        }

        return incompleteCard;
    }
}
