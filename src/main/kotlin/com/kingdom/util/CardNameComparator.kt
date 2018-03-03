package com.kingdom.util

import com.kingdom.model.cards.Card

import java.util.Comparator

class CardNameComparator : Comparator<Card> {
    override fun compare(c1: Card, c2: Card): Int {
        return if (c1.actionValue == c2.actionValue) {
            c1.name.compareTo(c2.name)
        } else {
            Integer.valueOf(c1.actionValue)!!.compareTo(c2.actionValue)
        }
    }
}
