package com.kingdom.model.cards.kingdom

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType

class Bureaucrat : Card() {
    init {
        name = "Bureaucrat"
        type = CardType.ActionAttack
        cost = 4
        special = "Gain a silver card; put it on top of your deck. Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no Victory cards)."
    }
}

