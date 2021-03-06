package com.kingdom.model.cards.base

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class BaseCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Base, type, cost)