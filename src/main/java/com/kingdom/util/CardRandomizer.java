package com.kingdom.util;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.RandomizingOptions;
import com.kingdom.repository.CardDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CardRandomizer {

    private CardDao dao;

    private RandomizingOptions options;
    private RandomCardsSelected rcs;

    private LinkedList<Card> selectedCards;

    private boolean cardSwapped;
    private boolean changingBaneCard;
    private boolean replacingCardWithSpecificType;

    public CardRandomizer(CardDao dao) {
        this.dao = dao;
    }

    public void setRandomKingdomCards(Game game, RandomizingOptions options) {
        game.setRandomizerReplacementCardNotFound(false);
        this.options = options;
        List<String> decks = game.getDecks();
        changingBaneCard = options.isSwappingCard() && options.getCardToReplaceIndex() == 10;
        replacingCardWithSpecificType = options.getCardTypeToReplaceWith() != null;

        selectedCards = new LinkedList<Card>();
        selectedCards.addAll(options.getCustomSelection());

        rcs = new RandomCardsSelected();

        List<Card> cards = new ArrayList<Card>();

        //special case where they only selected alchemy deck
        if (decks.size() == 1 && decks.get(0).equals(Card.DECK_ALCHEMY)) {
            cards = dao.getCards(Card.DECK_ALCHEMY, false);
            Collections.shuffle(cards);
            if (options.isSwappingCard()) {
                for (Card card : cards) {
                    if (canAddCard(card)) {
                        selectedCards.add(card);
                        break;
                    }
                }
            }
            else {
                selectedCards.addAll(cards.subList(0, 10 - options.getCustomSelection().size()));
            }
            game.setKingdomCards(cards.subList(0, 10));
            return;
        }

        boolean includeAlchemy = !options.isThreeToFiveAlchemy();
        if (options.isThreeToFiveAlchemy() && decks.contains(Card.DECK_ALCHEMY) && selectedCards.size() <= 5) {
            int rand = 1 + (int) (Math.random() * ((decks.size() - 1) + 1));
            if (rand == 1) {
                includeAlchemy = true;
            }
        }

        for (String deck : decks) {
            if (!options.isThreeToFiveAlchemy() || !deck.equals(Card.DECK_ALCHEMY)) {
                cards.addAll(dao.getCards(deck, false));
            }
        }
        Collections.shuffle(cards);

        if (includeAlchemy && options.isThreeToFiveAlchemy()) {
            int alchemyCardsToInclude = 3;
            List<Card> alchemyCards = dao.getCards(Card.DECK_ALCHEMY, false);
            Collections.shuffle(alchemyCards);
            if (alchemyCards.get(0).getCost() > 3) {
                alchemyCardsToInclude = 5;
            }
            else if (alchemyCards.get(0).getCost() > 2) {
                alchemyCardsToInclude = 4;
            }
            for (int i = 0; i < alchemyCardsToInclude; i++) {
                addSelectedCard(alchemyCards.get(i));
            }
        }

        if (options.isOneWithBuy() && !rcs.hasAdditionalBuys && needMoreCards()) {
            for (Card card : cards) {
                if (card.getAddBuys() > 0 && canAddCard(card)) {
                    addSelectedCard(card);
                    break;
                }
            }
        }

        if (options.isOneWithActions() && !rcs.hasAdditionalActions && needMoreCards()) {
            for (Card card : cards) {
                if (card.getAddActions() >= 2 && canAddCard(card)) {
                    addSelectedCard(card);
                    break;
                }
            }
        }

        if (options.isOneOfEachCost() && needMoreCards()) {
            for (Card card : cards) {
                if (!rcs.hasTwo && card.getCost() == 2) {
                    addSelectedCard(card);
                }
                else if (!rcs.hasThree && card.getCost() == 3) {
                    addSelectedCard(card);
                }
                else if (!rcs.hasFour && card.getCost() == 4) {
                    addSelectedCard(card);
                }
                else if (!rcs.hasFive && card.getCost() == 5) {
                    addSelectedCard(card);
                }

                if (!needMoreCards()) {
                    break;
                }
            }
        }

        if (needMoreCards()) {
            for (Card card : cards) {
                if (canAddCard(card)) {
                    addSelectedCard(card);
                }
                if (!needMoreCards()) {
                    break;
                }
            }
        }

        if (options.isDefenseForAttack() && rcs.hasAttackCard && !rcs.hasDefenseCard) {
            for (Card card : cards) {
                if (card.isDefense() && canAddCard(card)) {
                    selectedCards.removeLast();
                    addSelectedCard(card);
                    break;
                }
            }
        }

        if (!options.isSwappingCard()) {
            Collections.shuffle(selectedCards);
        }

        if (options.isSwappingCard() && !cardSwapped) {
            game.setRandomizerReplacementCardNotFound(true);
        }

        if (addBaneCard()) {
            for (Card card : cards) {
                if ((card.getCost() == 2 || card.getCost() == 3) && canAddCard(card)) {
                    selectedCards.add(card);
                    break;
                }
            }
            if (selectedCards.size() < 11) {
                Card baneCard = null;
                for (Card card : selectedCards) {
                    if(card.getCost() == 2 || card.getCost() == 3) {
                        baneCard = card;
                        break;
                    }
                }
                selectedCards.remove(baneCard);
                for (Card card : cards) {
                    if (canAddCard(card) && card.getCardId() != baneCard.getCardId()) {
                        selectedCards.add(card);
                    }
                    if (selectedCards.size() == 10) {
                        break;
                    }
                }
                selectedCards.add(baneCard);
            }
        }
        game.setKingdomCards(selectedCards);
    }

    private boolean addBaneCard() {
        if (selectedCards.size() == 11) {
            return false;
        }
        for (Card card : selectedCards) {
            if (card.getName().equals("Young Witch")) {
                return true;
            }
        }
        return false;
    }

    private boolean needMoreCards() {
        if (options.isSwappingCard()) {
            return !cardSwapped;
        }
        else {
            return selectedCards.size() < 10;
        }
    }

    private boolean addSelectedCard(Card card) {
        if (canAddCard(card)) {
            if (options.isSwappingCard()) {
                selectedCards.set(options.getCardToReplaceIndex(), card);
                cardSwapped = true;
                if (options.getCardToReplace().getName().equals("Young Witch") && selectedCards.size() > 10) {
                    selectedCards.remove(10);
                }
            }
            else {
                selectedCards.add(card);
            }
            randomCardSelected(card);
            return true;
        }
        return false;
    }

    private boolean canAddCard(Card card) {
        return !selectedCards.contains(card) && !options.getExcludedCards().contains(card)
                && (!changingBaneCard || card.getCost() == 2 || card.getCost() == 3)
                && (!replacingCardWithSpecificType || cardMatchesType(card, options.getCardTypeToReplaceWith()));
    }

    private boolean cardMatchesType(Card card, String type) {
        if (type.equals("extraBuy")) {
            return card.getAddBuys() > 0;
        }
        else if (type.equals("extraActions")) {
            return card.getAddActions() >= 2;
        }
        else if (type.equals("treasure")) {
            return card.isTreasure();
        }
        else if (type.equals("reaction")) {
            return card.isReaction();
        }
        else if (type.equals("attack")) {
            return card.isAttack();
        }
        else if (type.equals("trashingCard")) {
            return card.isTrashingCard();
        }
        return false;
    }

    private void randomCardSelected(Card card) {
        if (card.getAddBuys() > 0) {
            rcs.hasAdditionalBuys = true;
        }
        if (card.getAddActions() >= 2) {
            rcs.hasAdditionalActions = true;
        }
        if (card.isAttack()) {
            rcs.hasAttackCard = true;
        }
        if (card.isDefense()) {
            rcs.hasDefenseCard = true;
        }
        if (card.getCost() == 2 && !card.isCostIncludesPotion()) {
            rcs.hasTwo = true;
        }
        if (card.getCost() == 3 && !card.isCostIncludesPotion()) {
            rcs.hasThree = true;
        }
        if (card.getCost() == 4 && !card.isCostIncludesPotion()) {
            rcs.hasFour = true;
        }
        if (card.getCost() == 5 && !card.isCostIncludesPotion()) {
            rcs.hasFive = true;
        }
    }

    public void swapRandomCard(Game game, int cardId) {
        swapCard(game, cardId, null);
    }

    public void swapCard(Game game, int cardId, String cardType) {
        int cardToReplaceIndex = 0;
        Card cardToReplace = null;
        List<Card> cards = game.getKingdomCards();
        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i).getCardId() == cardId) {
                cardToReplace = cards.get(i);
                cardToReplaceIndex = i;
                break;
            }
        }
        RandomizingOptions swapOptions = new RandomizingOptions();
        swapOptions.setCardTypeToReplaceWith(cardType);
        swapOptions.setSwappingCard(true);
        swapOptions.setCardToReplace(cardToReplace);
        swapOptions.setCardToReplaceIndex(cardToReplaceIndex);
        swapOptions.setCustomSelection(cards);
        swapOptions.getExcludedCards().add(cardToReplace);
        setRandomKingdomCards(game, swapOptions);
    }

    private class RandomCardsSelected {
        boolean hasTwo = false;
        boolean hasThree = false;
        boolean hasFour = false;
        boolean hasFive = false;
        boolean hasAdditionalBuys = false;
        boolean hasAdditionalActions = false;
        boolean hasDefenseCard = false;
        boolean hasAttackCard = false;
    }
}
