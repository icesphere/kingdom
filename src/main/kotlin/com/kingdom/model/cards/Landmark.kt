package com.kingdom.model.cards

abstract class Landmark(name: String,
                        deck: Deck) : Card(name, deck, CardType.Landmark, 0)