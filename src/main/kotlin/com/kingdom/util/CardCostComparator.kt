package com.kingdom.util

import com.kingdom.model.cards.Card

import java.util.Comparator

class CardCostComparator : Comparator<Card> {
    override fun compare(c1: Card, c2: Card): Int {
        var cost1 = c1.cost
        if (c1.costIncludesPotion) {
            cost1 += 2
        }
        var cost2 = c2.cost
        if (c2.costIncludesPotion) {
            cost2 += 2
        }

        return when {
            cost1 > cost2 -> 1
            cost1 < cost2 -> -1
            else -> 0
        }
    }
}
