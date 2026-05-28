package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface AfterOtherPlayerCardGainedListenerForCardsInHand {

    fun afterCardGainedByOtherPlayer(card: Card, player: Player, otherPlayer: Player)
}
