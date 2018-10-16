package com.kingdom.util

import com.kingdom.model.cards.Card

val List<Card>.groupedString: String
    get() = KingdomUtil.groupCards(this.toMutableList(), true)