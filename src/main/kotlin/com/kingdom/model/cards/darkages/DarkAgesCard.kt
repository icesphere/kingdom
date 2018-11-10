package com.kingdom.model.cards.darkages

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class DarkAgesCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Cornucopia, type, cost)