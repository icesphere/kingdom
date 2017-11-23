package com.kingdom.util;

import com.kingdom.model.Card;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Aug 18, 2010
 * Time: 7:10:29 AM
 */
public class CardCostComparator implements Comparator<Card> {
    public int compare(Card c1, Card c2) {
        int cost1 = c1.getCost();
        if (c1.isCostIncludesPotion()) {
            cost1 += 2;
        }
        int cost2 = c2.getCost();
        if (c2.isCostIncludesPotion()) {
            cost2 += 2;
        }

        if (cost1 > cost2) {
            return +1;
        }
        else if (cost1 < cost2) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
