package com.kingdom.util.cardaction;

import com.kingdom.model.*;
import com.kingdom.util.KingdomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseUpToHandler {
    public static IncompleteCard handleCardAction(Game game, Player player, CardAction cardAction, List<Integer> selectedCardIds) {

        IncompleteCard incompleteCard = null;

        Map<Integer, Card> cardMap = game.getCardMap();
        Map<Integer, Integer> supply = game.getSupply();
        List<Player> players = game.getPlayers();

        if (cardAction.getCardName().equals("Ambassador")) {
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                player.removeCardFromHand(card);
                game.playerLostCard(player, card);
                game.addToSupply(card.getCardId());
                game.addHistory(player.getUsername(), " added ", KingdomUtil.INSTANCE.getArticleWithCardName(card), " to the supply");
            }
            Card selectedCard = cardAction.getCards().get(0);
            int playerIndex = game.calculateNextPlayerIndex(game.getCurrentPlayerIndex());
            while (playerIndex != game.getCurrentPlayerIndex()) {
                Player nextPlayer = players.get(playerIndex);
                if (game.isCheckEnchantedPalace() && game.revealedEnchantedPalace(nextPlayer.getUserId())) {
                    game.addHistory(nextPlayer.getUsername(), " revealed an ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Enchanted Palace", Card.VICTORY_AND_REACTION_IMAGE));
                } else if (!nextPlayer.hasMoat() && !nextPlayer.hasLighthouse()) {
                    if (game.isCardInSupply(selectedCard)) {
                        game.playerGainedCard(nextPlayer, selectedCard);
                        game.refreshDiscard(nextPlayer);
                    }
                } else {
                    if (nextPlayer.hasLighthouse()) {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Lighthouse", Card.ACTION_DURATION_COLOR));
                    } else {
                        game.addHistory(nextPlayer.getUsername(), " had a ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("Moat", Card.ACTION_REACTION_COLOR));
                    }
                }
                playerIndex = game.calculateNextPlayerIndex(playerIndex);
            }
        } else if (cardAction.getCardName().equals("Inn")) {
            if (selectedCardIds.size() > 0) {
                List<Card> cards = new ArrayList<Card>();
                for (Integer selectedCardId : selectedCardIds) {
                    Card selectedCard = cardMap.get(selectedCardId);
                    player.getDiscard().remove(selectedCard);
                    cards.add(selectedCard);
                }
                player.getDeck().addAll(cards);
                player.shuffleDeck();
                game.refreshDiscard(player);
                game.addHistory(player.getUsername(), " shuffled ", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), " Action card"), " into ", player.getPronoun(), " deck");
            }
        } else if (cardAction.getCardName().equals("King's Court")) {
            if (selectedCardIds.size() > 0) {
                Card actionCard = player.getCardFromHandById(selectedCardIds.get(0));
                Card cardCopy;
                if (game.isCheckQuest() && actionCard.getName().equals("Quest")) {
                    cardCopy = new Card(actionCard);
                    game.setCopiedPlayedCard(true);
                } else {
                    cardCopy = actionCard;
                }
                RepeatedAction firstAction = new RepeatedAction(cardCopy);
                firstAction.setFirstAction(true);
                RepeatedAction extraAction = new RepeatedAction(cardCopy);
                game.getRepeatedActions().push(extraAction);
                game.getRepeatedActions().push(extraAction);
                game.getRepeatedActions().push(firstAction);
                if (actionCard.isDuration()) {
                    game.getDurationCardsPlayed().add(game.getKingsCourtCard());
                }
                game.addHistory(player.getUsername(), " used ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("King's Court", Card.ACTION_COLOR), " on ", KingdomUtil.INSTANCE.getArticleWithCardName(actionCard));
                game.playRepeatedAction(player, true);
            } else {
                game.addHistory(player.getUsername(), " chose not to play an action with ", KingdomUtil.INSTANCE.getWordWithBackgroundColor("King's Court", Card.ACTION_COLOR));
            }
        } else if (cardAction.getCardName().equals("Mendicant")) {
            if (selectedCardIds.size() > 0) {
                Card selectedCard = cardMap.get(selectedCardIds.get(0));
                game.getTrashedCards().remove(selectedCard);
                game.playerGainedCard(player, selectedCard, false);
            } else {
                game.addHistory(player.getUsername(), " chose to not gain a card from the trash pile");
            }
        } else if (cardAction.getCardName().equals("Museum")) {
            if (selectedCardIds.size() > 0) {
                Card selectedCard = cardMap.get(selectedCardIds.get(0));
                player.removeCardFromHand(selectedCard);
                player.getMuseumCards().add(selectedCard);
            }
            if (player.getMuseumCards().size() >= 4) {
                CardAction museumCardAction = new CardAction(CardAction.TYPE_YES_NO);
                museumCardAction.setDeck(Deck.Fan);
                museumCardAction.setCardName("Museum Trash Cards");
                museumCardAction.setInstructions("Do you want to trash 4 cards from your Museum mat to gain a Prize and a Duchy?");
                game.setPlayerCardAction(player, museumCardAction);
            }
        } else if (cardAction.getCardName().equals("Rancher")) {
            if (selectedCardIds.size() > 0) {
                Card selectedCard = cardMap.get(selectedCardIds.get(0));
                game.addHistory(player.getUsername(), " revealed ", KingdomUtil.INSTANCE.getArticleWithCardName(selectedCard));
                CardAction choicesCardAction = new CardAction(CardAction.TYPE_CHOICES);
                choicesCardAction.setDeck(Deck.Proletariat);
                choicesCardAction.setCardName(cardAction.getCardName());
                choicesCardAction.setInstructions("Choose one: +1 cattle token or +1 Buy.");
                choicesCardAction.getChoices().add(new CardActionChoice("cattle token", "cattle"));
                choicesCardAction.getChoices().add(new CardActionChoice("+1 Buy", "buy"));
                game.setPlayerCardAction(player, choicesCardAction);
            }
        } else if (cardAction.getCardName().equals("Storybook")) {
            if (selectedCardIds.size() > 0) {
                for (Integer selectedCardId : selectedCardIds) {
                    Card selectedCard = cardMap.get(selectedCardId);
                    player.removeCardFromHand(selectedCard);
                    cardAction.getAssociatedCard().getAssociatedCards().add(selectedCard);
                    player.addCoins(1);
                }
                game.addHistory(player.getUsername(), " added ", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "card"), " under ", KingdomUtil.INSTANCE.getCardWithBackgroundColor(cardAction.getAssociatedCard()), " and gained ", "+", KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "coin"));
            }
        } else if (cardAction.getCardName().equals("Treasury") || cardAction.getCardName().equals("Alchemist") || cardAction.getCardName().equals("Herbalist") || cardAction.getCardName().equals("Walled Village") || cardAction.getCardName().equals("Scheme")) {
            incompleteCard = new SinglePlayerIncompleteCard(cardAction.getCardName(), game);
            for (Integer selectedCardId : selectedCardIds) {
                Card card = cardMap.get(selectedCardId);
                game.removePlayedCard(card);
                player.addCardToTopOfDeck(card);
            }
            if (cardAction.getCardName().equals("Treasury") || cardAction.getCardName().equals("Alchemist") || cardAction.getCardName().equals("Walled Village")) {
                for (Card card : cardAction.getCards()) {
                    card.setAutoSelect(false);
                }
            }
            if (selectedCardIds.size() > 0) {
                String typeAdded;
                if (cardAction.getCardName().equals("Herbalist")) {
                    typeAdded = KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "Treasure Card");
                } else if (cardAction.getCardName().equals("Scheme")) {
                    typeAdded = KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), "Action Card");
                } else {
                    typeAdded = KingdomUtil.INSTANCE.getPlural(selectedCardIds.size(), cardAction.getCardName() + " card");
                }
                game.addHistory(player.getUsername(), " added ", typeAdded, " to the top of ", player.getPronoun(), " deck");
            }
            incompleteCard.setEndTurn(true);
        }

        return incompleteCard;
    }
}
