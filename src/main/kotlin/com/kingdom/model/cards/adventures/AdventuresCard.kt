package com.kingdom.model.cards.adventures

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class AdventuresCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Adventures, type, cost)