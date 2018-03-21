package com.kingdom.model

import com.kingdom.model.cards.Card
import java.util.ArrayList

class RandomizingOptions {
    var isOneOfEachCost: Boolean = false
    var isOneWithBuy: Boolean = false
    var isOneWithActions: Boolean = false
    var isDefenseForAttack: Boolean = false
    var customSelection: List<Card> = ArrayList(0)
    var excludedCards: List<Card> = ArrayList(0)
    var isSwappingCard: Boolean = false
    var cardToReplaceIndex: Int = 0
    var cardToReplace: Card? = null
    var cardTypeToReplaceWith: String? = null
}
