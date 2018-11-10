package com.kingdom.model

import com.kingdom.model.cards.Card
import java.util.ArrayList

class TurnSummary {
    var cardsGained: MutableList<Card> = ArrayList()

    var cardsBought: MutableList<Card> = ArrayList()

    var gameTurn: Int = 0

    var trashedCards: MutableList<Card> = ArrayList()

    var cardsPlayed: MutableList<Card> = ArrayList()
}
