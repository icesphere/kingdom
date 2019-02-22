package com.kingdom.model.cards.empires.events

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event

abstract class EmpiresEvent(name: String, cost: Int, debtCost: Int = 0, oncePerTurnEvent: Boolean = false) : Event(name, Deck.Empires, cost, oncePerTurnEvent, debtCost)