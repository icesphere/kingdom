package com.kingdom.model.cards.menagerie.events

import com.kingdom.model.cards.Deck
import com.kingdom.model.cards.Event

abstract class MenagerieEvent(name: String, cost: Int, oncePerTurnEvent: Boolean = false) : Event(name, Deck.Menagerie, cost, oncePerTurnEvent)