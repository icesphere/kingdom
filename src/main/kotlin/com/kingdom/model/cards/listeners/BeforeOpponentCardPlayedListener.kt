package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface BeforeOpponentCardPlayedListener {

    fun onBeforeOpponentCardPlayed(card: Card, player: Player, opponent: Player)
}