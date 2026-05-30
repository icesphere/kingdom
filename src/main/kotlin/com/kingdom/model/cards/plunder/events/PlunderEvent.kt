package com.kingdom.model.cards.plunder.events

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event

abstract class PlunderEvent(name: String, cost: Int, oncePerTurnEvent: Boolean = false) : Event(name, Deck.Plunder, cost, oncePerTurnEvent)
