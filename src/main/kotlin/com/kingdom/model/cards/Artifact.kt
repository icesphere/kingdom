package com.kingdom.model.cards

abstract class Artifact(name: String,
                        deck: Deck) : Card(name, deck, CardType.Artifact, 0, 0) {

    var owner: String? = null
}