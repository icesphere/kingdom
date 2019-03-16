package com.kingdom.model.cards.renaissance

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class RenaissanceCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Prosperity, type, cost)