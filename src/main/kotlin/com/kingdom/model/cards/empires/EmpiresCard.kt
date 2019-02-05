package com.kingdom.model.cards.empires

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class EmpiresCard(name: String, type: CardType, cost: Int, debtCost: Int = 0) : Card(name, Deck.Empires, type, cost, debtCost)