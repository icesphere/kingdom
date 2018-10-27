package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface CardGainedListenerForCardsInPlay {

    //returns true if this handles gaining the card
    fun onCardGained(card: Card, player: Player): Boolean

}