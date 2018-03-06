package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class KingdomCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Kingdom, type, cost)