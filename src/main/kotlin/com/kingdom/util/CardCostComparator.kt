package com.kingdom.util

import com.kingdom.model.cards.Card

import java.util.Comparator

class CardCostComparator : Comparator<Card> {
    override fun compare(c1: Card, c2: Card): Int {
        val cost1 = c1.cost
        val cost2 = c2.cost

        return when {
            cost1 > cost2 -> 1
            cost1 < cost2 -> -1
            else -> 0
        }
    }
}
