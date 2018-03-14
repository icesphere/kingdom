package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation

class ActionResult {
    var selectedCard: Card? = null

    var choiceSelected: Int? = null

    var cardLocation: CardLocation? = null

    var isDoNotUse: Boolean = false

    var isDoneWithAction: Boolean = false
}