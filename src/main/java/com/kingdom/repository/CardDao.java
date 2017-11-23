package com.kingdom.repository;

import com.kingdom.model.Card;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardDao {

    private HibernateTemplate hibernateTemplate;

    public CardDao(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @SuppressWarnings({"unchecked"})
    public List<Card> getAllCards(boolean includeFanExpansionCards) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.addOrder(Order.asc("name"));
        if (!includeFanExpansionCards) {
            criteria.add(Restrictions.eq("fanExpansionCard", false));
        }
        criteria.add(Restrictions.eq("testing", false));
        criteria.add(Restrictions.eq("prizeCard", false));
        return (List<Card>) hibernateTemplate.findByCriteria(criteria);
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
        return (List<Card>) hibernateTemplate.findByCriteria(criteria);
    }

    @SuppressWarnings({"unchecked"})
    public List<Card> getPrizeCards() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.addOrder(Order.asc("name"));
        criteria.add(Restrictions.eq("prizeCard", true));
        return (List<Card>) hibernateTemplate.findByCriteria(criteria);
    }

    @SuppressWarnings({"unchecked"})
    public Card getCard(String cardName) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Card.class);
        criteria.add(Restrictions.eq("name", cardName));
        List<Card> cards = (List<Card>) hibernateTemplate.findByCriteria(criteria);
        if (cards.size() == 1) {
            return cards.get(0);
        }
        return null;
    }
}
