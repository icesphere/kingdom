package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;

public class ChoicesHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, String choice) {

        List<Player> players = game.getPlayers();
        IncompleteCard incompleteCard = null;

        if (cardAction.getCardName().equals("Archbishop")) {
            if (choice.equals("actions")) {
                player.addActions(2);
                game.addHistory(player.getUsername(), " chose +2 Actions");
                game.refreshAllPlayersCardsPlayed();
            }
            else if (choice.equals("remove")) {
                player.addSins(-1);
                game.refreshAllPlayersPlayers();
                game.addHistory(player.getUsername(), " chose to remove 1 sin");
                game.refreshAllPlayersCardsPlayed();
            }
            else if (choice.equals("sins")) {
                game.addHistory(player.getUsername(), " chose for all other players to gain 1 sin");
                for (Player otherPlayer : game.getPlayers()) {
                    if (otherPlayer.getUserId() != game.getCurrentPlayerId()) {
                        otherPlayer.addSins(1);
                        game.refreshHandArea(otherPlayer);
                    }
                }
                game.refreshAllPlayersPlayers();
            }
        }
        else if (cardAction.getCardName().equals("Archivist")) {
            if (choice.equals("draw")) {
                while (player.getHand().size() < 6) {
                    Card topCard = player.removeTopDeckCard();
                    if (topCard == null) {
                        break;
                    }
                    player.addCardToHand(topCard);
                }
                game.refreshHand(player);
                game.addHistory(player.getUsername(), " chose to draw until 6 cards in hand");
            }
            else if (choice.equals("discard")) {
                player.addCoins(1);
                game.addHistory(player.getUsername(), " chose +$1 and discard");
                if (!player.getHand().isEmpty()) {
                    CardAction discardCardAction = new CardAction(CardAction.TYPE_DISCARD_AT_LEAST_FROM_HAND);
                    discardCardAction.setDeck(Card.DECK_FAN);
                    discardCardAction.setCardName(cardAction.getCardName());
                    discardCardAction.setCards(player.getHand());
                    discardCardAction.setNumCards(1);
                    discardCardAction.setInstructions("Select 1 or more cards to discard from your hand and then click Done.");
                    discardCardAction.setButtonValue("Done");
                    game.setPlayerCardAction(player, discardCardAction);
                }
            }
        }
        else if (cardAction.getCardName().equals("Bell Tower")) {
            if (choice.equals("before")) {
                player.drawCards(2);
                game.addHistory(player.getUsername(), " revealed "+player.getPronoun()+" "+ KingdomUtil.getWordWithBackgroundColor("Bell Tower", Card.ACTION_REACTION_COLOR)+" to gain +2 Cards before the attack");
            }
            else if (choice.equals("after")) {
                game.getPlayersWaitingForBellTowerBonus().add(player);
            }
        }
        else if (cardAction.getCardName().equals("Cattle Farm")) {
            if (choice.equals("discard")) {
                game.addHistory(player.getUsername(), " discarded the top card of ", player.getPronoun(), " deck");
                player.addCardToDiscard(cardAction.getAssociatedCard());
                game.playerDiscardedCard(player, cardAction.getAssociatedCard());
            }
            else if (choice.equals("back")) {
                game.addHistory(player.getUsername(), " put back the top card of ", player.getPronoun(), " deck");
                player.addCardToTopOfDeck(cardAction.getAssociatedCard());
            }
        }
        else if (cardAction.getCardName().equals("Choose Reaction")) {
            Card cardToGain = cardAction.getAssociatedCard();
            CardAction reaction = cardToGain.getGainCardActions().remove(choice);
            if (reaction != null) {
                game.setPlayerCardAction(player, reaction);
            }
        }
        else if (cardAction.getCardName().equals("Develop")) {
            CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            gainCardAction.setDeck(Card.DECK_HINTERLANDS);
            gainCardAction.setCardName(cardAction.getCardName());
            gainCardAction.setButtonValue("Done");
            gainCardAction.setNumCards(1);
            gainCardAction.setAssociatedCard(cardAction.getAssociatedCard());
            gainCardAction.setInstructions("Select one of the following cards to gain and then click Done.");

            List<Card> cards = new ArrayList<Card>();
            int cost = game.getCardCost(gainCardAction.getAssociatedCard());
            if (choice.equals("more")) {
                cost = cost + 1;
                gainCardAction.setPhase(1);
            }
            else if (choice.equals("less")) {
                cost = cost - 1;
                gainCardAction.setPhase(2);
            }
            for (Card c : game.getSupplyMap().values()) {
                if (game.getCardCost(c) == cost && cardAction.getAssociatedCard().isCostIncludesPotion() == c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    cards.add(c);
                }
            }

            gainCardAction.setCards(cards);
            game.setPlayerCardAction(player, gainCardAction);
        }
        else if (cardAction.getCardName().equals("Duchess")) {
            if (choice.equals("discard")) {
                game.addHistory(player.getUsername(), " discarded the top card of ", player.getPronoun(), " deck");
                player.addCardToDiscard(cardAction.getAssociatedCard());
                game.playerDiscardedCard(player, cardAction.getAssociatedCard());
            }
            else if (choice.equals("back")) {
                game.addHistory(player.getUsername(), " put back the top card of ", player.getPronoun(), " deck");
                player.addCardToTopOfDeck(cardAction.getAssociatedCard());
            }
        }
        else if (cardAction.getCardName().equals("Explorer")) {
            if (choice.equals("gold")) {
                if (game.isCardInSupply(Card.GOLD_ID)) {
                    game.playerGainedCardToHand(player, game.getGoldCard());
                    game.refreshHand(player);
                }
            }
            else if (choice.equals("silver")) {
                if (game.isCardInSupply(Card.SILVER_ID)) {
                    game.playerGainedCardToHand(player, game.getSilverCard());
                    game.refreshHand(player);
                }
            }
        }
        else if (cardAction.getCardName().equals("Governor")) {
            if (choice.equals("cards")) {
                game.addHistory(player.getUsername(), " chose to gain cards");
                for (Player p : game.getPlayers()) {
                    if (game.isCurrentPlayer(p)) {
                        p.drawCards(3);
                    }
                    else {
                        p.drawCards(1);
                    }
                }
                game.refreshAllPlayersHand();
            }
            else if (choice.equals("money")) {
                game.addHistory(player.getUsername(), " chose Silver and Gold");
                if(game.getSupply().get(Card.GOLD_ID) > 0) {
                    game.playerGainedCard(player, game.getGoldCard());
                }
                int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
                while (playerIndex != game.getCurrentPlayerIndex()) {
                    Player nextPlayer = players.get(playerIndex);
                    if(game.getSupply().get(Card.SILVER_ID) > 0) {
                        game.playerGainedCard(nextPlayer, game.getSilverCard());
                    }
                    playerIndex = game.calculateNextPlayerIndex(playerIndex);
                }
                game.refreshAllPlayersDiscard();
            }
            else if (choice.equals("trash")) {
                game.addHistory(player.getUsername(), " chose to trash and gain cards");
                //todo do in player order
                incompleteCard = new MultiPlayerIncompleteCard(cardAction.getCardName(), game, true);
                for (Player p : game.getPlayers()) {
                    if (!p.getHand().isEmpty()) {
                        int addToCost = 1;
                        if (game.isCurrentPlayer(p)) {
                            addToCost = 2;
                        }
                        CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                        trashCardAction.setDeck(Card.DECK_PROMO);
                        trashCardAction.setCardName(cardAction.getCardName());
                        trashCardAction.setCards(KingdomUtil.uniqueCardList(p.getHand()));
                        trashCardAction.setNumCards(1);
                        trashCardAction.setInstructions("Select a card to trash in order to gain a card costing exactly "+ KingdomUtil.getPlural(addToCost, "coin")+"  more and then click Done, or just click Done if you don't want to trash a card.");
                        trashCardAction.setButtonValue("Done");
                        game.setPlayerCardAction(p, trashCardAction);
                    }
                    else {
                        incompleteCard.setPlayerActionCompleted(p.getUserId());
                    }
                }
                incompleteCard.allActionsSet();
            }
        }
        else if (cardAction.getCardName().equals("Hooligans")) {
            Player affectedPlayer = game.getPlayerMap().get(cardAction.getPlayerId());
            if (choice.equals("discard")) {
                game.addHistory(player.getUsername(), " discarded ", affectedPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(cardAction.getAssociatedCard()));
                affectedPlayer.addCardToDiscard(cardAction.getAssociatedCard());
                game.playerDiscardedCard(affectedPlayer, cardAction.getAssociatedCard());
            }
            else if (choice.equals("deck")) {
                game.addHistory(player.getUsername(), " put the selected card on top of ", affectedPlayer.getUsername(), "'s deck");
                affectedPlayer.addCardToTopOfDeck(cardAction.getAssociatedCard());
            }
        }
        else if (cardAction.getCardName().equals("Jack of all Trades")) {
            if (choice.equals("discard")) {
                game.addHistory(player.getUsername(), " discarded the top card of ", player.getPronoun(), " deck");
                player.addCardToDiscard(cardAction.getAssociatedCard());
                game.playerDiscardedCard(player, cardAction.getAssociatedCard());
            }
            else if (choice.equals("back")) {
                game.addHistory(player.getUsername(), " put back the top card of ", player.getPronoun(), " deck");
                player.addCardToTopOfDeck(cardAction.getAssociatedCard());
            }
            while (player.getHand().size() < 5) {
                Card topCard = player.removeTopDeckCard();
                if (topCard == null) {
                    break;
                }
                player.addCardToHand(topCard);
            }
            game.refreshHand(player);
            List<Card> cards = new ArrayList<Card>();
            for (Card c : player.getHand()) {
                if (!c.isTreasure()) {
                    cards.add(c);
                }
            }
            if (!cards.isEmpty()) {
                CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_UP_TO_FROM_HAND);
                trashCardAction.setDeck(Card.DECK_HINTERLANDS);
                trashCardAction.setCardName(cardAction.getCardName());
                trashCardAction.setNumCards(1);
                trashCardAction.getCards().addAll(KingdomUtil.uniqueCardList(cards));
                trashCardAction.setInstructions("Select a card to trash from your hand and then click Done, or just click Done if you don't want to trash a card.");
                trashCardAction.setButtonValue("Done");
                game.setPlayerCardAction(player, trashCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Jester")) {
            Card cardToGain = cardAction.getCards().get(0);
            if (choice.equals("me")) {
                if (game.isCardInSupply(cardToGain)) {
                    game.playerGainedCard(player, cardToGain);
                }
                else {
                    game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(cardToGain));
                }
            }
            else if(choice.equals("them")) {
                Player affectedPlayer = game.getPlayerMap().get(cardAction.getPlayerId());
                if (game.isCardInSupply(cardToGain)) {
                    game.playerGainedCard(affectedPlayer, cardToGain);
                    game.refreshDiscard(affectedPlayer);
                }
                else {
                    game.addHistory("The supply did not have ", KingdomUtil.getArticleWithCardName(cardToGain));
                }
            }
        }
        else if (cardAction.getCardName().equals("Loan")) {
            Card treasureCard = cardAction.getCards().get(0);
            if (choice.equals("discard")) {
                player.addCardToDiscard(treasureCard);
                game.addHistory(player.getUsername(), " discarded ", KingdomUtil.getArticleWithCardName(treasureCard));
            }
            else if (choice.equals("trash")) {
                game.getTrashedCards().add(treasureCard);
                game.addHistory(player.getUsername(), " trashed ", KingdomUtil.getArticleWithCardName(treasureCard));
                game.playerLostCard(player, treasureCard);
            }
        }
        else if (cardAction.getCardName().equals("Lost Village 1")) {
            if (choice.equals("actions")) {
                player.addActions(2);
                game.addHistory(player.getUsername(), " chose +2 Actions");
            }
            else if (choice.equals("draw")) {
                player.addActions(1);
                game.addHistory(player.getUsername(), " chose +1 Action and set aside cards until you choose to draw one.");
                CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                nextCardAction.setDeck(Card.DECK_FAIRYTALE);
                nextCardAction.setCardName("Lost Village");
                nextCardAction.setInstructions("Draw or set aside top card?");
                nextCardAction.getChoices().add(new CardActionChoice("Draw", "draw"));
                nextCardAction.getChoices().add(new CardActionChoice("Set Aside", "aside"));
                game.setPlayerCardAction(player, nextCardAction);
            }
            game.refreshAllPlayersCardsPlayed();
        }
        else if (cardAction.getCardName().equals("Lost Village")) {
            if (choice.equals("aside")) {
                Card card = player.removeTopDeckCard();
                if (card != null) {
                    game.addHistory(player.getUsername(), " set aside ", KingdomUtil.getArticleWithCardName(card));
                    game.getSetAsideCards().add(card);
                    CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                    nextCardAction.setDeck(Card.DECK_FAIRYTALE);
                    nextCardAction.setCardName(cardAction.getCardName());
                    nextCardAction.setInstructions("Draw or set aside top card?");
                    nextCardAction.getChoices().add(new CardActionChoice("Draw", "draw"));
                    nextCardAction.getChoices().add(new CardActionChoice("Set Aside", "aside"));
                    game.setPlayerCardAction(player, nextCardAction);
                }
                else {
                    game.setPlayerInfoDialog(player, InfoDialog.getInfoDialog("Your deck is empty."));
                    game.addHistory(player.getUsername(), "'s deck is empty");
                    for (Card setAsideCard : game.getSetAsideCards()) {
                        player.addCardToDiscard(setAsideCard);
                    }
                    game.getSetAsideCards().clear();
                }
            }
            else if (choice.equals("draw")) {
                player.drawCards(1);
                game.addHistory(player.getUsername(), " drew the top card of ", player.getPronoun(), " deck");
                for (Card setAsideCard : game.getSetAsideCards()) {
                    player.addCardToDiscard(setAsideCard);
                }
                game.getSetAsideCards().clear();
            }
        }
        else if (cardAction.getCardName().equals("Magic Beans")) {
            Card card = cardAction.getCards().get(0);
            if (choice.equals("trash")) {
                game.addHistory(player.getUsername(), " chose to trash ", KingdomUtil.getCardWithBackgroundColor(card));
                game.removePlayedCard(card);
                game.getTrashedCards().add(card);
                game.playerLostCard(player, card);
            }
            else if (choice.equals("supply")) {
                game.addHistory(player.getUsername(), " chose to return ", KingdomUtil.getCardWithBackgroundColor(card), " to the supply");
                game.removePlayedCard(card);
                game.playerLostCard(player, card);
                game.addToSupply(card.getCardId());
            }

            CardAction gainCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS_FROM_SUPPLY);
            gainCardAction.setDeck(Card.DECK_FAIRYTALE);
            gainCardAction.setCardName(card.getName());
            gainCardAction.setButtonValue("Done");
            gainCardAction.setNumCards(1);
            gainCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
            for (Card c : game.getSupplyMap().values()) {
                if (game.getCardCost(c) <= 3 && !c.isCostIncludesPotion() && game.isCardInSupply(c)) {
                    gainCardAction.getCards().add(c);
                }
            }
            if (gainCardAction.getCards().size() > 0) {
                game.setPlayerCardAction(player, gainCardAction);
            }
        }
        else if (cardAction.getCardName().equals("Minion")) {
            if (choice.equals("coins")) {
                player.addCoins(2);
                game.addHistory(player.getUsername(), " chose +2 Coins");
            }
            else if (choice.equals("discard")) {
                game.addHistory(player.getUsername(), " chose to discard all players hands and have them draw 4 cards");
                for (Card c : player.getHand()) {
                    game.playerDiscardedCard(player, c);
                }
                player.discardHand();
                player.drawCards(4);
                for (Player p : players) {
                    if (p.getUserId() != player.getUserId()) {
                        if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(p.getUserId())) {
                            game.addHistory(p.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                        }
                        else if (!p.hasMoat() && !p.hasLighthouse() && p.getHand().size() >= 5) {
                            for (Card c : p.getHand()) {
                                game.playerDiscardedCard(p, c);
                            }
                            p.discardHand();
                            p.drawCards(4);
                        }
                        else {
                            if (p.hasLighthouse()) {
                                game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                            }
                            else if (p.hasMoat()) {
                                game.addHistory(p.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                            }
                            else {
                                game.addHistory(p.getUsername(), " had less than 5 cards");
                            }
                        }
                    }
                }
                game.refreshAllPlayersHand();
                game.refreshAllPlayersDiscard();
                game.refreshAllPlayersCardsBought();
            }
        }
        else if (cardAction.getCardName().equals("Mountebank")) {
            if (choice.equals("discard")) {
                player.discardCardFromHand(Card.CURSE_ID);
                game.addHistory(player.getUsername(), " discarded a ", KingdomUtil.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR)," card");
                game.refreshHandArea(player);
            }
            else if (choice.equals("gain")) {
                if (game.isCardInSupply(Card.CURSE_ID)) {
                    game.playerGainedCard(player, game.getCurseCard());
                }
                if (game.isCardInSupply(Card.COPPER_ID)) {
                    game.playerGainedCard(player, game.getCopperCard());
                }
                game.refreshDiscard(player);
            }
        }
        else if (cardAction.getCardName().equals("Native Village")) {
            if (choice.equals("card")) {
                Card topDeckCard = player.removeTopDeckCard();
                if (topDeckCard != null) {
                    player.getNativeVillageCards().add(topDeckCard);
                    game.addHistory(player.getUsername(), " added ", player.getPronoun(), " top deck card to ", player.getPronoun(), " ", KingdomUtil.getWordWithBackgroundColor("Native Village", Card.ACTION_COLOR));
                }
                else {
                    game.addHistory(player.getUsername(), " did not have any cards to draw");
                }
            }
            else if (choice.equals("hand")) {
                for (Card card : player.getNativeVillageCards()) {
                    player.addCardToHand(card);
                }
                player.getNativeVillageCards().clear();
                game.addHistory(player.getUsername(), " added ", KingdomUtil.getWordWithBackgroundColor("Native Village", Card.ACTION_COLOR), " cards to ", player.getPronoun(), " hand");
            }
        }
        else if (cardAction.getCardName().equals("Navigator")) {
            if (choice.equals("discard")) {
                player.getDiscard().addAll(cardAction.getCards());
                for (Card card : cardAction.getCards()) {
                    game.playerDiscardedCard(player, card);
                }
                game.addHistory(player.getUsername(), " chose to discard the cards");
            }
            else if (choice.equals("deck")) {
                CardAction sortCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                sortCardAction.setDeck(Card.DECK_SEASIDE);
                sortCardAction.setHideOnSelect(true);
                sortCardAction.setNumCards(cardAction.getCards().size());
                sortCardAction.setCardName("Navigator");
                sortCardAction.setCards(cardAction.getCards());
                sortCardAction.setButtonValue("Done");
                sortCardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                game.setPlayerCardAction(player, sortCardAction);
                game.addHistory(player.getUsername(), " chose to put the cards back on ", player.getPronoun(), " deck");
            }
        }
        else if (cardAction.getCardName().equals("Noble Brigand")) {
            Player nextPlayer = game.getPlayerMap().get(cardAction.getPlayerId());
            Card cardToTrash;
            Card cardToDiscard;
            if (choice.equals("silver")) {
                cardToTrash = game.getSilverCard();
                cardToDiscard = game.getGoldCard();
            }
            else {
                cardToTrash = game.getGoldCard();
                cardToDiscard = game.getSilverCard();
            }
            game.addHistory(player.getUsername(), " trashed ", nextPlayer.getUsername(), "'s ", KingdomUtil.getCardWithBackgroundColor(cardToTrash));
            game.playerGainedCard(player, cardToTrash);

            player.addCardToDiscard(cardToDiscard);
            game.playerDiscardedCard(nextPlayer, cardToDiscard);
            game.refreshDiscard(nextPlayer);
        }
        else if (cardAction.getCardName().equals("Nobles")) {
            if (choice.equals("cards")) {
                player.drawCards(3);
                game.addHistory(player.getUsername(), " chose +3 Cards");
            }
            else if (choice.equals("actions")) {
                player.addActions(2);
                game.addHistory(player.getUsername(), " chose +2 Actions");
                game.refreshAllPlayersCardsPlayed();
            }
        }
        else if (cardAction.getCardName().equals("Oracle")) {
            Player affectedPlayer = game.getPlayerMap().get(cardAction.getPlayerId());
            if (choice.equals("discard")) {
                game.getIncompleteCard().setPlayerActionCompleted(affectedPlayer.getUserId());
                if (game.isCurrentPlayer(affectedPlayer)) {
                    game.addHistory(player.getUsername(), " chose to discard the top cards of ", affectedPlayer.getPronoun(), " deck");
                }
                else {
                    game.addHistory(player.getUsername(), " chose to discard the top cards of ", affectedPlayer.getUsername(), "'s deck");
                }
                for (Card card : cardAction.getCards()) {
                    affectedPlayer.addCardToDiscard(card);
                    game.playerDiscardedCard(affectedPlayer, card);
                }
                game.refreshDiscard(affectedPlayer);
            }
            else if (choice.equals("back")) {
                if (game.isCurrentPlayer(affectedPlayer)) {
                    game.addHistory(player.getUsername(), " chose to put back the top cards of ", affectedPlayer.getPronoun(), " deck");
                }
                else {
                    game.addHistory(player.getUsername(), " chose to put back the top cards of ", affectedPlayer.getUsername(), "'s deck");
                }
                if (cardAction.getCards().size() == 1 || KingdomUtil.uniqueCardList(cardAction.getCards()).size() == 1) {
                    game.getIncompleteCard().setPlayerActionCompleted(affectedPlayer.getUserId());
                    for (Card card : cardAction.getCards()) {
                        affectedPlayer.addCardToTopOfDeck(card);
                    }
                }
                else {
                    CardAction reorderCardAction = new CardAction(CardAction.TYPE_CHOOSE_IN_ORDER);
                    reorderCardAction.setDeck(Card.DECK_HINTERLANDS);
                    reorderCardAction.setHideOnSelect(true);
                    reorderCardAction.setNumCards(2);
                    reorderCardAction.setCardName(cardAction.getCardName());
                    reorderCardAction.getCards().addAll(cardAction.getCards());
                    reorderCardAction.setButtonValue("Done");
                    reorderCardAction.setInstructions("Click the cards in the order you want them to be on the top of your deck, starting with the top card and then click Done. (The first card you click will be the top card of your deck)");
                    game.setPlayerCardAction(affectedPlayer, reorderCardAction);
                }
            }
            if (!player.isShowCardAction() && (!game.hasIncompleteCard() || game.getIncompleteCard().getExtraCardActions().isEmpty())) {
                player.drawCards(2);
            }
        }
        else if (cardAction.getCardName().equals("Pawn")) {
            if (choice.equals("cardAndAction")) {
                player.drawCards(1);
                player.addActions(1);
                game.addHistory(player.getUsername(), " chose +1 Card, +1 Action");
            }
            else if (choice.equals("cardAndBuy")) {
                player.drawCards(1);
                player.addBuys(1);
                game.addHistory(player.getUsername(), " chose +1 Card, +1 Buy");
            }
            else if (choice.equals("cardAndCoin")) {
                player.drawCards(1);
                player.addCoins(1);
                game.addHistory(player.getUsername(), " chose +1 Card, +1 Coin");
            }
            else if (choice.equals("actionAndBuy")) {
                player.addActions(1);
                player.addBuys(1);
                game.addHistory(player.getUsername(), " chose +1 Action, +1 Buy");
            }
            else if (choice.equals("actionAndCoin")) {
                player.addActions(1);
                player.addCoins(1);
                game.addHistory(player.getUsername(), " chose +1 Action, +1 Coin");
            }
            else if (choice.equals("buyAndCoin")) {
                player.addBuys(1);
                player.addCoins(1);
                game.addHistory(player.getUsername(), " chose +1 Buy, +1 Coin");
            }
            game.refreshAllPlayersPlayingArea();
        }
        else if (cardAction.getCardName().equals("Pirate Ship")) {
            if (choice.equals("attack")) {
                incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
                int nextPlayerIndex = game.getNextPlayerIndex();
                while (nextPlayerIndex != game.getCurrentPlayerIndex()) {
                    Player nextPlayer = players.get(nextPlayerIndex);
                    if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                        game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                    }
                    else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                        CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOOSE_CARDS);
                        nextCardAction.setDeck(Card.DECK_SEASIDE);
                        nextCardAction.setPlayerId(nextPlayer.getUserId());
                        nextCardAction.setCardName("Pirate Ship");
                        Card card1 = nextPlayer.removeTopDeckCard();
                        if (card1 != null) {
                            if (!card1.isTreasure()) {
                                card1.setDisableSelect(true);
                            }
                            nextCardAction.getCards().add(card1);
                            Card card2 = nextPlayer.removeTopDeckCard();
                            if(card2 != null) {
                                if (!card2.isTreasure()) {
                                    card2.setDisableSelect(true);
                                }
                                nextCardAction.getCards().add(card2);
                                String instructions = "These are the top two cards from " + nextPlayer.getUsername() + "'s deck.";
                                if ((card1 != null && card1.isTreasure()) || (card2 != null && card2.isTreasure())) {
                                    instructions += " Select a treasure card to trash and then click Done.";
                                    nextCardAction.setButtonValue("Done");
                                    nextCardAction.setNumCards(1);
                                }
                                else {
                                    instructions += " There are no treasure cards to trash. Click Continue.";
                                    nextCardAction.setButtonValue("Continue");
                                    nextCardAction.setNumCards(0);
                                }
                                nextCardAction.setInstructions(instructions);
                                incompleteCard.getExtraCardActions().add(nextCardAction);
                            }
                            else {
                                game.addHistory("The top card from ", nextPlayer.getUsername(), "'s deck was ", KingdomUtil.getArticleWithCardName(card1));
                                if (card1.isTreasure()) {
                                    game.addHistory(player.getUsername(), " trashed ", nextPlayer.getUsername(), "'s ", KingdomUtil.getWordWithBackgroundColor(card1.getName(), Card.TREASURE_COLOR));
                                    game.getTrashedCards().add(card1);
                                    game.playerLostCard(player, card1);
                                }
                            }
                        }
                        else {
                            game.addHistory(nextPlayer.getUsername(), " did not have any cards to draw");
                        }
                    }
                    else {
                        if (nextPlayer.hasLighthouse()) {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                        }
                        else {
                            game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                        }
                    }
                    if (nextPlayerIndex == players.size() - 1) {
                        nextPlayerIndex = 0;
                    }
                    else {
                        nextPlayerIndex++;
                    }
                }
                if (!incompleteCard.getExtraCardActions().isEmpty()) {
                    CardAction pirateAttackAction = incompleteCard.getExtraCardActions().remove();
                    game.setPlayerCardAction(player, pirateAttackAction);
                }
            }
            else if (choice.equals("coins")) {
                player.addCoins(player.getPirateShipCoins());
                game.addHistory(player.getUsername(), " used ", KingdomUtil.getPlural(player.getPirateShipCoins(), "Pirate Ship Coin"));
            }
        }
        else if (cardAction.getCardName().equals("Rancher")) {
            if (choice.equals("cattle")) {
                player.addCattleTokens(1);
                game.addHistory(player.getUsername(), " chose +1 cattle token");
            }
            else if (choice.equals("buy")) {
                player.addBuys(1);
                game.addHistory(player.getUsername(), " chose +1 Buy");
                game.refreshAllPlayersCardsBought();
            }
        }
        else if (cardAction.getCardName().equals("Sorceress")) {
            if (!choice.equals("none")) {
                boolean showTrashCardAction = false;
                if (choice.equals("cards")) {
                    player.drawCards(2);
                    game.addHistory(player.getUsername(), " chose to get +2 Cards");
                }
                else if (choice.equals("actions")) {
                    player.addActions(2);
                    game.refreshAllPlayersCardsPlayed();
                    game.addHistory(player.getUsername(), " chose to get +2 Actions");
                }
                else if (choice.equals("coins")) {
                    player.addCoins(2);
                    game.refreshAllPlayersCardsPlayed();
                    game.addHistory(player.getUsername(), " chose to get +2 Coins");
                }
                else if (choice.equals("buys")) {
                    player.addBuys(2);
                    game.refreshAllPlayersCardsBought();
                    game.addHistory(player.getUsername(), " chose to get +2 Buys");
                }
                else if (choice.equals("trash")) {
                    game.addHistory(player.getUsername(), " chose to trash 2 Cards");
                    if (player.getHand().size() == 1) {
                        game.getTrashedCards().add(player.getHand().get(0));
                        game.playerLostCard(player, player.getHand().get(0));
                        player.removeCardFromHand(player.getHand().get(0));
                        game.addHistory(player.getUsername()," trashed the last card in ", player.getPronoun(), " hand");
                    }
                    else if (player.getHand().size() >= 2) {
                        showTrashCardAction = true;
                        CardAction trashCardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                        trashCardAction.setDeck(Card.DECK_FAIRYTALE);
                        trashCardAction.setCardName(cardAction.getCardName());
                        trashCardAction.setButtonValue("Done");
                        trashCardAction.setNumCards(2);
                        trashCardAction.setInstructions("Select two cards to trash.");
                        trashCardAction.setCards(player.getHand());
                        trashCardAction.setPhase(cardAction.getPhase());
                        for (CardActionChoice cardActionChoice : cardAction.getChoices()) {
                            if (!cardActionChoice.getValue().equals(choice)) {
                                trashCardAction.getChoices().add(cardActionChoice);
                            }
                        }
                        if (cardAction.getPhase() == 1) {
                            trashCardAction.getChoices().add(new CardActionChoice("None", "none"));
                        }
                        game.setPlayerCardAction(player, trashCardAction);
                    }
                }

                int cursesRemaining = game.getSupply().get(Card.CURSE_ID);
                if (cursesRemaining > 0 && cardAction.getPhase() > 1) {
                    game.playerGainedCard(player, game.getCurseCard());
                }
                if (!showTrashCardAction && (cursesRemaining > 0 || cardAction.getPhase() == 1)) {

                    CardAction nextCardAction = new CardAction(CardAction.TYPE_CHOICES);
                    nextCardAction.setDeck(Card.DECK_FAIRYTALE);
                    nextCardAction.setCardName(cardAction.getCardName());

                    if (cursesRemaining == 0) {
                        nextCardAction.setInstructions("There are no curses remaining so you may only choose one more effect.");
                    }
                    else {
                        nextCardAction.setInstructions("Choose another effect to apply (you will gain a curse), or click None if you don't want to apply any more effects.");
                    }

                    for (CardActionChoice cardActionChoice : cardAction.getChoices()) {
                        if (!cardActionChoice.getValue().equals(choice)) {
                            nextCardAction.getChoices().add(cardActionChoice);
                        }
                    }
                    if (cardAction.getPhase() == 1) {
                        nextCardAction.getChoices().add(new CardActionChoice("None", "none"));
                    }
                    if (cardAction.getChoices().size() > 1) {
                        nextCardAction.setPhase(cardAction.getPhase()+1);
                        game.setPlayerCardAction(player, nextCardAction);
                    }
                }
            }
        }
        else if (cardAction.getCardName().equals("Spice Merchant")) {
            if (choice.equals("cards")) {
                player.drawCards(2);
                player.addActions(1);
                game.refreshAllPlayersCardsPlayed();
                game.addHistory(player.getUsername(), " chose +2 Cards and +1 Action");
            }
            else if (choice.equals("money")) {
                player.addCoins(2);
                player.addBuys(1);
                game.refreshAllPlayersCardsBought();
                game.addHistory(player.getUsername(), " chose +$2 and +1 Buy");
            }
        }
        else if (cardAction.getCardName().equals("Steward")) {
            if (choice.equals("cards")) {
                player.drawCards(2);
                game.addHistory(player.getUsername(), " chose +2 Cards");
            }
            else if (choice.equals("coins")) {
                player.addCoins(2);
                game.addHistory(player.getUsername(), " chose +2 Coins");
            }
            else if (choice.equals("trash")) {
                if (player.getHand().size() > 0) {
                    if (player.getHand().size() <= 2) {
                        List<Card> cards = new ArrayList<Card>(player.getHand());
                        for (Card card : cards) {
                            player.removeCardFromHand(card);
                            game.addHistory(player.getUsername(), " trashed ", KingdomUtil.getArticleWithCardName(card));
                        }
                    }
                    else {
                        CardAction trashCardsCardAction = new CardAction(CardAction.TYPE_TRASH_CARDS_FROM_HAND);
                        trashCardsCardAction.setDeck(Card.DECK_INTRIGUE);
                        trashCardsCardAction.setCardName("Steward");
                        trashCardsCardAction.setButtonValue("Done");
                        trashCardsCardAction.setNumCards(2);
                        trashCardsCardAction.setInstructions("Select two cards to trash.");
                        trashCardsCardAction.setCards(player.getHand());
                        game.setPlayerCardAction(player, trashCardsCardAction);
                    }
                }
            }
        }
        else if (cardAction.getCardName().equals("Trusty Steed")) {
            if (choice.equals("cardsAndActions")) {
                player.drawCards(2);
                player.addActions(2);
                game.addHistory(player.getUsername(), " chose +2 Cards, +2 Actions");
            }
            else if (choice.equals("cardsAndCoins")) {
                player.drawCards(2);
                player.addCoins(2);
                game.addHistory(player.getUsername(), " chose +2 Cards, +2 Coins");
            }
            else if (choice.equals("cardsAndSilvers")) {
                player.drawCards(2);
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                player.getDiscard().addAll(player.getDeck());
                player.getDeck().clear();
                game.addHistory(player.getUsername(), " chose +2 Cards, gain 4 Silvers and put your deck into your discard pile");
            }
            else if (choice.equals("actionsAndCoins")) {
                player.addActions(2);
                player.addCoins(2);
                game.addHistory(player.getUsername(), " chose +2 Actions, +2 Coins");
            }
            else if (choice.equals("actionsAndSilvers")) {
                player.addActions(2);
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                player.getDiscard().addAll(player.getDeck());
                player.getDeck().clear();
                game.addHistory(player.getUsername(), " chose +2 Actions, gain 4 Silvers and put your deck into your discard pile");
            }
            else if (choice.equals("coinsAndSilvers")) {
                player.addCoins(2);
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                game.playerGainedCard(player, game.getSilverCard());
                player.getDiscard().addAll(player.getDeck());
                player.getDeck().clear();
                game.addHistory(player.getUsername(), " chose +2 Coins, gain 4 Silvers and put your deck into your discard pile");
            }
            game.refreshAllPlayersPlayingArea();
        }
        else if (cardAction.getCardName().equals("Torturer")) {
            if (choice.equals("discard")) {
                int cardsToDiscard = 2;
                if (player.getHand().size() == 1) {
                    cardsToDiscard = 1;
                }
                if (player.getHand().size() > 0) {
                    CardAction discardCardsAction = new CardAction(CardAction.TYPE_DISCARD_FROM_HAND);
                    discardCardsAction.setDeck(Card.DECK_INTRIGUE);
                    discardCardsAction.setCardName("Torturer");
                    discardCardsAction.setButtonValue("Done");
                    discardCardsAction.setNumCards(cardsToDiscard);
                    discardCardsAction.setInstructions("Select two cards to discard.");
                    discardCardsAction.setCards(player.getHand());
                    game.setPlayerCardAction(player, discardCardsAction);
                }
            }
            else if (choice.equals("curse")) {
                game.addHistory(player.getUsername(), " chose to gain a ", KingdomUtil.getWordWithBackgroundColor("Curse", Card.CURSE_COLOR));
                if (game.isCardInSupply(Card.CURSE_ID)) {
                    game.playerGainedCardToHand(player, game.getCurseCard());
                    player.addCardToHand(game.getCurseCard());
                    game.refreshHand(player);
                }
            }
        }
        else if (cardAction.getCardName().equals("Tournament")) {
            if (choice.equals("prize")) {
                if (game.getPrizeCards().isEmpty()) {
                    game.addHistory(player.getUsername(), " chose to gain a Prize but there were no more available");
                }
                else if (game.getPrizeCards().size() == 1) {
                    game.playerGainedCardToTopOfDeck(player, game.getPrizeCards().get(0), false);
                    game.getPrizeCards().clear();
                }
                else {
                    CardAction choosePrizeCardAction = new CardAction(CardAction.TYPE_GAIN_CARDS);
                    choosePrizeCardAction.setDeck(Card.DECK_CORNUCOPIA);
                    choosePrizeCardAction.setCardName(cardAction.getCardName());
                    choosePrizeCardAction.setNumCards(1);
                    choosePrizeCardAction.setButtonValue("Done");
                    choosePrizeCardAction.setInstructions("Select one of the following cards to gain and then click Done.");
                    choosePrizeCardAction.getCards().addAll(game.getPrizeCards());
                    game.setPlayerCardAction(player, choosePrizeCardAction);
                }
            }
            else if (choice.equals("duchy")) {
                if (game.isCardInSupply(Card.DUCHY_ID)) {
                    game.playerGainedCardToTopOfDeck(player, game.getDuchyCard());
                    game.addHistory(player.getUsername(), " chose to gain a ", KingdomUtil.getCardWithBackgroundColor(game.getDuchyCard()));
                }
                else {
                    game.addHistory(player.getUsername(), " chose to gain a ", KingdomUtil.getCardWithBackgroundColor(game.getDuchyCard()), " but there were no more in the supply");
                }
            }
        }
        else if (cardAction.getCardName().equals("Trader")) {
            Card cardToGain = cardAction.getAssociatedCard();
            if (choice.equals("silver")) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.getWordWithBackgroundColor("Trader", Card.ACTION_REACTION_COLOR), " to gain ", KingdomUtil.getArticleWithCardName(game.getSilverCard()), " instead");
                if (game.getSupply().get(Card.SILVER_ID) > 0) {
                    game.playerGainedCard(player, game.getSilverCard());
                }
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
        else if (cardAction.getCardName().equals("Watchtower")) {
            Card cardToGain = cardAction.getCards().get(0);
            if (choice.equals("trash")) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.getWordWithBackgroundColor("Watchtower", Card.ACTION_REACTION_COLOR), " to trash ", KingdomUtil.getArticleWithCardName(cardToGain));
                game.moveGainedCard(player, cardToGain, "trash");
            }
            else if (choice.equals("deck")) {
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.getWordWithBackgroundColor("Watchtower", Card.ACTION_REACTION_COLOR), " to put ", KingdomUtil.getArticleWithCardName(cardToGain), " on top of ", player.getPronoun(), " deck");
                game.moveGainedCard(player, cardToGain, "deck");
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
        else if (cardAction.getCardName().equals("Young Witch")) {
            if (choice.equals("reveal")) {
                game.addHistory(player.getUsername(), " revealed a Bane card");
            }
            else if (choice.equals("curse")) {
                if (game.getSupply().get(Card.CURSE_ID) > 0) {
                    game.playerGainedCard(player, game.getCurseCard());
                }
            }
        }
        return incompleteCard;
    }
}
