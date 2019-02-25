package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.Event
import com.kingdom.model.cards.Landmark
import java.util.ArrayList

class RandomizingOptions {
    var isOneOfEachCost: Boolean = false
    var isOneWithBuy: Boolean = false
    var isOneWithActions: Boolean = false
    var isDefenseForAttack: Boolean = false
    var customCardSelection: List<Card> = ArrayList(0)
    var numEventsAndLandmarks: Int = 2
    var customEventSelection: List<Event> = ArrayList(0)
    var customLandmarkSelection: List<Landmark> = ArrayList(0)
    var excludedCards: List<Card> = ArrayList(0)
    var isSwappingCard: Boolean = false
    var cardToReplaceIndex: Int = 0
    var cardToReplace: Card? = null
}
