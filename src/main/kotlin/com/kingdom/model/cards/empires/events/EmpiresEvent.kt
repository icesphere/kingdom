package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event

abstract class EmpiresEvent(name: String, cost: Int, oncePerTurnEvent: Boolean = false) : Event(name, Deck.Empires, cost, oncePerTurnEvent)