package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class HinterlandsCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Hinterlands, type, cost)