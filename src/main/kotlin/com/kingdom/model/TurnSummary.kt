package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.util.groupedString
import java.util.ArrayList

@Suppress("unused")
class TurnSummary(val username: String) {

    var cardsGained: MutableList<Card> = ArrayList()

    val cardsGainedString
        get() = cardsGained.groupedString

    var cardsBought: MutableList<Card> = ArrayList()

    var eventsBought: MutableList<Event> = ArrayList()

    val eventsBoughtString
        get() = eventsBought.groupedString

    var gameTurn: Int = 0

    var trashedCards: MutableList<Card> = ArrayList()

    val cardsTrashedString
        get() = trashedCards.groupedString

    var cardsPlayed: MutableList<Card> = ArrayList()

    val cardsPlayedString
        get() = cardsPlayed.groupedString
}
