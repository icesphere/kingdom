package com.kingdom.util;

import com.kingdom.model.Card;

import java.util.Comparator;

public class CardNameComparator implements Comparator<Card> {
    public int compare(Card c1, Card c2) {
        if (c1.getActionValue() == c2.getActionValue()) {
            return c1.getName().compareTo(c2.getName());
        } else {
            return Integer.valueOf(c1.getActionValue()).compareTo(c2.getActionValue());
        }
    }
}
