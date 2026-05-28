package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface AfterOtherPlayerCardPlayedListenerForCardsInPlay {

    fun afterCardPlayedByOtherPlayer(card: Card, player: Player, otherPlayer: Player)
}
