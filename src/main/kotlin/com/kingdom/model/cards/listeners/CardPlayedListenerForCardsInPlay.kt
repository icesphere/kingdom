package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface CardPlayedListenerForCardsInPlay {

    fun onCardPlayed(card: Card, player: Player)

}