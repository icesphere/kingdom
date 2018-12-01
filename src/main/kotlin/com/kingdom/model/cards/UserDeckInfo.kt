package com.kingdom.model.cards

class UserDeckInfo(deck: Deck, val cards: List<Card>) {

    var deckName: String = deck.toString()

    var displayName: String

    init {
        displayName = when(deck) {
            Deck.Base -> "Base Set"
            Deck.DarkAges -> "Dark Ages"
            else -> deckName
        }
    }

}