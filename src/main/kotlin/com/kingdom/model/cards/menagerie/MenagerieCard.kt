package com.kingdom.model.cards.menagerie

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class MenagerieCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Menagerie, type, cost)