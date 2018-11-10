package com.kingdom.model.cards

import com.kingdom.model.User

class UserDeckInfo(user: User, deck: Deck, val cards: List<Card>) {

    var deckName: String = deck.toString()

    var displayName: String
    
    var deckChecked: Boolean

    var deckWeight: Int

    init {
        displayName = when(deck) {
            Deck.Base -> "Base Set"
            Deck.DarkAges -> "Dark Ages"
            Deck.Promo -> "Promo Cards"
            else -> deckName
        }

        deckChecked = when(deck) {
            Deck.Base -> user.baseChecked
            Deck.Cornucopia -> user.cornucopiaChecked
            Deck.DarkAges -> user.darkAgesChecked
            Deck.Hinterlands -> user.hinterlandsChecked
            Deck.Intrigue -> user.intrigueChecked
            Deck.Promo -> user.promoChecked
            Deck.Prosperity -> user.prosperityChecked
            Deck.Seaside -> user.seasideChecked
            else -> true
        }

        deckWeight = when(deck) {
            Deck.Base -> user.baseWeight
            Deck.Cornucopia -> user.cornucopiaWeight
            Deck.DarkAges -> user.darkAgesWeight
            Deck.Hinterlands -> user.hinterlandsWeight
            Deck.Intrigue -> user.intrigueWeight
            Deck.Promo -> user.promoWeight
            Deck.Prosperity -> user.prosperityWeight
            Deck.Seaside -> user.seasideWeight
            else -> 3
        }
    }

}