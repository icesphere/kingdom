package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface AfterCardBoughtListenerForCardsInPlay {

    fun afterCardBought(card: Card, player: Player)

}