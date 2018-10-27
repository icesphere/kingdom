package com.kingdom.model.cards.prosperity

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class ProsperityCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Prosperity, type, cost)