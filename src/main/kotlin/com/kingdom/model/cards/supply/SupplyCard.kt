package com.kingdom.model.cards.supply

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class SupplyCard(name: String, type: CardType, cost: Int) : Card(name, Deck.None, type, cost)