package com.kingdom.model.cards.seaside

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class SeasideCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Seaside, type, cost)