package com.kingdom.model.cards.intrigue

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class IntrigueCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Intrigue, type, cost)