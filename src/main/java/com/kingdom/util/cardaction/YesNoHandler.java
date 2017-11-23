package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YesNoHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, String yesNoAnswer) {

        Map<Integer, Integer> supply = game.getSupply();
        Map<Integer, Player> playerMap = game.getPlayerMap();
        IncompleteCard incompleteCard = null;

        if (cardAction.getCardName().equals("Baron")) {
            if (yesNoAnswer.equals("yes")) {
                player.discardCardFromHand(Card.ESTATE_ID);
                player.addCoins(4);
                game.addHistory(player.getUsername(), " discarded an ", KingdomUtil.getWordWithBackgroundColor("Estate", Card.VICTORY_COLOR), " and got +4 coins");
            }
            else {
                if (game.isCardInSupply(Card.ESTATE_ID)) {
                    game.playerGainedCard(player, game.getEstateCard());
                    game.refreshDiscard(player);
                }
            }
        }
        else if (cardAction.getCardName().equals("Black Market")) {
            if (yesNoAnswer.equals("yes")) {
                if (!player.getTreasureCards().isEmpty()) {
                    CardAction playTreasureCardsAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    playTreasureCardsAction.setDeck(Card.DECK_PROMO);
                    playTreasureCardsAction.setHideOnSelect(true);
                    playTreasureCardsAction.setNumCards(-1);
                    playTreasureCardsAction.setCardName("Black Market Treasure");
                    playTreasureCardsAction.setCards(player.getTreasureCards());
                    playTreasureCardsAction.setButtonValue("Done");
                    playTreasureCardsAction.setInstructions("Click the treasure cards you want to play in the order you want to play them, and then click Done.");
                    game.setPlayerCardAction(player, playTreasureCardsAction);
                }
                else {
                    incompleteCard = new SinglePlayerIncompleteCard("Black Market", game);
                    game.addNextAction("Buy Card");
                    incompleteCard.setPlayerActionCompleted(player.getUserId());
                }
            }
            else {
                game.addHistory(player.getUsername(), " chose to not buy a card from the black market deck");
                CardAction chooseOrderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                chooseOrderCardAction.setDeck(Card.DECK_PROMO);
                chooseOrderCardAction.setHideOnSelect(true);
                chooseOrderCardAction.setNumCards(cardAction.getCards().size());
                chooseOrderCardAction.setCardName("Black Market");
                chooseOrderCardAction.setCards(cardAction.getCards());
                chooseOrderCardAction.setButtonValue("Done");
                chooseOrderCardAction.setInstructions("Click the cards in the order you want them to be on the bottom of the black market deck, starting with the top card and then click Done. (The last card you click will be the bottom card of the black market deck)");
                game.setPlayerCardAction(player, chooseOrderCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Chancellor")) {
            if (yesNoAnswer.equals("yes")) {
                player.getDiscard().addAll(player.getDeck());
                player.getDeck().clear();
                game.addHistory(player.getUsername(), " added ", player.getPronoun(), " deck to ", player.getPronoun(), " discard");
            }
        }
        else if (cardAction.getCardName().equals("City Planner")) {
            if (yesNoAnswer.equals("yes")) {
                player.subtractCoins(2);
                game.refreshAllPlayersCardsBought();

                if (KingdomUtil.uniqueCardList(player.getVictoryCards()).size() == 1) {
                    Card victoryCard = player.getVictoryCards().get(0);
                    player.removeCardFromHand(victoryCard);
                    player.getCityPlannerCards().add(victoryCard);
                    game.addHistory(player.getUsername(), " paid an extra $2 to set aside ", KingdomUtil.getArticleWithCardName(victoryCard));
                }
                else {
                    CardAction chooseVictoryCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                    chooseVictoryCardAction.setDeck(Card.DECK_PROLETARIAT);
                    chooseVictoryCardAction.setCardName(cardAction.getCardName());
                    chooseVictoryCardAction.getCards().addAll(player.getVictoryCards());
                    chooseVictoryCardAction.setNumCards(1);
                    chooseVictoryCardAction.setInstructions("Choose a victory card to set aside and then click Done.");
                    chooseVictoryCardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, chooseVictoryCardAction);
                }
            }
        }
        else if (cardAction.getCardName().equals("Confirm Buy")) {
            if (yesNoAnswer.equals("yes")) {
                game.removeProcessingClick(player);
                game.cardClicked(player, "supply", cardAction.getCards().get(0), false);
            }
        }
        else if (cardAction.getCardName().equals("Confirm End Turn")) {
            if (yesNoAnswer.equals("yes")) {
                game.removeProcessingClick(player);
                game.endPlayerTurn(player, false);
            }
        }
        else if (cardAction.getCardName().equals("Confirm Play Treasure Card")) {
            if (yesNoAnswer.equals("yes")) {
                game.removeProcessingClick(player);
                game.cardClicked(player, "hand", cardAction.getCards().get(0), false);
            }
        }
        else if (cardAction.getCardName().equals("Confirm Play Treasure Cards")) {
            if (yesNoAnswer.equals("yes")) {
                game.removeProcessingClick(player);
                game.playAllTreasureCards(player, false);
            }
        }
        else if (cardAction.getCardName().equals("Duchess for Duchy")) {
            if (yesNoAnswer.equals("yes")) {
                game.playerGainedCard(player, cardAction.getCards().get(0));
            }
        }
        else if (cardAction.getCardName().equals("Enchanted Palace")) {
            if (yesNoAnswer.equals("yes")) {
                player.drawCards(2);
                game.playerRevealedEnchantedPalace(player.getUserId());
            }
        }
        else if (cardAction.getCardName().equals("Fool's Gold")) {
            if (yesNoAnswer.equals("yes")) {
                Card foolsGoldCard = game.getFoolsGoldCard();
                game.addHistory(player.getUsername(), " revealed and trashed ", KingdomUtil.getCardWithBackgroundColor(foolsGoldCard));
                player.removeCardFromHand(foolsGoldCard);
                game.getTrashedCards().add(foolsGoldCard);
                if (game.isCardInSupply(game.getGoldCard())) {
                    game.playerGainedCardToTopOfDeck(player, game.getGoldCard());
                }
                else {
                    game.addHistory("There were no more Gold cards in the supply");
                }
            }
            if (!player.isShowCardAction()) {
                game.getPlayersWithCardActions().remove(player.getUserId());
            }
        }
        else if (cardAction.getCardName().equals("Graverobber")) {
            Player affectedPlayer = playerMap.get(cardAction.getPlayerId());
            game.addHistory("The Graverobber revealed ", KingdomUtil.getArticleWithCardName(cardAction.getCards().get(0)), " from ", affectedPlayer.getUsername(), "'s discard pile");
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " chose to gain the revealed treasure card");
                if (affectedPlayer.getDiscard().get(cardAction.getCardId()).getCardId() == cardAction.getCards().get(0).getCardId()) {
                    affectedPlayer.getDiscard().remove(cardAction.getCardId());
                }
                else {
                    GameError error = new GameError(GameError.GAME_ERROR, "Card in discard pile does not match expected Graverobber revealed card");
                    game.logError(error, false);
                }
                game.playerGainedCard(player, cardAction.getCards().get(0), false);
            }
            else {
                game.addHistory(player.getUsername(), " did not choose to gain the revealed treasure card");
            }
            if (!game.getIncompleteCard().getExtraCardActions().isEmpty()) {
                CardAction nextPlayerCardAction = game.getIncompleteCard().getExtraCardActions().peek();
                //need to update index if card removed index was before next card's index
                if (yesNoAnswer.equals("yes") && nextPlayerCardAction.getPlayerId() == cardAction.getPlayerId() && cardAction.getCardId() < nextPlayerCardAction.getCardId()) {
                    nextPlayerCardAction.setCardId(nextPlayerCardAction.getCardId()-1);
                }
            }
        }
        else if (cardAction.getCardName().equals("Hamlet")) {
            if (yesNoAnswer.equals("yes")) {
                CardAction discardCardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                discardCardAction.setDeck(Card.DECK_CORNUCOPIA);
                discardCardAction.setCardName(cardAction.getCardName());
                discardCardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                discardCardAction.setNumCards(1);
                discardCardAction.setInstructions("Select the card you want to discard and then click Done.");
                discardCardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, discardCardAction);
            }
            else {
                game.addHistory(player.getUsername(), " chose not to discard a card for +1 Action");
                incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
                game.addNextAction("discard for buy");
            }
        }
        else if (cardAction.getCardName().equals("Hamlet2")) {
            if (yesNoAnswer.equals("yes")) {
                CardAction discardCardAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                discardCardAction.setDeck(Card.DECK_CORNUCOPIA);
                discardCardAction.setCardName(cardAction.getCardName());
                discardCardAction.setCards(KingdomUtil.uniqueCardList(player.getHand()));
                discardCardAction.setNumCards(1);
                discardCardAction.setInstructions("Select the card you want to discard and then click Done.");
                discardCardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, discardCardAction);
            }
            else {
                game.addHistory(player.getUsername(), " chose not to discard a card for +1 Buy");
            }
        }
        else if (cardAction.getCardName().equals("Horse Traders")) {
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " set aside ", KingdomUtil.getWordWithBackgroundColor("Horse Traders", Card.ACTION_REACTION_COLOR));
                player.setAsideCardFromHand(game.getHorseTradersCard());
            }
        }
        else if (cardAction.getCardName().equals("Ill-Gotten Gains")) {
            if (yesNoAnswer.equals("yes")) {
                if (game.isCardInSupply(game.getCopperCard())) {
                    game.playerGainedCardToHand(player, game.getCopperCard());
                }
            }
        }
        else if (cardAction.getCardName().equals("Library")) {
            Card card = cardAction.getCards().get(0);
            if (yesNoAnswer.equals("yes")) {
                game.getSetAsideCards().add(card);
                game.addHistory(player.getUsername(), " set aside ", KingdomUtil.getArticleWithCardName(card));
            }
            else {
                player.addCardToHand(card);
            }
            boolean noTopCard = false;
            while (player.getHand().size() < 7) {
                Card topCard = player.removeTopDeckCard();
                if (topCard == null) {
                    noTopCard = true;
                    break;
                }
                if (topCard.isAction()) {
                    CardAction libraryCardAction = new CardAction(CardAction.TYPE_YES_NO);
                    libraryCardAction.setDeck(Card.DECK_KINGDOM);
                    libraryCardAction.setCardName("Library");
                    libraryCardAction.getCards().add(topCard);
                    libraryCardAction.setInstructions("Do you want to set aside this action card?");
                    game.setPlayerCardAction(player, libraryCardAction);
                    break;
                }
                else {
                    player.addCardToHand(topCard);
                }
            }
            if (player.getHand().size() == 7 || noTopCard) {
                for (Card setAsideCard : game.getSetAsideCards()) {
                    player.addCardToDiscard(setAsideCard);
                    game.playerDiscardedCard(player, setAsideCard);
                }
                game.getSetAsideCards().clear();
            }
        }
        else if (cardAction.getCardName().equals("Mining Village")) {
            if (yesNoAnswer.equals("yes")) {
                Card miningVillageCard = cardAction.getCards().get(0);
                game.removePlayedCard(miningVillageCard);
                game.getTrashedCards().add(miningVillageCard);
                game.playerLostCard(player, miningVillageCard);
                player.addCoins(2);
                game.addHistory(player.getUsername(), " trashed a ", KingdomUtil.getWordWithBackgroundColor("Mining Village", Card.ACTION_COLOR), " to get +2 coins");
                game.refreshAllPlayersCardsPlayed();
            }
        }
        else if (cardAction.getCardName().equals("Museum Trash Cards")) {
            if (yesNoAnswer.equals("yes")) {
                if (player.getMuseumCards().size() == 4) {
                    game.getTrashedCards().addAll(player.getMuseumCards());
                    player.getMuseumCards().clear();
                    game.addHistory(player.getUsername(), " trashed 4 cards from ", player.getPronoun(), " Museum mat");
                    game.playerGainedCard(player, game.getDuchyCard());
                    if (game.getPrizeCards().isEmpty()) {
                        game.addHistory("There were no more prizes available");
                    }
                    else if (game.getPrizeCards().size() == 1) {
                        game.playerGainedCard(player, game.getPrizeCards().get(0), false);
                        game.getPrizeCards().clear();
                    }
                    else {
                        CardAction choosePrizeCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS);
                        choosePrizeCardAction.setDeck(Card.DECK_FAN);
                        choosePrizeCardAction.setCardName("Museum");
                        choosePrizeCardAction.setNumCards(1);
                        choosePrizeCardAction.setButtonValue("Done");
                        choosePrizeCardAction.setInstructions("Select one of the following prize cards to gain and then click Done.");
                        choosePrizeCardAction.getCards().addAll(game.getPrizeCards());
                        game.setPlayerCardAction(player, choosePrizeCardAction);
                    }
                }
                else {
                    CardAction museumCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                    museumCardAction.setDeck(Card.DECK_FAN);
                    museumCardAction.setCardName("Museum Trash Cards");
                    museumCardAction.getCards().addAll(player.getMuseumCards());
                    museumCardAction.setNumCards(4);
                    museumCardAction.setInstructions("Select 4 cards to trash from your Museum mat and then click Done.");
                    museumCardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, museumCardAction);
                }
            }
        }
        else if (cardAction.getCardName().equals("Orchard")) {
            if (yesNoAnswer.equals("yes")) {
                player.subtractCoins(2);
                player.addFruitTokens(2);
                game.refreshAllPlayersCardsBought();
                game.addHistory(player.getUsername(), " paid an extra two coins to gain two fruit tokens");
            }
        }
        else if (cardAction.getCardName().equals("Pearl Diver")) {
            if (yesNoAnswer.equals("yes")) {
                Card bottomCard = player.getDeck().remove(player.getDeck().size() - 1);
                player.addCardToTopOfDeck(bottomCard);
                game.addHistory(player.getUsername(), " put the bottom card of ", player.getPronoun(), " deck on top of ", player.getPronoun(), " deck");
            }
            else {
                game.addHistory(player.getUsername(), " chose to keep the card on the bottom of ", player.getPronoun(), " deck");
            }
        }
        else if (cardAction.getCardName().equals("Royal Seal")) {
            incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
            Card cardToGain = cardAction.getCards().get(0);
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " used ", KingdomUtil.getWordWithBackgroundColor("Royal Seal", Card.TREASURE_COLOR), " to add the gained card to the top of ", player.getPronoun(), " deck");
                game.moveGainedCard(player, cardToGain, "deck");
            }
            else {
                if (cardAction.getDestination().equals("hand")) {
                    game.playerGainedCardToHand(player, cardToGain);
                }
                else if (cardAction.getDestination().equals("discard")) {
                    game.playerGainedCard(player, cardToGain);
                }
            }
            if (player.getBuys() == 0 && !player.isComputer() && !player.isShowCardAction() && player.getExtraCardActions().isEmpty() && !game.hasUnfinishedGainCardActions()) {
                incompleteCard.setEndTurn(true);
            }
        }
        else if (cardAction.getCardName().equals("Scrying Pool")) {
            Player cardActionPlayer = playerMap.get(cardAction.getPlayerId());
            Card topDeckCard = cardActionPlayer.lookAtTopDeckCard();
            if (topDeckCard != null) {
                game.addHistory("The top card of ", cardActionPlayer.getUsername(), "'s deck was ", KingdomUtil.getArticleWithCardName(topDeckCard));
                if (yesNoAnswer.equals("yes")) {
                    cardActionPlayer.getDeck().remove(0);
                    cardActionPlayer.addCardToDiscard(topDeckCard);
                    game.playerDiscardedCard(cardActionPlayer, topDeckCard);
                    game.refreshDiscard(cardActionPlayer);
                    game.addHistory(game.getCurrentPlayer().getUsername(), " decided to discard the card");
                }
                else {
                    game.addHistory(game.getCurrentPlayer().getUsername(), " decided to keep the card on top of ", player.getPronoun(), " deck");
                }
            }
            else {
                game.addHistory(game.getCurrentPlayer().getUsername(), " did not have a card to draw");
            }
            if (game.getIncompleteCard().getExtraCardActions().isEmpty()) {
                Player currentPlayer = game.getCurrentPlayer();
                List<Card> revealedCards = new ArrayList<Card>();
                boolean foundNonActionCard = false;
                while (!foundNonActionCard) {
                    topDeckCard = currentPlayer.removeTopDeckCard();
                    if (topDeckCard == null) {
                        break;
                    }
                    revealedCards.add(topDeckCard);
                    if (!topDeckCard.isAction()) {
                        foundNonActionCard = true;
                    }
                    currentPlayer.addCardToHand(topDeckCard);
                }
                if (!revealedCards.isEmpty()) {
                    game.addHistory(currentPlayer.getUsername(), " revealed ", KingdomUtil.groupCards(revealedCards, true));
                }
            }
        }
        else if (cardAction.getCardName().equals("Secret Chamber")) {
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " is using a ", KingdomUtil.getWordWithBackgroundColor("Secret Chamber", Card.ACTION_REACTION_COLOR));
                player.drawCards(2);
                CardAction secretChamberAction = new CardAction(CardAction.TYPE_CARDS_FROM_HAND_TO_TOP_OF_DECK);
                secretChamberAction.setDeck(Card.DECK_INTRIGUE);
                secretChamberAction.setCardName("Secret Chamber");
                secretChamberAction.getCards().addAll(player.getHand());
                secretChamberAction.setButtonValue("Done");
                secretChamberAction.setNumCards(2);
                secretChamberAction.setInstructions("Select two cards from your hand to put on top of your deck.");
                game.setPlayerCardAction(player, secretChamberAction);
            }
        }
        else if (cardAction.getCardName().equals("Shepherd")) {
            if (yesNoAnswer.equals("yes")) {
                player.subtractCoins(2);
                player.addCattleTokens(2);
                game.refreshAllPlayersCardsBought();
                game.addHistory(player.getUsername(), " paid an extra $2 to gain 2 cattle tokens");
            }
        }
        else if (cardAction.getCardName().equals("Spy")) {
            Player cardActionPlayer = playerMap.get(cardAction.getPlayerId());
            Card topDeckCard = cardActionPlayer.lookAtTopDeckCard();
            if (topDeckCard != null) {
                game.addHistory("The top card of ", cardActionPlayer.getUsername(), "'s deck was ", KingdomUtil.getArticleWithCardName(topDeckCard));
                if (yesNoAnswer.equals("yes")) {
                    cardActionPlayer.getDeck().remove(0);
                    cardActionPlayer.addCardToDiscard(topDeckCard);
                    game.playerDiscardedCard(cardActionPlayer, topDeckCard);
                    game.refreshDiscard(cardActionPlayer);
                    game.addHistory(game.getCurrentPlayer().getUsername(), " decided to discard the card");
                }
                else {
                    game.addHistory(game.getCurrentPlayer().getUsername(), " decided to keep the card on top of ", cardActionPlayer.getPronoun(), " deck");
                }
            }
            else {
                game.addHistory(game.getCurrentPlayer().getUsername(), " did not have a card to draw");
            }
        }
        else if (cardAction.getCardName().equals("Squatter")) {
            Card squatterCard = cardAction.getAssociatedCard();
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " returned ", KingdomUtil.getCardWithBackgroundColor(squatterCard), " to the supply");
                cardAction.setGainCardAfterBuyAction(false);

                int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
                while (playerIndex != game.getCurrentPlayerIndex()) {
                    Player nextPlayer = game.getPlayers().get(playerIndex);
                    if(game.isCardInSupply(squatterCard)) {
                        game.playerGainedCard(nextPlayer, game.getCardMap().get(squatterCard.getCardId()));
                        game.refreshDiscard(nextPlayer);
                    }
                    playerIndex = game.calculateNextPlayerIndex(playerIndex);
                }
            }
        }
        else if (cardAction.getCardName().equals("Tinker")) {
            Card cardToGain = cardAction.getCards().get(0);
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " put ", KingdomUtil.getArticleWithCardName(cardToGain), " under ", player.getPronoun(), " ", KingdomUtil.getWordWithBackgroundColor("Tinker", Card.ACTION_DURATION_COLOR));
                player.getTinkerCards().add(cardToGain);
                game.moveGainedCard(player, cardToGain, "tinker");
            }
            else {
                if (cardAction.getDestination().equals("hand")) {
                    game.playerGainedCardToHand(player, cardToGain);
                }
                else if (cardAction.getDestination().equals("deck")) {
                    game.playerGainedCardToTopOfDeck(player, cardToGain);
                }
                else if (cardAction.getDestination().equals("discard")) {
                    game.playerGainedCard(player, cardToGain);
                }
            }
        }
        else if (cardAction.getCardName().equals("Tournament")) {
            if (yesNoAnswer.equals("yes")) {
                if (player.getUserId() == game.getCurrentPlayerId()) {
                    player.discardCardFromHand(game.getProvinceCard());
                    game.addHistory(player.getUsername(), " revealed and discarded a ", KingdomUtil.getCardWithBackgroundColor(game.getProvinceCard()));
                    CardAction chooseType = new CardAction(CardAction.TYPE_CHOICES);
                    chooseType.setDeck(Card.DECK_CORNUCOPIA);
                    chooseType.setCardName(cardAction.getCardName());
                    chooseType.setInstructions("Available Prizes: "+game.getPrizeCardsString()+". Do you want to gain a Prize or a Duchy?");
                    chooseType.getChoices().add(new CardActionChoice("Prize", "prize"));
                    chooseType.getChoices().add(new CardActionChoice("Duchy", "duchy"));
                    game.setPlayerCardAction(player, chooseType);
                }
                else {
                    game.setGainTournamentBonus(false);
                    game.addHistory(player.getUsername(), " revealed a ", KingdomUtil.getCardWithBackgroundColor(game.getProvinceCard()));
                }
            }
        }
        else if (cardAction.getCardName().equals("Tunnel")) {
            if (yesNoAnswer.equals("yes")) {
                game.addHistory(player.getUsername(), " revealed a ", KingdomUtil.getCardWithBackgroundColor(cardAction.getAssociatedCard()));
                if (game.isCardInSupply(game.getGoldCard())) {
                    game.playerGainedCard(player, game.getGoldCard());
                }
                else {
                    game.addHistory("There were no more Gold cards in the supply");
                }
            }
            game.finishTunnelCardAction(player);
        }
        else if (cardAction.getCardName().equals("Vault")) {
            if (yesNoAnswer.equals("yes")) {
                if (player.getHand().size() == 1) {
                    for (Card c : player.getHand()) {
                        game.playerDiscardedCard(player, c);
                    }
                    player.discardHand();
                    game.addHistory(player.getUsername(), " discarded the last card from ", player.getPronoun(), " hand");
                }
                else {
                    CardAction discardCardsAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    discardCardsAction.setDeck(Card.DECK_PROSPERITY);
                    discardCardsAction.setCardName("Vault2");
                    discardCardsAction.getCards().addAll(player.getHand());
                    discardCardsAction.setButtonValue("Done");
                    discardCardsAction.setNumCards(2);
                    discardCardsAction.setInstructions("Select two cards from your hand to discard.");
                    game.setPlayerCardAction(player, discardCardsAction);
                }
            }
            else {
                game.addHistory(player.getUsername(), " chose not to discard");
            }
        }
        else if (cardAction.getCardName().equals("Walled Village")) {
            incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
            if (yesNoAnswer.equals("yes")) {
                Card walledVillage = cardAction.getCards().get(0);
                game.removePlayedCard(walledVillage);
                player.addCardToTopOfDeck(walledVillage);
                game.addHistory(player.getUsername(), " added ", KingdomUtil.getCardWithBackgroundColor(walledVillage)," to the top of ",player.getPronoun()," deck");
            }
            incompleteCard.setEndTurn(true);
        }

        return incompleteCard;
    }
}
