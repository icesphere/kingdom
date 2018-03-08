package com.kingdom.model.cards.actions

import com.kingdom.model.cards.Card

class ActionResult {
    var selectedCard: Card? = null

    var choiceSelected: Int? = null

    var cardLocation: String? = null

    var isDoNotUse: Boolean = false

    var isDoneWithAction: Boolean = false
}