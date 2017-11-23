package com.kingdom.service;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.repository.CardDao;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CardManager {

    CardDao dao;

    public CardManager(CardDao dao) {
        this.dao = dao;
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

    public Card getCard(int cardId){
        return dao.getCard(cardId);
    }

    public Card getCard(String cardName){
        return dao.getCard(cardName);
    }

    public void saveCard(Card card){
        dao.saveCard(card);
    }

    public void setCardDao(CardDao cardDao) {
        this.dao = cardDao;
    }

    public void setRandomKingdomCards(Game game){
        dao.setRandomKingdomCards(game);
    }

    public void swapRandomCard(Game game, int cardId) {
        dao.swapRandomCard(game, cardId);
    }

    public void swapForTypeOfCard(Game game, int cardId, String cardType) {
        dao.swapForTypeOfCard(game, cardId, cardType);
    }

    public List<Card> getAvailableLeaderCards() {
        List<Card> cards = dao.getCards(Card.DECK_LEADERS, false);
        Collections.shuffle(cards);
        return cards.subList(0, 7);
    }
}
