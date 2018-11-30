package com.kingdom.model.cards.guilds

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.Deck

abstract class GuildsCard(name: String, type: CardType, cost: Int) : Card(name, Deck.Guilds, type, cost)