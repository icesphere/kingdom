package com.kingdom.service;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.repository.CardDao;
import com.kingdom.repository.CardRepository;
import com.kingdom.util.CardRandomizer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CardManager {

    private CardDao dao;
    private CardRepository cardRepository;
    private CardRandomizer cardRandomizer;

    public CardManager(CardDao dao, CardRepository cardRepository, CardRandomizer cardRandomizer) {
        this.dao = dao;
        this.cardRepository = cardRepository;
        this.cardRandomizer = cardRandomizer;
    }

    public List<Card> getAllCards(boolean includeFanExpansionCards) {
        return dao.getAllCards(includeFanExpansionCards);
    }

    public List<Card> getCards(String deck, boolean includeTesting) {
        return dao.getCards(deck, includeTesting);
    }

    public List<Card> getPrizeCards() {
        return dao.getPrizeCards();
    }

    public Card getCard(int cardId) {
        return cardRepository.findOne(cardId);
    }

    public Card getCard(String cardName) {
        return dao.getCard(cardName);
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
        List<Card> cards = dao.getCards(Card.DECK_LEADERS, false);
        Collections.shuffle(cards);
        return cards.subList(0, 7);
    }
}
