package com.kingdom.repository;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.util.CardRandomizer;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class CardDao extends HibernateDaoSupport {

    @SuppressWarnings({"unchecked"})
    public List<Card> getAllCards(boolean includeFanExpansionCards) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.addOrder(Order.asc("name"));
        if (!includeFanExpansionCards) {
            criteria.add(Restrictions.eq("fanExpansionCard", false));
        }
        criteria.add(Restrictions.eq("testing", false));
        criteria.add(Restrictions.eq("prizeCard", false));
        return (List<Card>) getHibernateTemplate().findByCriteria(criteria);
    }

    @SuppressWarnings({"unchecked"})
    public List<Card> getCards(String deck, boolean includeTesting) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.add(Restrictions.eq("deck", deck));
        criteria.addOrder(Order.asc("name"));
        if (!includeTesting) {
            criteria.add(Restrictions.eq("testing", false));
            criteria.add(Restrictions.eq("disabled", false));
        }
        criteria.add(Restrictions.eq("prizeCard", false));
        return (List<Card>) getHibernateTemplate().findByCriteria(criteria);
    }

    @SuppressWarnings({"unchecked"})
    public List<Card> getPrizeCards() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.addOrder(Order.asc("name"));
        criteria.add(Restrictions.eq("prizeCard", true));
        return (List<Card>) getHibernateTemplate().findByCriteria(criteria);
    }

    public Card getCard(int cardId){
        return getHibernateTemplate().get(Card.class, cardId);
    }

    @SuppressWarnings({"unchecked"})
    public Card getCard(String cardName) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.add(Restrictions.eq("name", cardName));
        List<Card> cards = (List<Card>) getHibernateTemplate().findByCriteria(criteria);
        if (cards.size() == 1) {
            return cards.get(0);
        }
        return null;
    }

    public void saveCard(Card card){
        getHibernateTemplate().saveOrUpdate(card);
    }

    public void setRandomKingdomCards(Game game) {
        CardRandomizer randomizer = new CardRandomizer(this);
        randomizer.setRandomKingdomCards(game, game.getRandomizingOptions());
    }

    public void swapRandomCard(Game game, int cardId) {
        CardRandomizer randomizer = new CardRandomizer(this);
        randomizer.swapRandomCard(game, cardId);
    }

    public void swapForTypeOfCard(Game game, int cardId, String cardType) {
        CardRandomizer randomizer = new CardRandomizer(this);
        randomizer.swapCard(game, cardId, cardType);
    }
}
