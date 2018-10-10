package com.kingdom.model.cards.listeners

import com.kingdom.model.cards.Card
import com.kingdom.model.players.Player

interface BeforeAttackListener {

    fun onBeforeAttack(card: Card, player: Player, opponent: Player)
}