package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface CardBoughtListenerForCardsInPlay {

    //returns true if this handles buying the card
    fun onCardBought(card: Card, player: Player): Boolean

}