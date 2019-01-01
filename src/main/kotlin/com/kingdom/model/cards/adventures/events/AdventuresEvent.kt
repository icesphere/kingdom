package com.kingdom.model.cards.adventures.events

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event

abstract class AdventuresEvent(name: String, cost: Int, oncePerTurnEvent: Boolean = false) : Event(name, Deck.Adventures, cost, oncePerTurnEvent)