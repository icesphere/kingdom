package com.kingdom.service;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.repository.CardRepository;
import com.kingdom.util.CardRandomizer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CardManager {

    private CardRepository cardRepository;
    private CardRandomizer cardRandomizer;

    public CardManager(CardRepository cardRepository, CardRandomizer cardRandomizer) {
        this.cardRepository = cardRepository;
        this.cardRandomizer = cardRandomizer;
    }

    public List<Card> getAllCards(boolean includeFanExpansionCards) {
        return cardRepository.findByFanExpansionCardAndDisabledAndPrizeCardOrderByNameAsc(includeFanExpansionCards, false, false);
    }

    public List<Card> getCards(String deck, boolean includeTesting) {
        return getCardsByDeck(deck, includeTesting);
    }

    public List<Card> getPrizeCards() {
        return cardRepository.findByPrizeCardOrderByNameAsc(true);
    }

    public Card getCard(int cardId) {
        return cardRepository.findOne(cardId);
    }

    public Card getCard(String cardName) {
        return cardRepository.findByName(cardName);
    }

    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    public void setRandomKingdomCards(Game game) {
        cardRandomizer.setRandomKingdomCards(game, game.getRandomizingOptions());
    }

    public void swapRandomCard(Game game, int cardId) {
        cardRandomizer.swapRandomCard(game, cardId);
    }

    public void swapForTypeOfCard(Game game, int cardId, String cardType) {
        cardRandomizer.swapCard(game, cardId, cardType);
    }

    public List<Card> getAvailableLeaderCards() {
        List<Card> cards = getCardsByDeck(Card.DECK_LEADERS, false);
        Collections.shuffle(cards);
        return cards.subList(0, 7);
    }

    private List<Card> getCardsByDeck(String deck, boolean includeTesting) {
        if (!includeTesting) {
            return cardRepository.findByDeckAndTestingAndDisabledAndPrizeCardOrderByNameAsc(deck, false, false, false);
        } else {
            return cardRepository.findByDeckAndDisabledAndPrizeCardOrderByNameAsc(deck, false, false);
        }
    }
}
