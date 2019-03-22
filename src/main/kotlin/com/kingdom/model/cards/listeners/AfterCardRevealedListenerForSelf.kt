package com.kingdom.model.cards.listeners

import com.kingdom.model.players.Player

interface AfterCardRevealedListenerForSelf {

    fun afterCardRevealed(player: Player)

}